package com.tafu.bazi.dto.response;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ThemeUnlockResponse
 *
 * <p>描述: 主题解锁响应 DTO
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemeUnlockResponse {

  /** 是否已经解锁 */
  private Boolean alreadyUnlocked;

  /** 任务ID（异步生成时返回） */
  private String taskId;

  /** 剩余积分余额 */
  private Integer remainingBalance;

  /** 主题内容（如果已解锁） */
  private Map<String, Object> content;

  public static ThemeUnlockResponse alreadyUnlocked(Map<String, Object> content) {
    return ThemeUnlockResponse.builder().alreadyUnlocked(true).content(content).build();
  }

  public static ThemeUnlockResponse newUnlock(String taskId, Integer remainingBalance) {
    return ThemeUnlockResponse.builder().taskId(taskId).remainingBalance(remainingBalance).build();
  }
}
