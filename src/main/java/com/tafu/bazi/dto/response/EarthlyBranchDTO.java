package com.tafu.bazi.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * 地支 DTO
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Data
@Builder
public class EarthlyBranchDTO {
  /** 地支中文（如"子"） */
  private String chinese;

  /** 五行："wood"|"fire"|"earth"|"metal"|"water" */
  private String element;
}
