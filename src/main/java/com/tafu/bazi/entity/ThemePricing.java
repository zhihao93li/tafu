package com.tafu.bazi.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * ThemePricing
 *
 * <p>描述: 主题定价实体类 (配置不同测算主题的价格与描述)。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Data
@Entity
@Table(name = "theme_pricing")
public class ThemePricing {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(unique = true, nullable = false)
  private String theme;

  private String name;
  private String description;

  @Column(nullable = false)
  private Integer price; // Points

  private Integer originalPrice;

  @Column(nullable = false)
  private Boolean isActive = true;

  private Integer sortOrder = 0;
}
