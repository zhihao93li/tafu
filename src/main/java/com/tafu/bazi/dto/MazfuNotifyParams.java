package com.tafu.bazi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MazfuNotifyParams
 *
 * <p>描述: 码支付异步通知参数。
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
public class MazfuNotifyParams {
  private String pid; // 商户 ID
  private String tradeNo; // 码支付订单号
  private String outTradeNo; // 商户订单号
  private String type; // 支付方式
  private String name; // 商品名称
  private String money; // 金额
  private String tradeStatus; // 支付状态
  private String param; // 业务扩展参数（可选）
  private String sign; // 签名
  private String signType; // 签名类型
}
