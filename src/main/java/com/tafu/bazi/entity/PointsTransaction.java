package com.tafu.bazi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * PointsTransaction Entity
 *
 * <p>描述: 积分流水实体，映射 points_transactions 表。
 *
 * <p>包含内容: 1. 交易类型 (type: recharge, consume, gift) 2. 变动金额 (amount) 3. 变动后余额 (balance)
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Entity
@Table(name = "points_transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PointsTransaction {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private Integer amount;

  @Column(nullable = false)
  private Integer balance;

  @Column(nullable = false)
  private String description;

  @Column(name = "order_id")
  private String orderId;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
}
