package com.tafu.bazi.controller.admin;

import com.tafu.bazi.dto.request.AuthRequest;
import com.tafu.bazi.dto.response.ApiResponse;
import com.tafu.bazi.service.AdminService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * AdminController
 *
 * <p>描述: 管理后台控制器 (登录、统计等)。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

  private final AdminService adminService;

  @PostMapping("/login")
  public ApiResponse<Map<String, String>> login(@RequestBody AuthRequest.PhoneLogin request) {
    String token = adminService.login(request.getPhone(), request.getCode());
    // Reuse LoginRequest but treat phone as username, code as password for simplicity or Create
    // AdminLoginRequest
    // Better: create AdminLoginDto
    return ApiResponse.success(Map.of("token", token));
  }

  @GetMapping("/dashboard")
  public ApiResponse<Map<String, Object>> getDashboard() {
    return ApiResponse.success(adminService.getDashboardStats());
  }
}
