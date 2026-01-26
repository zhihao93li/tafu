package com.tafu.bazi.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * 神煞信息 DTO
 *
 * <p>✅ 已实现年月日时四柱完整神煞，使用 lunar-java 库的按柱位 API：
 *
 * <ul>
 *   <li>Lunar.getYearShenSha() - 年柱神煞
 *   <li>Lunar.getMonthShenSha() - 月柱神煞
 *   <li>Lunar.getDayShenSha() - 日柱神煞
 *   <li>Lunar.getTimeShenSha() - 时柱神煞
 * </ul>
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Data
@Builder
public class ShenShaDTO {
  /** 年柱神煞列表（如 ["太岁", "岁德"]） */
  private List<String> year;

  /** 月柱神煞列表（如 ["月德", "天德"]） */
  private List<String> month;

  /** 日柱神煞列表（如 ["天乙贵人", "文昌"]） */
  private List<String> day;

  /** 时柱神煞列表（如 ["时德", "日禄"]） */
  private List<String> hour;
}
