package com.tafu.bazi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.service.OpenAiService;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

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
    ObjectMapper mapper = OpenAiService.defaultObjectMapper();
    OkHttpClient client = OpenAiService.defaultClient(apiKey, Duration.ofSeconds(60));

    Retrofit retrofit =
        new Retrofit.Builder()
            .baseUrl(baseUrl.endsWith("/") ? baseUrl : baseUrl + "/")
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    com.theokanning.openai.client.OpenAiApi api =
        retrofit.create(com.theokanning.openai.client.OpenAiApi.class);

    return new OpenAiService(api);
  }
}
