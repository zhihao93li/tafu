package com.tafu.bazi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ApiResponse
 *
 * <p>描述: 统一 API 响应结构封装类。
 *
 * <p>包含内容: 1. 统一的 success/code/message/data 结构 2. 静态工厂方法 (success/error)
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

  @Builder.Default private boolean success = true;

  @Builder.Default private int code = 200;

  @Builder.Default private String message = "操作成功";

  private T data;

  private String traceId;

  public static <T> ApiResponse<T> success(T data) {
    return ApiResponse.<T>builder().success(true).code(200).data(data).build();
  }

  public static <T> ApiResponse<T> success() {
    return success(null);
  }

  public static <T> ApiResponse<T> error(int code, String message) {
    return ApiResponse.<T>builder().success(false).code(code).message(message).build();
  }
}
