package com.tafu.bazi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MazfuCreatePaymentRequest
 *
 * <p>描述: 码支付创建支付请求参数。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MazfuCreatePaymentRequest {
  private String orderNo; // 商户订单号
  private Integer amount; // 金额（分）
  private String productName; // 商品名称
  private String device; // 设备类型: pc/mobile
  private String clientIp; // 客户端 IP（可选）
}
