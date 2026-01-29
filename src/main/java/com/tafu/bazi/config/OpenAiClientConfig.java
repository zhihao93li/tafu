package com.tafu.bazi.config;

import com.theokanning.openai.service.OpenAiService;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * OpenAI Client Configuration
 *
 * <p>使用原生 OpenAI Java 客户端
 *
 * @author Zhihao Li
 * @since 2026-01-29
 */
@Slf4j
@Configuration
@EnableAsync
public class OpenAiClientConfig {

  @Value("${openai.api-key}")
  private String apiKey;

  @Value("${openai.base-url:https://api.openai.com}")
  private String baseUrl;

  @Bean
  public OpenAiService openAiService() {
    log.info("===== OpenAI Client Configuration =====");
    log.info("Base URL: {}", baseUrl);
    log.info(
        "API Key: {}... (length: {})",
        apiKey != null && apiKey.length() > 10 ? apiKey.substring(0, 10) : "null",
        apiKey != null ? apiKey.length() : 0);
    log.info("=======================================");

    // 创建带自定义 base URL 的 OpenAiService
    com.theokanning.openai.client.OpenAiApi openAiApi =
        new retrofit2.Retrofit.Builder()
            .baseUrl(baseUrl.endsWith("/") ? baseUrl : baseUrl + "/")
            .client(
                com.theokanning.openai.service.OpenAiService.defaultClient(
                        apiKey, Duration.ofSeconds(60))
                    .newBuilder()
                    .build())
            .addConverterFactory(
                com.theokanning.openai.service.OpenAiService.defaultConverterFactory())
            .addCallAdapterFactory(
                com.theokanning.openai.service.OpenAiService.defaultCallAdapterFactory())
            .build()
            .create(com.theokanning.openai.client.OpenAiApi.class);

    return new OpenAiService(openAiApi);
  }
}
