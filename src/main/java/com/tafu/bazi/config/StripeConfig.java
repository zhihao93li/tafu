package com.tafu.bazi.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * StripeConfig
 *
 * <p>描述: Stripe 支付配置类。
 *
 * <p>包含内容: 1. Stripe API Key 初始化 2. Webhook Secret 配置
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Slf4j
@Configuration
public class StripeConfig {

  @Value("${stripe.api.key:}")
  private String apiKey;

  @Value("${stripe.webhook.secret:}")
  private String webhookSecret;

  @PostConstruct
  public void init() {
    if (apiKey != null && !apiKey.isEmpty()) {
      Stripe.apiKey = apiKey;
      log.info("Stripe API initialized successfully");
    } else {
      log.warn("Stripe API key not configured. Payment features will not work.");
    }
  }

  public String getWebhookSecret() {
    return webhookSecret;
  }
}
