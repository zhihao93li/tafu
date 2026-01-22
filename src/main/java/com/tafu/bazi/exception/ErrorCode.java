package com.tafu.bazi.exception;

/**
 * ErrorCode
 *
 * <p>描述: 全局业务错误码枚举接口。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
public interface ErrorCode {
  int getCode();

  String getMessage();
}
