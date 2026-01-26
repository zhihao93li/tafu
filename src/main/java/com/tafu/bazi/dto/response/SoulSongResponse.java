package com.tafu.bazi.dto.response;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SoulSongResponse
 *
 * <p>描述: 灵魂歌曲响应 DTO
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoulSongResponse {

  /** 是否已解锁 */
  private Boolean isUnlocked;

  /** 灵魂歌曲数据 */
  private Map<String, Object> data;

  public static SoulSongResponse unlocked(Map<String, Object> data) {
    return SoulSongResponse.builder().isUnlocked(true).data(data).build();
  }

  public static SoulSongResponse locked() {
    return SoulSongResponse.builder().isUnlocked(false).data(null).build();
  }
}
