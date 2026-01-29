package com.tafu.bazi.config;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * AiPromptsConfig
 *
 * <p>描述: AI 提示词配置类，映射 resources/ai-prompts.yaml。
 *
 * <p>包含内容: 1. Provider 配置 2. Prompts 模板映射
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Data
@Configuration
@EnableConfigurationProperties(AiPromptsConfig.class)
@ConfigurationProperties(prefix = "")
@PropertySource(value = "classpath:ai-prompts.yaml", factory = YamlPropertySourceFactory.class)
public class AiPromptsConfig {

  private String provider;
  private String model;
  private Double temperature;
  private Integer maxTokens;
  private Prompts prompts;

  @Data
  public static class Prompts {
    private PromptTemplate initial =
        new PromptTemplate(
            "你是一位资深的中国传统命理分析师，精通八字命理学。请对用户的八字进行全面深入的初步解读，作为后续分主题深度分析的参考基础。分析要专业、全面、有条理。",
            "请对此八字进行全面的初步解读。\n\n八字信息：\n{{ baziMinimalJson }}");
    private Map<String, ThemePromptTemplate> themes =
        Map.of(
            "life_color", new ThemePromptTemplate("你是一位资深的命理分析师，专注于解读人的生命底色与核心特质。", "请分析此人的生命底色。"),
            "relationship",
                new ThemePromptTemplate("你是一位资深的命理分析师，专注于亲密关系与情感运势分析。", "请分析此人的亲密关系运势。"),
            "career_wealth",
                new ThemePromptTemplate("你是一位资深的命理分析师，专注于事业发展与财富运势分析。", "请分析此人的事业财富运势。"),
            "health", new ThemePromptTemplate("你是一位资深的命理分析师，专注于身心健康分析。", "请分析此人的身心健康状况。"),
            "life_lesson",
                new ThemePromptTemplate("你是一位资深的命理分析师，专注于解读命局中的贵人与小人。", "请分析此人命局中的贵人与小人。"),
            "yearly_fortune", new ThemePromptTemplate("你是一位资深的命理分析师，专注于流年运势分析。", "请分析此人的当年运势。"),
            "synastry", new ThemePromptTemplate("你是一位资深的命理分析师，专注于合盘分析。", "请分析双人合盘。"));
  }

  @Data
  public static class PromptTemplate {
    private String system;
    private String user;

    public PromptTemplate() {}

    public PromptTemplate(String system, String user) {
      this.system = system;
      this.user = user;
    }
  }

  @Data
  public static class ThemePromptTemplate {
    private String description;
    private String category;
    private String word_count;
    private String system;
    private String user;

    public ThemePromptTemplate() {}

    public ThemePromptTemplate(String system, String user) {
      this.system = system;
      this.user = user;
    }
  }
}
