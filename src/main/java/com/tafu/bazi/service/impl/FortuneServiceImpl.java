package com.tafu.bazi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tafu.bazi.config.AiPromptsConfig;
import com.tafu.bazi.dto.ai.MinimalBaziData;
import com.tafu.bazi.entity.Subject;
import com.tafu.bazi.exception.BusinessException;
import com.tafu.bazi.exception.StandardErrorCode;
import com.tafu.bazi.service.FortuneService;
import com.tafu.bazi.service.SubjectService;
import com.tafu.bazi.utils.BaziResultOptimizer;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

/**
 * FortuneServiceImpl
 *
 * <p>描述: 运势分析核心实现。 逻辑: 1. 获取 Subject 排盘数据 2. 也是用 Spring AI 调用大模型 3. 使用 ai-prompts.yaml 中的 "initial"
 * 模板
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FortuneServiceImpl implements FortuneService {

  private final SubjectService subjectService;
  private final AiPromptsConfig aiPromptsConfig;
  private final ChatClient chatClient;
  private final ObjectMapper objectMapper;
  private final org.springframework.ai.openai.OpenAiChatOptions openAiChatOptions;

  @Override
  @Transactional
  public Map<String, Object> analyzeInitial(String userId, String subjectId) {
    Subject subject = subjectService.getEntity(userId, subjectId);

    // 如果已经分析过，直接返回 (暂定逻辑)
    if (subject.getInitialAnalysis() != null && !subject.getInitialAnalysis().isEmpty()) {
      return subject.getInitialAnalysis();
    }

    // 1. 准备 Prompt
    String systemPrompt = aiPromptsConfig.getPrompts().getInitial().getSystem();
    String userPromptTemplate = aiPromptsConfig.getPrompts().getInitial().getUser();

    // 简单替换模版变量 (Spring AI 有 PromptTemplate，这里手动替换 demo)
    // 实际开发中推荐使用 BeanWrapper 或 MapAccessor 替换
    String userPrompt = replacePlaceholders(userPromptTemplate, subject);

    // 2. 调用 AI with configured options
    log.info("Calling AI for initial analysis, subject: {}", subjectId);
    
    String aiResponse;
    try {
      aiResponse =
          chatClient
              .prompt(
                  new Prompt(
                      List.of(new SystemMessage(systemPrompt), new UserMessage(userPrompt)),
                      openAiChatOptions))
              .call()
              .content();
    } catch (Exception e) {
      log.error("AI Call Failed", e);
      throw new BusinessException(StandardErrorCode.SYSTEM_ERROR.getCode(), "AI 服务暂时不可用");
    }

    // 3. 解析结果 (假设 AI 返回 JSON，如果返回纯文本则封装为 { "content": ... })
    // 这里简化为直接封装文本
    Map<String, Object> result = new HashMap<>();
    result.put("content", aiResponse);

    // 4. 保存结果
    subject.setInitialAnalysis(result);
    subject.setInitialAnalyzedAt(LocalDateTime.now());
    // subjectRepository.save(subject); // subjectService.getEntity returns attached entity?
    // Better call update explicitly if separation of concern needed

    return result;
  }

  @Override
  public Flux<String> analyzeInitialStream(String userId, String subjectId) {
    Subject subject = subjectService.getEntity(userId, subjectId);

    // 如果已经分析过，返回已有结果
    if (subject.getInitialAnalysis() != null && !subject.getInitialAnalysis().isEmpty()) {
      String content = (String) subject.getInitialAnalysis().get("content");
      return Flux.just(content != null ? content : "");
    }

    // 1. 准备 Prompt
    String systemPrompt = aiPromptsConfig.getPrompts().getInitial().getSystem();
    String userPromptTemplate = aiPromptsConfig.getPrompts().getInitial().getUser();
    String userPrompt = replacePlaceholders(userPromptTemplate, subject);

    // 2. 调用 AI 流式接口 with configured options
    log.info("Calling AI stream for initial analysis, subject: {}", subjectId);
    
    AtomicReference<StringBuilder> fullContent = new AtomicReference<>(new StringBuilder());

    return chatClient
        .prompt(
            new Prompt(
                List.of(new SystemMessage(systemPrompt), new UserMessage(userPrompt)),
                openAiChatOptions))
        .stream()
        .content()
        .doOnNext(chunk -> fullContent.get().append(chunk))
        .doOnComplete(
            () -> {
              // 流式传输完成后保存完整结果
              try {
                Map<String, Object> result = new HashMap<>();
                result.put("content", fullContent.get().toString());
                subject.setInitialAnalysis(result);
                subject.setInitialAnalyzedAt(LocalDateTime.now());
                log.info("Stream analysis completed for subject: {}", subjectId);
              } catch (Exception e) {
                log.error("Failed to save stream analysis result", e);
              }
            })
        .onErrorResume(
            e -> {
              log.error("AI Stream Call Failed", e);
              return Flux.error(
                  new BusinessException(StandardErrorCode.SYSTEM_ERROR.getCode(), "AI 服务暂时不可用"));
            });
  }

  private String replacePlaceholders(String template, Subject subject) {
    if (template == null) return "";
    String result = template;

    // 1. Gender
    result =
        result
            .replace("{{ gender }}", subject.getGender())
            .replace("{{gender}}", subject.getGender());

    // 2. Bazi Minimal JSON
    if (result.contains("baziMinimalJson")) {
      try {
        Map<String, Object> baziDataMap = subject.getBaziData();
        MinimalBaziData minimalData = BaziResultOptimizer.optimize(baziDataMap);
        String json = objectMapper.writeValueAsString(minimalData);
        result = result.replace("{{ baziMinimalJson }}", json).replace("{{baziMinimalJson}}", json);
      } catch (JsonProcessingException e) {
        log.error("Failed to serialize MinimalBaziData", e);
        // Fallback or throw? Fallback to empty to avoid crash but AI will be confused.
        // Keeping original placeholder might be better for debugging.
      }
    }

    return result;
  }
}
