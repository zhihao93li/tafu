package com.tafu.bazi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * VerificationCode Entity
 *
 * <p>描述: 验证码实体类，映射 verification_codes 表。
 *
 * <p>包含内容: 1. 验证码信息 (phone, code, expiresAt) 2. 状态 (used, attempts)
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Entity
@Table(name = "verification_codes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class VerificationCode {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String phone;

  @Column(nullable = false)
  private String code;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Builder.Default private Integer attempts = 0;

  @Builder.Default private Boolean used = false;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "user_id")
  private String userId;
}
