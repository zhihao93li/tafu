package com.tafu.bazi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日主强弱分析 DTO
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayMasterAnalysisDTO {
  /** 得令分数（0-100） */
  private double deLing;

  /** 得令描述 */
  private String deLingDesc;

  /** 得地分数（0-100） */
  private double deDi;

  /** 得地描述 */
  private String deDiDesc;

  /** 天干帮扶分数（0-100） */
  private double tianGanHelp;

  /** 天干帮扶描述 */
  private String tianGanHelpDesc;

  /** 总分（0-100） */
  private double totalScore;
}
