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
 * Task Entity
 *
 * <p>描述: 异步任务实体，映射 tasks 表。
 *
 * <p>包含内容: 1. 任务类型 (type) 2. 状态 (pending, processing, completed, failed) 3. 结果/错误信息
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Entity
@Table(name = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Convert(attributeName = "jsonb", converter = JsonBinaryType.class)
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(nullable = false)
  private String type; // e.g., "FORTUNE_ANALYSIS", "THEME_UNLOCK"

  @Builder.Default
  @Column(nullable = false)
  private String status = "pending";

  @Type(JsonBinaryType.class)
  @Column(columnDefinition = "jsonb")
  private Map<String, Object> payload;

  @Column(columnDefinition = "text")
  private String result;

  @Column(columnDefinition = "text")
  private String error;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "started_at")
  private LocalDateTime startedAt;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;
}
