package com.tafu.bazi.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserResponse {
  private String id;
  private String phone;
  private String username;
  private Integer balance;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // Counts placeholders - can be implemented later with dedicated queries
  // private long subjectsCount;
  // private long ordersCount;
}
