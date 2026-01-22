package com.tafu.bazi.service.impl;

import com.tafu.bazi.entity.Admin;
import com.tafu.bazi.exception.BusinessException;
import com.tafu.bazi.exception.StandardErrorCode;
import com.tafu.bazi.repository.*;
import com.tafu.bazi.service.AdminService;
import com.tafu.bazi.utils.JwtUtil;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AdminServiceImpl
 *
 * <p>描述: 管理员业务逻辑实现。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

  private final AdminRepository adminRepository;
  private final UserRepository userRepository;
  private final PaymentOrderRepository orderRepository;
  private final FortuneReportRepository reportRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  @Override
  public String login(String username, String password) {
    Admin admin =
        adminRepository
            .findByUsername(username)
            .orElseThrow(
                () -> new BusinessException(StandardErrorCode.AUTH_FAILED.getCode(), "管理员不存在"));

    if (!passwordEncoder.matches(password, admin.getPasswordHash())) {
      throw new BusinessException(StandardErrorCode.AUTH_FAILED.getCode(), "密码错误");
    }

    // Use a distinct secret or role in claim for admin if needed
    return jwtUtil.generateToken(admin.getId());
  }

  @Override
  public Admin createAdmin(String username, String password, String role) {
    if (adminRepository.findByUsername(username).isPresent()) {
      throw new BusinessException(StandardErrorCode.PARAM_ERROR.getCode(), "管理员已存在");
    }
    Admin admin = new Admin();
    admin.setUsername(username);
    admin.setPasswordHash(passwordEncoder.encode(password));
    admin.setRole(role);
    return adminRepository.save(admin);
  }

  @Override
  public Map<String, Object> getDashboardStats() {
    Map<String, Object> stats = new HashMap<>();
    stats.put("totalUsers", userRepository.count());
    stats.put("totalOrders", orderRepository.count());
    stats.put("totalReports", reportRepository.count());
    // Add revenue calc if needed
    return stats;
  }
}
