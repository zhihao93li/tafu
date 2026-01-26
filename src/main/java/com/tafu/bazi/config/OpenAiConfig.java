package com.tafu.bazi.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * OpenAI API Configuration
 *
 * <p>配置 OpenAI API 客户端和日志
 *
 * @author Zhihao Li
 * @since 2026-01-27
 */
@Slf4j
@Configuration
public class OpenAiConfig {

  @Autowired private Environment env;

  @PostConstruct
  public void logConfig() {
    log.info("===== OpenAI Configuration =====");
    String baseUrl = env.getProperty("spring.ai.openai.base-url");
    String apiKey = env.getProperty("spring.ai.openai.api-key");

    log.info("Base URL: {}", baseUrl);
    log.info(
        "API Key: {}... (length: {})",
        apiKey != null && apiKey.length() > 10 ? apiKey.substring(0, 10) : "null",
        apiKey != null ? apiKey.length() : 0);
    log.info("================================");
  }
}
