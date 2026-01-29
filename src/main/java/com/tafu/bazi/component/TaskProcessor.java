package com.tafu.bazi.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tafu.bazi.config.AiPromptsConfig;
import com.tafu.bazi.dto.ai.MinimalBaziData;
import com.tafu.bazi.entity.Subject;
import com.tafu.bazi.entity.Task;
import com.tafu.bazi.entity.ThemeAnalysis;
import com.tafu.bazi.repository.TaskRepository;
import com.tafu.bazi.repository.ThemeAnalysisRepository;
import com.tafu.bazi.service.SubjectService;
import com.tafu.bazi.utils.BaziResultOptimizer;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * TaskProcessor
 *
 * <p>描述: 异步任务处理器，处理主题解锁等耗时任务。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskProcessor {

  private final TaskRepository taskRepository;
  private final ThemeAnalysisRepository themeAnalysisRepository;
  private final SubjectService subjectService;
  private final OpenAiService openAiService;
  private final AiPromptsConfig aiPromptsConfig;
  private final ObjectMapper objectMapper;
  private final com.tafu.bazi.service.PointsService pointsService;
  private final com.tafu.bazi.repository.ThemePricingRepository themePricingRepository;

  private final org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor taskExecutor;

  @PostConstruct
  public void init() {
    log.info("===== TaskProcessor initialized =====");
    log.info("Task executor pool size: {}", taskExecutor.getCorePoolSize());
    log.info("Scheduler will check for pending tasks every 2 seconds");

    // 检查是否有待处理的任务
    List<Task> pendingTasks =
        taskRepository.findByStatusAndType(
            "pending", "THEME_UNLOCK", org.springframework.data.domain.PageRequest.of(0, 10));
    log.info("Found {} pending THEME_UNLOCK tasks at startup", pendingTasks.size());
    if (!pendingTasks.isEmpty()) {
      pendingTasks.forEach(t -> log.info("  - Task ID: {}, User: {}", t.getId(), t.getUserId()));
    }
  }

  @Scheduled(fixedDelay = 2000) // 每2秒轮询一次
  @net.javacrumbs.shedlock.spring.annotation.SchedulerLock(
      name = "processPendingTasks",
      lockAtLeastFor = "PT2S",
      lockAtMostFor = "PT10S")
  public void processPendingTasks() {
    List<Task> pendingTasks =
        taskRepository.findByStatusAndType(
            "pending", "THEME_UNLOCK", org.springframework.data.domain.PageRequest.of(0, 50));

    if (!pendingTasks.isEmpty()) {
      log.info("Found {} pending THEME_UNLOCK tasks", pendingTasks.size());
    }

    List<java.util.concurrent.CompletableFuture<Void>> futures = new java.util.ArrayList<>();
    for (Task task : pendingTasks) {
      log.info(
          "Submitting task {} for processing (user: {}, type: {})",
          task.getId(),
          task.getUserId(),
          task.getType());
      futures.add(
          java.util.concurrent.CompletableFuture.runAsync(() -> processTask(task), taskExecutor));
    }

    if (!futures.isEmpty()) {
      try {
        java.util.concurrent.CompletableFuture.allOf(
                futures.toArray(new java.util.concurrent.CompletableFuture[0]))
            .join();
      } catch (Exception e) {
        log.error("Error waiting for tasks to complete", e);
      }
    }
  }

  @Transactional
  public void processTask(Task task) {
    log.info("Processing task: {}", task.getId());
    try {
      // Update to processing
      task.setStatus("processing");
      task.setStartedAt(LocalDateTime.now());
      taskRepository.saveAndFlush(task);

      Map<String, Object> payload = task.getPayload();
      String subjectId = (String) payload.get("subjectId");
      String theme = (String) payload.get("theme");

      processThemeUnlock(task.getUserId(), subjectId, theme);

      // Success
      task.setStatus("completed");
      task.setCompletedAt(LocalDateTime.now());
      taskRepository.save(task);

    } catch (Exception e) {
      log.error("Task processing failed: {}", task.getId(), e);
      task.setStatus("failed");
      task.setError(e.getMessage());
      taskRepository.save(task);

      // 退还积分
      try {
        Map<String, Object> payload = task.getPayload();
        String theme = (String) payload.get("theme");
        int price =
            themePricingRepository
                .findByTheme(theme)
                .map(com.tafu.bazi.entity.ThemePricing::getPrice)
                .orElse(20);

        pointsService.addPoints(
            task.getUserId(), price, "refund_unlock_theme", "退还积分: 主题解锁失败 - " + theme);
        log.info(
            "Refunded {} points to user {} for failed task {}",
            price,
            task.getUserId(),
            task.getId());
      } catch (Exception refundError) {
        log.error("Failed to refund points for task: {}", task.getId(), refundError);
      }
    }
  }

  private void processThemeUnlock(String userId, String subjectId, String theme) {
    // 1. Double check if already exists
    if (themeAnalysisRepository.findBySubjectIdAndTheme(subjectId, theme).isPresent()) {
      log.info("Theme analysis already exists for subject: {}, theme: {}", subjectId, theme);
      return;
    }

    // 2. Prepare Data
    Subject subject = subjectService.getEntity(userId, subjectId);
    AiPromptsConfig.ThemePromptTemplate template =
        aiPromptsConfig.getPrompts().getThemes().get(theme);

    if (template == null) {
      throw new RuntimeException("Theme template not found: " + theme);
    }

    String systemPrompt = template.getSystem();
    String userPrompt = replacePlaceholders(template.getUser(), subject);

    // 验证 prompt 不为空
    if (systemPrompt == null || systemPrompt.trim().isEmpty()) {
      throw new RuntimeException("System prompt is empty for theme: " + theme);
    }
    if (userPrompt == null || userPrompt.trim().isEmpty()) {
      throw new RuntimeException("User prompt is empty for theme: " + theme);
    }

    // 3. Call AI with options from ai-prompts.yaml
    log.info("Calling AI for theme: {}, subject: {}", theme, subjectId);
    log.debug(
        "System prompt length: {}, User prompt length: {}",
        systemPrompt != null ? systemPrompt.length() : 0,
        userPrompt != null ? userPrompt.length() : 0);

    String aiResponse;
    try {
      List<ChatMessage> messages = new ArrayList<>();
      messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt));
      messages.add(new ChatMessage(ChatMessageRole.USER.value(), userPrompt));

      ChatCompletionRequest request =
          ChatCompletionRequest.builder()
              .model(aiPromptsConfig.getModel())
              .messages(messages)
              .temperature(aiPromptsConfig.getTemperature())
              .maxTokens(aiPromptsConfig.getMaxTokens())
              .build();

      aiResponse =
          openAiService.createChatCompletion(request).getChoices().get(0).getMessage().getContent();
      log.info("AI response received, length: {}", aiResponse != null ? aiResponse.length() : 0);
    } catch (Exception e) {
      log.error("Failed to call AI API for theme: {}, subject: {}", theme, subjectId, e);
      // 记录更详细的错误信息
      if (e.getCause() != null) {
        log.error("Caused by: {}", e.getCause().getMessage());
      }
      throw new RuntimeException("AI 调用失败: " + e.getMessage(), e);
    }

    // 4. Save Result
    ThemeAnalysis analysis = new ThemeAnalysis();
    analysis.setUserId(userId);
    analysis.setSubjectId(subjectId);
    analysis.setTheme(theme);
    analysis.setPointsCost(0); // Already deducted

    Map<String, Object> contentMap = new HashMap<>();
    contentMap.put("text", aiResponse); // 直接存储文本内容
    analysis.setContent(contentMap);

    themeAnalysisRepository.save(analysis);
  }

  private String replacePlaceholders(String template, Subject subject) {
    if (template == null) return "";
    String result = template;

    result =
        result
            .replace("{{ gender }}", subject.getGender())
            .replace("{{gender}}", subject.getGender());

    if (result.contains("baziMinimalJson")) {
      try {
        Map<String, Object> baziDataMap = subject.getBaziData();
        if (baziDataMap == null || baziDataMap.isEmpty()) {
          log.error("BaziData is null or empty for subject: {}", subject.getId());
          throw new RuntimeException("八字数据为空，无法生成分析");
        }

        log.debug("Converting baziData to MinimalBaziData for subject: {}", subject.getId());
        MinimalBaziData minimalData = BaziResultOptimizer.optimize(baziDataMap);
        String json = objectMapper.writeValueAsString(minimalData);
        result = result.replace("{{ baziMinimalJson }}", json).replace("{{baziMinimalJson}}", json);
        log.debug(
            "Successfully replaced baziMinimalJson placeholder, JSON length: {}", json.length());
      } catch (JsonProcessingException e) {
        log.error("Failed to serialize MinimalBaziData for subject: {}", subject.getId(), e);
        throw new RuntimeException("八字数据序列化失败: " + e.getMessage(), e);
      } catch (Exception e) {
        log.error("Failed to optimize BaziResult for subject: {}", subject.getId(), e);
        throw new RuntimeException("八字数据处理失败: " + e.getMessage(), e);
      }
    }
    return result;
  }
}
