package com.tafu.bazi.service;

import com.tafu.bazi.dto.response.PointsResponse;

/**
 * PointsService Interface
 *
 * <p>描述: 积分业务逻辑接口。
 *
 * <p>包含内容: 1. 查询余额 (getBalance) 2. 增加积分 (addPoints) 3. 扣除积分 (deductPoints) 4. 查询流水
 * (getTransactions)
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
public interface PointsService {

  PointsResponse getMyPoints(String userId);

  void addPoints(String userId, int amount, String type, String description);

  void deductPoints(String userId, int amount, String type, String description);

  // Internal use for ensuring account exists
  void ensureAccountExists(String userId);
}
