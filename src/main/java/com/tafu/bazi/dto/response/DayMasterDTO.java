package com.tafu.bazi.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * 日主 DTO
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Data
@Builder
public class DayMasterDTO {
  /** 日主天干 */
  private String gan;

  /** 强弱："weak"|"balanced"|"strong" */
  private String strength;

  /** 详细分析 */
  private DayMasterAnalysisDTO analysis;
}
