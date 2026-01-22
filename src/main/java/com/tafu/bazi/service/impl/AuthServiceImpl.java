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

  private final UserRepository userRepository;
  private final VerificationCodeRepository verificationCodeRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  @Override
  @Transactional
  public AuthResponse loginWithPhone(AuthRequest.PhoneLogin request) {
    // 1. 验证验证码
    verifyCode(request.getPhone(), request.getCode());

    // 2. 查找或创建用户
    User user =
        userRepository
            .findByPhone(request.getPhone())
            .orElseGet(() -> registerUserByPhone(request.getPhone()));

    // 3. 生成 Token
    String token = generateToken(user);

    return buildAuthResponse(
        user, token, true); // Assuming always new session, functionality refined below
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
      // Use 400 for duplicate user
      throw new BusinessException(StandardErrorCode.PARAM_ERROR.getCode(), "用户名已存在");
    }

    User user =
        User.builder()
            .username(request.getUsername())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .build();

    userRepository.save(user);

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
      // 增加尝试次数逻辑 ...
      throw new BusinessException(StandardErrorCode.VERIFY_CODE_ERROR);
    }

    // 标记为已使用
    vc.setUsed(true);
    verificationCodeRepository.save(vc);
  }

  private User registerUserByPhone(String phone) {
    User user = User.builder().phone(phone).build();
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
    return AuthResponse.builder()
        .token(token)
        .user(
            AuthResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .isNewUser(isNew)
                .build())
        .build();
  }
}
