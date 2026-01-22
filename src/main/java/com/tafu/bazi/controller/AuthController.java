package com.tafu.bazi.controller;

import com.tafu.bazi.dto.request.AuthRequest;
import com.tafu.bazi.dto.response.ApiResponse;
import com.tafu.bazi.dto.response.AuthResponse;
import com.tafu.bazi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthController
 *
 * <p>描述: 认证相关 API 接口。
 *
 * <p>包含内容: 1. 手机号登录接口 2. 密码登录接口 3. 注册接口 4. 发送验证码接口
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login/phone")
  public ApiResponse<AuthResponse> loginWithPhone(
      @RequestBody @Valid AuthRequest.PhoneLogin request) {
    return ApiResponse.success(authService.loginWithPhone(request));
  }

  @PostMapping("/login/password")
  public ApiResponse<AuthResponse> loginWithPassword(
      @RequestBody @Valid AuthRequest.PasswordLogin request) {
    return ApiResponse.success(authService.loginWithPassword(request));
  }

  @PostMapping("/register")
  public ApiResponse<AuthResponse> register(@RequestBody @Valid AuthRequest.Register request) {
    return ApiResponse.success(authService.register(request));
  }

  @PostMapping("/send-code")
  public ApiResponse<Void> sendCode(@RequestBody @Valid AuthRequest.SendCode request) {
    authService.sendCode(request.getPhone());
    return ApiResponse.success();
  }
}
