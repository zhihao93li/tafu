package com.tafu.bazi.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Subject Entity
 *
 * <p>描述: 测算对象实体，映射 subjects 表。
 *
 * <p>包含内容: 1. 基本信息 (name, gender, birthInfo) 2. 排盘数据 (baziData - JSONB)
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Entity
@Table(name = "subjects")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Convert(
    attributeName = "jsonb",
    converter = JsonBinaryType.class) // For Hibernate 6 with hypersistence
public class Subject {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String gender; // male/female

  @Column(name = "calendar_type", nullable = false)
  private String calendarType; // solar/lunar

  @Column(name = "birth_year", nullable = false)
  private Integer birthYear;

  @Column(name = "birth_month", nullable = false)
  private Integer birthMonth;

  @Column(name = "birth_day", nullable = false)
  private Integer birthDay;

  @Column(name = "birth_hour", nullable = false)
  private Integer birthHour;

  @Column(name = "birth_minute", nullable = false)
  private Integer birthMinute;

  @Builder.Default
  @Column(name = "is_leap_month", nullable = false)
  private Boolean isLeapMonth = false;

  @Column(nullable = false)
  private String location; // format: "City,Province,Country" or just "City"

  @Type(JsonBinaryType.class)
  @Column(name = "bazi_data", columnDefinition = "jsonb")
  private Map<String, Object> baziData;

  @Type(JsonBinaryType.class)
  @Column(name = "initial_analysis", columnDefinition = "jsonb")
  private Map<String, Object> initialAnalysis;

  @Column(name = "initial_analyzed_at")
  private LocalDateTime initialAnalyzedAt;

  private String relationship;

  private String note;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
