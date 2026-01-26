package com.tafu.bazi.service;

import com.tafu.bazi.dto.response.ThemeUnlockResponse;
import java.util.List;
import java.util.Map;

/**
 * ThemeService Map
 *
 * <p>描述: 主题解锁业务接口。
 *
 * <p>包含内容: 1. 解锁主题 (unlockTheme) 2. 获取主题内容 (getThemeContent) - 若未解锁则报错 3. 获取主题价格配置 4. 获取主题状态 5.
 * 批量获取主题内容
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
public interface ThemeService {

  // 返回解锁响应对象 (包含 taskId 或 alreadyUnlocked)
  ThemeUnlockResponse unlockTheme(String userId, String subjectId, String themeName);

  Map<String, Object> getThemeContent(String userId, String subjectId, String themeName);

  // 获取主题价格配置
  List<Map<String, Object>> getThemePricing();

  // 获取指定 subject 的主题状态
  List<Map<String, Object>> getThemeStatus(String userId, String subjectId);

  // 批量获取主题内容
  List<Map<String, Object>> getThemesBatch(String userId, String subjectId, List<String> themes);
}
