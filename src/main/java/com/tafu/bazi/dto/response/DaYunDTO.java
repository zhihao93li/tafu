package com.tafu.bazi.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 大运 DTO
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DaYunDTO {
  /** 大运序号（0-9） */
  private int index;

  /** 起始年龄 */
  private int startAge;

  /** 结束年龄 */
  private int endAge;

  /** 完整干支（如"甲子"）- 保留兼容性 */
  private String ganZhi;

  /** 天干（如"甲"）- 新增字段 */
  private String gan;

  /** 地支（如"子"）- 新增字段 */
  private String zhi;

  /** 起始年份 */
  private int startYear;

  /** 结束年份 */
  private int endYear;

  /** 该大运内的流年列表 */
  private List<LiuNianDTO> liuNian;
}
