package com.tafu.bazi.service.impl;

import com.tafu.bazi.dto.request.AuthRequest;
import com.tafu.bazi.dto.response.AuthResponse;
import com.tafu.bazi.entity.User;
import com.tafu.bazi.entity.VerificationCode;
import com.tafu.bazi.exception.BusinessException;
import com.tafu.bazi.exception.StandardErrorCode;
import com.tafu.bazi.repository.UserRepository;
import com.tafu.bazi.repository.VerificationCodeRepository;
import com.tafu.bazi.service.AuthService;
import com.tafu.bazi.utils.JwtUtil;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthServiceImpl
 *
 * <p>描述: 认证业务逻辑实现。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AuthServiceImpl implements AuthService {

  private static final int INITIAL_GIFT_POINTS = 50;

  private final UserRepository userRepository;
  private final VerificationCodeRepository verificationCodeRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final com.tafu.bazi.service.PointsService pointsService;

  @Override
  @Transactional
  public AuthResponse loginWithPhone(AuthRequest.PhoneLogin request) {
    // 1. 验证验证码
    verifyCode(request.getPhone(), request.getCode());

    // 2. 查找或创建用户
    boolean[] isNewUserRef = {false};
    User user =
        userRepository
            .findByPhone(request.getPhone())
            .orElseGet(
                () -> {
                  isNewUserRef[0] = true;
                  return registerUserByPhone(request.getPhone());
                });

    // 3. 处理新用户积分
    if (isNewUserRef[0]) {
      pointsService.ensureAccountExists(user.getId());
      pointsService.addPoints(user.getId(), INITIAL_GIFT_POINTS, "gift", "注册赠送积分");
    }

    // 4. 生成 Token
    String token = generateToken(user);

    return buildAuthResponse(user, token, isNewUserRef[0]);
  }

  @Override
  public AuthResponse loginWithPassword(AuthRequest.PasswordLogin request) {
    User user =
        userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(() -> new BusinessException(StandardErrorCode.AUTH_FAILED));

    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
      throw new BusinessException(StandardErrorCode.AUTH_FAILED);
    }

    String token = generateToken(user);
    return buildAuthResponse(user, token, false);
  }

  @Override
  @Transactional
  public AuthResponse register(AuthRequest.Register request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new BusinessException(StandardErrorCode.PARAM_ERROR.getCode(), "用户名已存在");
    }

    User user =
        User.builder()
            .username(request.getUsername())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    userRepository.save(user);

    // 初始化积分
    pointsService.ensureAccountExists(user.getId());
    // 如果用户名注册也送积分，可以在这里加 addPoints logic.
    // 参照 Node phone login logic, assume yes or keep consistent.
    // Node registerWithPassword implementation wasn't fully visible but Phone login
    // was the primary.
    // Usually new user gets points.
    pointsService.addPoints(user.getId(), INITIAL_GIFT_POINTS, "gift", "注册赠送积分");

    String token = generateToken(user);
    return buildAuthResponse(user, token, true);
  }

  @Override
  @Transactional
  public void sendCode(String phone) {
    // 生成 6 位验证码
    String code = String.valueOf(new Random().nextInt(899999) + 100000);

    // 保存到数据库
    VerificationCode vc =
        VerificationCode.builder()
            .phone(phone)
            .code(code)
            .expiresAt(LocalDateTime.now().plusMinutes(5))
            .used(false)
            .attempts(0)
            .build();

    verificationCodeRepository.save(vc);

    // TODO: 对接真实短信服务 (阿里云/腾讯云)
    log.info("Mock SMS sent to {}: {}", phone, code);
  }

  @Override
  public AuthResponse getMe(String userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BusinessException(StandardErrorCode.USER_NOT_FOUND));

    // Ensure specific fields like Points are up to date if needed,
    // although PointsService keeps them in sync.

    return buildAuthResponse(user, null, false);
  }

  private void verifyCode(String phone, String code) {
    // 开发环境后门：888888 直接通过
    if ("888888".equals(code)) {
      return;
    }

    VerificationCode vc =
        verificationCodeRepository
            .findFirstByPhoneAndUsedFalseOrderByCreatedAtDesc(phone)
            .orElseThrow(() -> new BusinessException(StandardErrorCode.VERIFY_CODE_ERROR));

    if (vc.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new BusinessException(StandardErrorCode.VERIFY_CODE_ERROR);
    }

    if (!vc.getCode().equals(code)) {
      throw new BusinessException(StandardErrorCode.VERIFY_CODE_ERROR);
    }

    // 标记为已使用
    vc.setUsed(true);
    verificationCodeRepository.save(vc);
  }

  private User registerUserByPhone(String phone) {
    User user =
        User.builder()
            .phone(phone)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    return userRepository.save(user);
  }

  private String generateToken(User user) {
    return jwtUtil.generateToken(
        new org.springframework.security.core.userdetails.User(
            user.getId(), // ID as standard UserDetails username
            "",
            Collections.emptyList()));
  }

  private AuthResponse buildAuthResponse(User user, String token, boolean isNew) {

    // Fetch points balance safely
    int points = 0;
    try {
      points = pointsService.getMyPoints(user.getId()).getBalance();
    } catch (Exception e) {
      log.warn("Failed to fetch points for user {}: {}", user.getId(), e.getMessage());
    }

    return AuthResponse.builder()
        .token(token)
        .user(
            AuthResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .pointsBalance(points)
                .isNewUser(isNew)
                .build())
        .build();
  }
}
