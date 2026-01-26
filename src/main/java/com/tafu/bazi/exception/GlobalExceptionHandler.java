package com.tafu.bazi.exception;

import com.tafu.bazi.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * GlobalExceptionHandler
 *
 * <p>描述: 全局异常处理器，将所有异常转换为统一的 ApiResponse 格式。
 *
 * <p>包含内容: 1. 处理业务异常 (BusinessException) 2. 处理参数校验异常 (MethodArgumentNotValidException) 3. 处理系统未知异常
 * (Exception)
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** 处理参数校验异常 */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
    String message =
        e.getBindingResult().getAllErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining("; "));
    log.warn("Parameter validation failed: {}", message);
    return ApiResponse.error(StandardErrorCode.PARAM_ERROR.getCode(), message);
  }

  /** 处理资源未找到异常 */
  @ExceptionHandler(NoResourceFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiResponse<Void> handleNoResourceFoundException(NoResourceFoundException e) {
    return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "接口不存在");
  }

  /**
   * 处理客户端断开连接异常
   *
   * <p>常见场景：用户刷新页面、取消请求、网络超时等 这不是服务器错误，只需记录警告日志即可
   */
  @ExceptionHandler({
    org.springframework.web.context.request.async.AsyncRequestNotUsableException.class,
    org.apache.catalina.connector.ClientAbortException.class,
    java.io.IOException.class
  })
  public void handleClientAbortException(Exception e, HttpServletRequest request) {
    // 检查是否是 Broken pipe 或客户端断开连接
    String message = e.getMessage();
    if (message != null
        && (message.contains("Broken pipe")
            || message.contains("ClientAbortException")
            || message.contains("Connection reset by peer")
            || e
                instanceof
                org.springframework.web.context.request.async.AsyncRequestNotUsableException)) {
      // 只记录 INFO 级别的日志，不需要堆栈信息
      log.info(
          "Client disconnected during request: {} {} - {}",
          request.getMethod(),
          request.getRequestURI(),
          e.getClass().getSimpleName());
      return; // 不返回任何响应，因为客户端已经断开
    }

    // 如果是其他 IOException，继续抛出让下面的通用异常处理器处理
    throw new RuntimeException(e);
  }

  /** 处理系统未知异常 */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiResponse<Void> handleException(Exception e, HttpServletRequest request) {
    log.error("Unhandled exception at {}: ", request.getRequestURI(), e);
    // 生产环境隐藏具体错误堆栈，仅返回通过错误提示
    return ApiResponse.error(
        StandardErrorCode.SYSTEM_ERROR.getCode(),
        "调试错误信息: " + e.getMessage() + " | Stack: " + e.toString());
  }
}
