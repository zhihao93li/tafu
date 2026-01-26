package com.tafu.bazi.config;

import com.tafu.bazi.utils.YamlPropertySourceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * AiConfig
 *
 * <p>描述: Spring AI 相关配置。
 *
 * <p>包含内容: 1. ChatClient Bean 2. 加载 ai-prompts.yaml 3. 启用异步 (@EnableAsync)
 *
 * <p>说明: 模型配置从 ai-prompts.yaml 读取，在调用时显式传入 OpenAiChatOptions
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Slf4j
@Configuration
@EnableAsync
@RequiredArgsConstructor
@PropertySource(value = "classpath:ai-prompts.yaml", factory = YamlPropertySourceFactory.class)
public class AiConfig {

  private final AiPromptsConfig aiPromptsConfig;

  /**
   * 创建 ChatClient Bean
   *
   * <p>不设置默认 options，避免与 Spring AI 自动配置冲突
   *
   * @param builder Spring AI 提供的 ChatClient.Builder
   * @return 配置好的 ChatClient
   */
  @Bean
  public ChatClient chatClient(ChatClient.Builder builder) {
    return builder.build();
  }

  /**
   * 创建 OpenAiChatOptions Bean
   *
   * <p>从 ai-prompts.yaml 读取配置，供业务代码调用时使用
   *
   * @return 配置好的 OpenAiChatOptions
   */
  @Bean
  public OpenAiChatOptions openAiChatOptions() {
    String model = aiPromptsConfig.getModel();
    Double temperature = aiPromptsConfig.getTemperature();
    Integer maxTokens = aiPromptsConfig.getMaxTokens();

    log.info("===== AI Model Configuration (from ai-prompts.yaml) =====");
    log.info("Model: {}", model);
    log.info("Temperature: {}", temperature);
    log.info("Max Tokens: {}", maxTokens);
    log.info("========================================================");

    return OpenAiChatOptions.builder()
        .withModel(model)
        .withTemperature(temperature.floatValue())
        .withMaxTokens(maxTokens)
        .build();
  }
}
