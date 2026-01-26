package com.tafu.bazi.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * MazfuConfig
 *
 * <p>描述: 码支付配置类。
 *
 * <p>包含内容: 1. 商户 PID 2. 签名密钥 KEY 3. API 基础 URL 4. 异步通知 URL 5. 同步跳转 URL
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Slf4j
@Getter
@Configuration
public class MazfuConfig {

  @Value("${mazfu.pid:}")
  private String pid;

  @Value("${mazfu.key:}")
  private String key;

  @Value("${mazfu.api.base-url:https://www.mazfu.com}")
  private String apiBaseUrl;

  @Value("${mazfu.notify-url:}")
  private String notifyUrl;

  @Value("${mazfu.return-url:}")
  private String returnUrl;

  @PostConstruct
  public void init() {
    if (isConfigured()) {
      log.info("Mazfu payment initialized successfully");
    } else {
      log.warn(
          "Mazfu payment not configured. Payment features will not work. Missing: {}",
          getMissingConfigs());
    }
  }

  public boolean isConfigured() {
    return pid != null
        && !pid.isEmpty()
        && key != null
        && !key.isEmpty()
        && notifyUrl != null
        && !notifyUrl.isEmpty()
        && returnUrl != null
        && !returnUrl.isEmpty();
  }

  private String getMissingConfigs() {
    StringBuilder sb = new StringBuilder();
    if (pid == null || pid.isEmpty()) sb.append("MAZFU_PID ");
    if (key == null || key.isEmpty()) sb.append("MAZFU_KEY ");
    if (notifyUrl == null || notifyUrl.isEmpty()) sb.append("MAZFU_NOTIFY_URL ");
    if (returnUrl == null || returnUrl.isEmpty()) sb.append("MAZFU_RETURN_URL ");
    return sb.toString().trim();
  }
}
