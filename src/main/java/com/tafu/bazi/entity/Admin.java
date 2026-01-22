package com.tafu.bazi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * Admin
 *
 * <p>描述: 管理员实体类。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Data
@Entity
@Table(name = "admins")
public class Admin {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(nullable = false)
  private String passwordHash;

  @Column(nullable = false)
  private String role = "admin"; // admin, super_admin

  @Column(nullable = false)
  private Boolean isActive = true;

  private LocalDateTime lastLoginAt;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

  @PreUpdate
  public void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
