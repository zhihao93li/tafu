package com.tafu.bazi.dto.response;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * SubjectResponse
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Data
@Builder
public class SubjectResponse {
  private String id;
  private String name;
  private String gender;
  private String calendarType;
  private Integer birthYear;
  private Integer birthMonth;
  private Integer birthDay;
  private Integer birthHour;
  private Integer birthMinute;
  private boolean isLeapMonth;
  private String location;
  private String relationship;
  private String note;
  private LocalDateTime createdAt;

  // 完整的排盘数据（与前端 baziData 字段名保持一致）
  private Map<String, Object> baziData;
}
