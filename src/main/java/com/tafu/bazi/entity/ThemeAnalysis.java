package com.tafu.bazi.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ThemeAnalysis Entity
 *
 * <p>描述: 主题分析结果实体，映射 theme_analyses 表。
 *
 * <p>包含内容: 1. 关联 Subject, User 2. 主题名称 (theme) 3. 内容 (content - JSONB)
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Entity
@Table(
    name = "theme_analyses",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"subject_id", "theme"})})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Convert(attributeName = "jsonb", converter = JsonBinaryType.class)
public class ThemeAnalysis {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "subject_id", nullable = false)
  private String subjectId;

  @Column(nullable = false)
  private String theme;

  @Type(JsonBinaryType.class)
  @Column(columnDefinition = "jsonb", nullable = false)
  private Map<String, Object> content;

  @Column(name = "points_cost", nullable = false)
  private Integer pointsCost;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
}
