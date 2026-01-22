package com.tafu.bazi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * PointsPackage Entity
 *
 * <p>描述: 积分充值套餐实体，映射 points_packages 表。
 *
 * <p>包含内容: 1. 套餐名 (name) 2. 积分数 (points) 3. 价格 (price)
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Entity
@Table(name = "points_packages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PointsPackage {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Integer points;

  @Column(nullable = false)
  private Integer price; // Unit: cents

  @Builder.Default
  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  @Builder.Default
  @Column(name = "sort_order", nullable = false)
  private Integer sortOrder = 0;
}
