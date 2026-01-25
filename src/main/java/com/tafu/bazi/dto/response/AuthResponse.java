package com.tafu.bazi.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * AuthResponse
 *
 * <p>
 * 描述: 认证响应数据 (Token + 用户信息)。
 *
 * <p>
 * 维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Data
@Builder
public class AuthResponse {
  private String token;
  private UserInfo user;

  @Data
  @Builder
  public static class UserInfo {
    private String id;
    private String username;
    private String phone;
    private int pointsBalance;
    private boolean isNewUser;
  }
}
