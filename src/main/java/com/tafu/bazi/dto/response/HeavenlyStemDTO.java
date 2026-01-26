package com.tafu.bazi.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * 天干 DTO
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Data
@Builder
public class HeavenlyStemDTO {
  /** 天干中文（如"甲"） */
  private String chinese;

  /** 五行："wood"|"fire"|"earth"|"metal"|"water" */
  private String element;

  /** 阴阳："yang"|"yin" */
  private String yinYang;
}
