package com.tafu.bazi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * PaymentOrder Entity
 *
 * <p>描述: 支付订单实体，映射 payment_orders 表。
 *
 * <p>包含内容: 1. 订单号 (orderNo) 2. 金额 (amount) 3. 对应积分 (points) 4. 状态 (pending, paid, failed) 5. 支付方式
 * (stripe, alipay, wechat)
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Entity
@Table(name = "payment_orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PaymentOrder {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "order_no", nullable = false, unique = true)
  private String orderNo;

  @Column(nullable = false)
  private Integer amount; // Unit: cents

  @Column(nullable = false)
  private Integer points;

  @Column(name = "payment_method", nullable = false)
  private String paymentMethod;

  @Builder.Default
  @Column(nullable = false)
  private String status = "pending";

  @Column(name = "transaction_id")
  private String transactionId;

  @Column(name = "stripe_session_id")
  private String stripeSessionId;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "paid_at")
  private LocalDateTime paidAt;
}
