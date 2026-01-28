package com.tafu.bazi.service;

import com.tafu.bazi.entity.PaymentOrder;
import com.tafu.bazi.entity.PointsPackage;
import java.util.List;
import java.util.Map;

/**
 * PaymentService Interface
 *
 * <p>描述: 支付业务逻辑接口。
 *
 * <p>包含内容: 1. 创建订单 (createOrder) 2. 处理回调 (handleCallback) 3. 获取套餐 (getPackages) 4. Stripe·Checkout
 * 集成 5. Webhook 处理 6. 码支付集成
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
public interface PaymentService {

  List<PointsPackage> getActivePackages();

  PaymentOrder createOrder(String userId, String packageId, String paymentMethod);

  // 模拟处理支付成功通知 (实际应有更复杂的 Webhook 参数)
  void handlePaymentSuccess(String orderNo, String transactionId);

  PaymentOrder getOrder(String orderNo);

  // Stripe Checkout 会话创建
  Map<String, String> createCheckoutSession(
      String userId, String packageId, String successUrl, String cancelUrl);

  // Stripe Webhook 处理
  void handleWebhook(String payload, String signature);

  // 根据 Stripe Session ID 或订单号查询订单
  PaymentOrder getOrderBySessionIdOrOrderNo(String identifier);

  // 码支付订单创建
  Map<String, Object> createMazfuOrder(String userId, String packageId, String device);
}
