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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * TaskProcessor
 *
 * <p>
 * 描述: 异步任务处理器，处理主题解锁等耗时任务。
 *
 * <p>
 * 维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
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
  private final ChatClient chatClient;
  private final AiPromptsConfig aiPromptsConfig;
  private final ObjectMapper objectMapper;

  private final org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor taskExecutor;

  @Scheduled(fixedDelay = 2000) // 每2秒轮询一次
  @net.javacrumbs.shedlock.spring.annotation.SchedulerLock(name = "processPendingTasks", lockAtLeastFor = "PT2S", lockAtMostFor = "PT10S")
  public void processPendingTasks() {
    List<Task> pendingTasks = taskRepository.findByStatusAndType(
        "pending", "THEME_UNLOCK", org.springframework.data.domain.PageRequest.of(0, 50));

    List<java.util.concurrent.CompletableFuture<Void>> futures = new java.util.ArrayList<>();
    for (Task task : pendingTasks) {
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
    }
  }

  private void processThemeUnlock(String userId, String subjectId, String theme) {
    // 1. Double check if already exists
    if (themeAnalysisRepository.findBySubjectIdAndTheme(subjectId, theme).isPresent()) {
      return;
    }

    // 2. Prepare Data
    Subject subject = subjectService.getEntity(userId, subjectId);
    AiPromptsConfig.ThemePromptTemplate template = aiPromptsConfig.getPrompts().getThemes().get(theme);

    if (template == null) {
      throw new RuntimeException("Theme template not found: " + theme);
    }

    String systemPrompt = template.getSystem();
    String userPrompt = replacePlaceholders(template.getUser(), subject);

    // 3. Call AI
    String aiResponse = chatClient
        .prompt(
            new Prompt(List.of(new SystemMessage(systemPrompt), new UserMessage(userPrompt))))
        .call()
        .content();

    // 4. Save Result
    ThemeAnalysis analysis = new ThemeAnalysis();
    analysis.setUserId(userId);
    analysis.setSubjectId(subjectId);
    analysis.setTheme(theme);
    analysis.setPointsCost(0); // Already deducted

    Map<String, Object> contentMap = new HashMap<>();
    contentMap.put("content", aiResponse);
    analysis.setContent(contentMap);

    themeAnalysisRepository.save(analysis);
  }

  private String replacePlaceholders(String template, Subject subject) {
    if (template == null)
      return "";
    String result = template;

    result = result
        .replace("{{ gender }}", subject.getGender())
        .replace("{{gender}}", subject.getGender());

    if (result.contains("baziMinimalJson")) {
      try {
        Map<String, Object> baziDataMap = subject.getBaziData();
        MinimalBaziData minimalData = BaziResultOptimizer.optimize(baziDataMap);
        String json = objectMapper.writeValueAsString(minimalData);
        result = result.replace("{{ baziMinimalJson }}", json).replace("{{baziMinimalJson}}", json);
      } catch (JsonProcessingException e) {
        log.error("Failed to serialize MinimalBaziData", e);
      }
    }
    return result;
  }
}
