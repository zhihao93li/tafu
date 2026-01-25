package com.tafu.bazi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * StandardErrorCode
 *
 * <p>描述: 标准业务错误码实现。 格式: [2位模块码][3位错误码]
 *
 * <p>包含内容: 1. 通用错误 (10xxx) 2. 认证错误 (20xxx) 3. 业务错误 (30xxx)
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Getter
@AllArgsConstructor
public enum StandardErrorCode implements ErrorCode {

  // 通用错误
  SUCCESS(200, "操作成功"),
  SYSTEM_ERROR(500, "系统繁忙，请稍后重试"),
  PARAM_ERROR(400, "参数错误"),

  // 认证模块 (20xxx)
  UNAUTHORIZED(401, "请先登录"),
  FORBIDDEN(403, "无权访问"),
  TOKEN_EXPIRED(401, "令牌已过期"),
  AUTH_FAILED(20001, "用户名或密码错误"),
  PHONE_INVALID(20002, "手机号格式错误"),
  VERIFY_CODE_ERROR(20003, "验证码错误或已失效"),
  USER_NOT_FOUND(20004, "用户不存在"),

  // 业务模块 (30xxx)
  RESOURCE_NOT_FOUND(404, "资源不存在"),
  BALANCE_NOT_ENOUGH(30001, "余额不足"),
  ;

  private final int code;
  private final String message;
}
