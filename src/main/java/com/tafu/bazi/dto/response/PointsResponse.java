package com.tafu.bazi.dto.response;

import com.tafu.bazi.entity.PointsTransaction;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * PointsResponse
 *
 * <p>描述: 积分信息响应数据。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Data
@Builder
public class PointsResponse {
  private Integer balance;
  private List<PointsTransaction> transactions;
  private Integer total;
}
