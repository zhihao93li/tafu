package com.tafu.bazi.service;

import java.util.Map;

/**
 * ThemeService Map
 *
 * <p>描述: 主题解锁业务接口。
 *
 * <p>包含内容: 1. 解锁主题 (unlockTheme) 2. 获取主题内容 (getThemeContent) - 若未解锁则报错
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
public interface ThemeService {

  // 返回 TaskId (异步)
  String unlockTheme(String userId, String subjectId, String themeName);

  Map<String, Object> getThemeContent(String userId, String subjectId, String themeName);
}
