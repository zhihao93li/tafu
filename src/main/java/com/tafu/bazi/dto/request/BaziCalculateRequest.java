package com.tafu.bazi.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * BaziCalculateRequest
 *
 * <p>描述: 八字排盘计算请求参数。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Data
public class BaziCalculateRequest {

  @NotNull
  @Min(1900)
  private Integer year;

  @NotNull
  @Min(1)
  @Max(12)
  private Integer month;

  @NotNull
  @Min(1)
  @Max(31)
  private Integer day;

  @NotNull
  @Min(0)
  @Max(23)
  private Integer hour;

  @NotNull
  @Min(0)
  @Max(59)
  private Integer minute;

  @NotBlank private String calendarType; // "solar" or "lunar"

  @NotBlank private String gender; // "male" or "female"

  private boolean isLeapMonth = false;

  @NotBlank private String location; // City name for solar time correction
}
