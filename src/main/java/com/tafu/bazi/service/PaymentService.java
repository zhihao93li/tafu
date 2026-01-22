package com.tafu.bazi.service;

import com.tafu.bazi.entity.PaymentOrder;
import com.tafu.bazi.entity.PointsPackage;
import java.util.List;

/**
 * PaymentService Interface
 *
 * <p>描述: 支付业务逻辑接口。
 *
 * <p>包含内容: 1. 创建订单 (createOrder) 2. 处理回调 (handleCallback) 3. 获取套餐 (getPackages)
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
}
