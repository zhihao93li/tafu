package com.tafu.bazi.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * 四柱 DTO
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Data
@Builder
public class FourPillarsDTO {
  /** 年柱 */
  private PillarDTO year;

  /** 月柱 */
  private PillarDTO month;

  /** 日柱 */
  private PillarDTO day;

  /** 时柱 */
  private PillarDTO hour;
}
