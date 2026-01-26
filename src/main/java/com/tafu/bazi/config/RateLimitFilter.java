package com.tafu.bazi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tafu.bazi.dto.response.ApiResponse;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * RateLimitFilter
 *
 * <p>描述: 接口限流过滤器，基于 IP 地址进行限流。
 *
 * <p>使用 Bucket4j 实现令牌桶算法，防止恶意请求。
 *
 * <p>限流规则：
 * - 游客（未登录）：每分钟最多 20 次请求
 * - 已登录用户：每分钟最多 60 次请求
 *
 * @author Zhihao Li
 * @since 2026-01-27
 */
@Slf4j
@Component
public class RateLimitFilter extends OncePerRequestFilter {

  // 存储每个 IP 的限流桶
  private final Map<String, Bucket> ipBuckets = new ConcurrentHashMap<>();

  // 游客限流：每分钟 20 次请求
  private static final int GUEST_REQUESTS_PER_MINUTE = 20;

  // 已登录用户限流：每分钟 60 次请求
  private static final int USER_REQUESTS_PER_MINUTE = 60;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String clientIp = getClientIp(request);
    String token = request.getHeader("Authorization");
    boolean isAuthenticated = token != null && token.startsWith("Bearer ");

    // 白名单：这些路径不限流
    String path = request.getRequestURI();
    if (isWhitelisted(path)) {
      filterChain.doFilter(request, response);
      return;
    }

    // 获取或创建该 IP 的限流桶
    Bucket bucket = resolveBucket(clientIp, isAuthenticated);

    // 尝试消费一个令牌
    if (bucket.tryConsume(1)) {
      filterChain.doFilter(request, response);
    } else {
      // 限流触发
      log.warn("Rate limit exceeded for IP: {} (authenticated: {})", clientIp, isAuthenticated);
      sendRateLimitResponse(response);
    }
  }

  /**
   * 获取客户端真实 IP
   */
  private String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("X-Real-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    // 如果是多个 IP（经过多层代理），取第一个
    if (ip != null && ip.contains(",")) {
      ip = ip.split(",")[0].trim();
    }
    return ip;
  }

  /**
   * 获取或创建限流桶
   */
  private Bucket resolveBucket(String ip, boolean isAuthenticated) {
    return ipBuckets.computeIfAbsent(
        ip + "_" + isAuthenticated,
        key -> {
          int capacity = isAuthenticated ? USER_REQUESTS_PER_MINUTE : GUEST_REQUESTS_PER_MINUTE;
          Bandwidth limit =
              Bandwidth.classic(capacity, Refill.intervally(capacity, Duration.ofMinutes(1)));
          return Bucket.builder().addLimit(limit).build();
        });
  }

  /**
   * 白名单路径：不限流
   */
  private boolean isWhitelisted(String path) {
    return path.startsWith("/api/auth/login")
        || path.startsWith("/api/auth/register")
        || path.startsWith("/api/v3/api-docs")
        || path.startsWith("/api/swagger-ui");
  }

  /**
   * 返回限流响应
   */
  private void sendRateLimitResponse(HttpServletResponse response) throws IOException {
    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    ApiResponse<Void> apiResponse =
        ApiResponse.error(
            HttpStatus.TOO_MANY_REQUESTS.value(), "请求过于频繁，请稍后再试");

    response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
  }
}
