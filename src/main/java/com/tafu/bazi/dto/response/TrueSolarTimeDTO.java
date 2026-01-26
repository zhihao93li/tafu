package com.tafu.bazi.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * 真太阳时 DTO
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Data
@Builder
public class TrueSolarTimeDTO {
  /** 年 */
  private int year;

  /** 月 */
  private int month;

  /** 日 */
  private int day;

  /** 时 */
  private int hour;

  /** 分 */
  private int minute;
}
