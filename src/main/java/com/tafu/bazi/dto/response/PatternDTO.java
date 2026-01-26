package com.tafu.bazi.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * 格局信息 DTO
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Data
@Builder
public class PatternDTO {
  /** 格局名称（如"正财格"） */
  private String name;

  /** 分类："normal"|"special" */
  private String category;

  /** 格局描述 */
  private String description;

  /** 月令本气（可选） */
  private String monthStem;

  /** 月令十神（可选） */
  private String monthStemTenGod;

  /** 是否透出（可选） */
  private Boolean isTransparent;
}
