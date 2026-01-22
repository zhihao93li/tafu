package com.tafu.bazi.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SubjectRequest DTOs
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
public class SubjectRequest {

  @Data
  public static class Create {
    @NotBlank private String name;

    @NotBlank private String gender; // male/female

    @NotBlank private String calendarType; // solar/lunar

    @NotNull
    @Min(1900)
    private Integer birthYear;

    @NotNull
    @Min(1)
    @Max(12)
    private Integer birthMonth;

    @NotNull
    @Min(1)
    @Max(31)
    private Integer birthDay;

    @NotNull
    @Min(0)
    @Max(23)
    private Integer birthHour;

    @NotNull
    @Min(0)
    @Max(59)
    private Integer birthMinute;

    private boolean isLeapMonth = false;

    @NotBlank private String location;

    private String relationship;
    private String note;
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  public static class Update extends Create {}
}
