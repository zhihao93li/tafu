package com.tafu.bazi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Auth Request DTOs
 *
 * <p>包含多个静态内部类，用于认证相关的请求参数。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
public class AuthRequest {

  @Data
  public static class PhoneLogin {
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "验证码不能为空")
    private String code;
  }

  @Data
  public static class PasswordLogin {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
  }

  @Data
  public static class Register {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
  }

  @Data
  public static class SendCode {
    @NotBlank(message = "手机号不能为空")
    private String phone;
  }
}
