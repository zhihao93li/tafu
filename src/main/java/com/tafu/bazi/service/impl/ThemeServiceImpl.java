package com.tafu.bazi.service.impl;

import com.tafu.bazi.entity.Task;
import com.tafu.bazi.entity.ThemeAnalysis;
import com.tafu.bazi.exception.BusinessException;
import com.tafu.bazi.exception.StandardErrorCode;
import com.tafu.bazi.repository.TaskRepository;
import com.tafu.bazi.repository.ThemeAnalysisRepository;
import com.tafu.bazi.service.PointsService;
import com.tafu.bazi.service.ThemeService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ThemeServiceImpl
 *
 * <p>描述: 主题解锁业务逻辑实现。 逻辑: 1. 检查是否已解锁 2. 扣除积分 3. 创建异步任务 (Task) 4. 触发异步 AI 生成 (analyzeThemeAsync)
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ThemeServiceImpl implements ThemeService {

  private final ThemeAnalysisRepository themeAnalysisRepository;
  private final PointsService pointsService;
  private final TaskRepository taskRepository;
  private final com.tafu.bazi.repository.ThemePricingRepository themePricingRepository;
  private final com.tafu.bazi.repository.SubjectRepository subjectRepository;

  @Override
  @Transactional
  public String unlockTheme(String userId, String subjectId, String themeName) {
    // 1. 检查是否已存在
    if (themeAnalysisRepository.findBySubjectIdAndTheme(subjectId, themeName).isPresent()) {
      return "ALREADY_UNLOCKED";
    }

    // 2. 扣分
    int price =
        themePricingRepository
            .findByTheme(themeName)
            .map(com.tafu.bazi.entity.ThemePricing::getPrice)
            .orElse(20); // Fallback default
    pointsService.deductPoints(userId, price, "unlock_theme", "解锁主题: " + themeName);

    // 3. 创建 Task
    Task task =
        Task.builder()
            .userId(userId)
            .type("THEME_UNLOCK")
            .status("pending")
            .payload(Map.of("subjectId", subjectId, "theme", themeName))
            .build();
    taskRepository.save(task);

    // 4. 触发异步处理 (TaskProcessor handles this via @Scheduled)
    log.info("Task created id={}", task.getId());

    return task.getId();
  }

  @Override
  public Map<String, Object> getThemeContent(String userId, String subjectId, String themeName) {
    return themeAnalysisRepository
        .findBySubjectIdAndTheme(subjectId, themeName)
        .map(ThemeAnalysis::getContent)
        .orElseThrow(() -> new BusinessException(StandardErrorCode.FORBIDDEN.getCode(), "主题未解锁"));
  }

  @Override
  public List<Map<String, Object>> getThemePricing() {
    return themePricingRepository.findByIsActiveTrueOrderBySortOrderAsc().stream()
        .map(
            pricing -> {
              Map<String, Object> map = new HashMap<>();
              map.put("theme", pricing.getTheme());
              map.put("name", pricing.getName());
              map.put("description", pricing.getDescription());
              map.put("price", pricing.getPrice());
              map.put("originalPrice", pricing.getOriginalPrice());
              return map;
            })
        .toList();
  }

  @Override
  public List<Map<String, Object>> getThemeStatus(String userId, String subjectId) {
    // 验证 subject 归属
    subjectRepository
        .findByIdAndUserId(subjectId, userId)
        .orElseThrow(() -> new BusinessException(StandardErrorCode.RESOURCE_NOT_FOUND));

    // 获取所有主题价格配置
    List<com.tafu.bazi.entity.ThemePricing> allThemes =
        themePricingRepository.findByIsActiveTrueOrderBySortOrderAsc();

    // 获取该 subject 已解锁的主题
    List<ThemeAnalysis> unlockedAnalyses =
        themeAnalysisRepository.findAll().stream()
            .filter(a -> a.getSubjectId().equals(subjectId))
            .toList();

    Map<String, Boolean> unlockedMap = new HashMap<>();
    for (ThemeAnalysis analysis : unlockedAnalyses) {
      unlockedMap.put(analysis.getTheme(), true);
    }

    // 组合返回
    List<Map<String, Object>> result = new ArrayList<>();
    for (com.tafu.bazi.entity.ThemePricing pricing : allThemes) {
      Map<String, Object> map = new HashMap<>();
      map.put("theme", pricing.getTheme());
      map.put("isUnlocked", unlockedMap.getOrDefault(pricing.getTheme(), false));
      result.add(map);
    }

    return result;
  }

  @Override
  public List<Map<String, Object>> getThemesBatch(
      String userId, String subjectId, List<String> themes) {
    // 验证 subject 归属
    subjectRepository
        .findByIdAndUserId(subjectId, userId)
        .orElseThrow(() -> new BusinessException(StandardErrorCode.RESOURCE_NOT_FOUND));

    List<Map<String, Object>> result = new ArrayList<>();

    for (String theme : themes) {
      themeAnalysisRepository
          .findBySubjectIdAndTheme(subjectId, theme)
          .ifPresent(
              analysis -> {
                Map<String, Object> map = new HashMap<>();
                map.put("theme", analysis.getTheme());
                map.put("isUnlocked", true);
                map.put("content", analysis.getContent());
                result.add(map);
              });
    }

    return result;
  }
}
