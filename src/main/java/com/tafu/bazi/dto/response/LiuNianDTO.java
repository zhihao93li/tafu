package com.tafu.bazi.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * 流年 DTO
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Data
@Builder
public class LiuNianDTO {
  /** 流年年份 */
  private int year;

  /** 流年年龄 */
  private int age;

  /** 完整干支（如"甲子"）- 保留兼容性 */
  private String ganZhi;

  /** 天干（如"甲"）- 新增字段 */
  private String gan;

  /** 地支（如"子"）- 新增字段 */
  private String zhi;
}
