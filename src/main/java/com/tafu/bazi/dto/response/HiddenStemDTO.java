package com.tafu.bazi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 藏干 DTO
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HiddenStemDTO {
  /** 藏干中文名称（如"辛"） */
  private String chinese;

  /** 五行属性（wood/fire/earth/metal/water） */
  private String element;

  /** 阴阳属性（yang/yin） */
  private String yinYang;
}
