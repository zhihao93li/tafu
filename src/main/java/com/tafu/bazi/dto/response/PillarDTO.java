package com.tafu.bazi.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * 单柱 DTO（年/月/日/时柱）
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Data
@Builder
public class PillarDTO {
  /** 天干 */
  private HeavenlyStemDTO heavenlyStem;

  /** 地支 */
  private EarthlyBranchDTO earthlyBranch;

  /** 纳音（如"海中金"） */
  private String naYin;

  /** 藏干列表 */
  private List<String> hiddenStems;

  /** 十神（相对日主） */
  private String tenGod;
}
