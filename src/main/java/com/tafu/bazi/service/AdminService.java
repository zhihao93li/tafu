package com.tafu.bazi.service;

import com.tafu.bazi.entity.Admin;
import java.util.Map;

/**
 * AdminService
 *
 * <p>描述: 管理员业务逻辑接口。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
public interface AdminService {
  String login(String username, String password);

  Admin createAdmin(String username, String password, String role);

  Map<String, Object> getDashboardStats();
}
