package com.tafu.bazi.exception;

import lombok.Getter;

/**
 * BusinessException
 *
 * <p>描述: 通用业务异常类。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Getter
public class BusinessException extends RuntimeException {

  private final int code;

  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.code = errorCode.getCode();
  }

  public BusinessException(int code, String message) {
    super(message);
    this.code = code;
  }
}
