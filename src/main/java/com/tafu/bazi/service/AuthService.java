package com.tafu.bazi.service;

import com.tafu.bazi.dto.request.AuthRequest;
import com.tafu.bazi.dto.response.AuthResponse;

/**
 * AuthService Interface
 *
 * <p>描述: 认证业务逻辑接口。
 *
 * <p>包含内容: 1. 手机号登录 (loginWithPhone) 2. 密码登录 (loginWithPassword) 3. 注册 (register) 4. 发送验证码
 * (sendCode)
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
public interface AuthService {

  AuthResponse loginWithPhone(AuthRequest.PhoneLogin request);

  AuthResponse loginWithPassword(AuthRequest.PasswordLogin request);

  AuthResponse register(AuthRequest.Register request);

  void sendCode(String phone);
}
