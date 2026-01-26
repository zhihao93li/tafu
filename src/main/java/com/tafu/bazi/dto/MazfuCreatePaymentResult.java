package com.tafu.bazi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MazfuCreatePaymentResult
 *
 * <p>描述: 码支付创建支付返回结果。
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
public class MazfuCreatePaymentResult {
  private Boolean success;
  private String tradeNo; // 码支付订单号
  private String qrcode; // 二维码链接（PC 端）
  private String payurl; // 支付跳转 URL（移动端）
  private String money; // 支付金额
  private String message; // 错误信息
}
