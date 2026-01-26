package com.tafu.bazi.dto.response;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 五行分析 DTO
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiveElementsDTO {
  /** 五行分布（带权重） */
  private Map<String, Double> distribution;

  /** 五行个数（天干+本气） */
  private Map<String, Integer> counts;

  /** 最旺五行 */
  private String strongest;

  /** 最弱五行 */
  private String weakest;

  /** 喜用五行 */
  private List<String> favorable;

  /** 忌讳五行 */
  private List<String> unfavorable;

  /** 五行旺衰状态 */
  private Map<String, String> elementStates;

  /** 月令五行 */
  private String monthElement;
}
