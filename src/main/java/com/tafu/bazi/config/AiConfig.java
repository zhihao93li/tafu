package com.tafu.bazi.config;

import com.tafu.bazi.utils.YamlPropertySourceFactory;
import org.springframework.ai.chat.client.ChatClient;
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
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Configuration
@EnableAsync
@PropertySource(value = "classpath:ai-prompts.yaml", factory = YamlPropertySourceFactory.class)
public class AiConfig {

  @Bean
  public ChatClient chatClient(ChatClient.Builder builder) {
    return builder.build();
  }
}
