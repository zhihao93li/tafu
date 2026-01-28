package com.tafu.bazi.service.impl;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.tafu.bazi.config.MazfuConfig;
import com.tafu.bazi.config.StripeConfig;
import com.tafu.bazi.dto.MazfuCreatePaymentRequest;
import com.tafu.bazi.dto.MazfuCreatePaymentResult;
import com.tafu.bazi.entity.PaymentOrder;
import com.tafu.bazi.entity.PointsPackage;
import com.tafu.bazi.exception.BusinessException;
import com.tafu.bazi.exception.StandardErrorCode;
import com.tafu.bazi.repository.PaymentOrderRepository;
import com.tafu.bazi.repository.PointsPackageRepository;
import com.tafu.bazi.service.MazfuService;
import com.tafu.bazi.service.PaymentService;
import com.tafu.bazi.service.PointsService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PaymentServiceImpl
 *
 * <p>描述: 支付业务逻辑实现。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class PaymentServiceImpl implements PaymentService {

  private final PaymentOrderRepository orderRepository;
  private final PointsPackageRepository packageRepository;
  private final PointsService pointsService;
  private final StripeConfig stripeConfig;
  private final MazfuConfig mazfuConfig;
  private final MazfuService mazfuService;

  @Override
  public List<PointsPackage> getActivePackages() {
    return packageRepository.findByIsActiveTrueOrderBySortOrderAsc();
  }

  @Override
  @Transactional
  public PaymentOrder createOrder(String userId, String packageId, String paymentMethod) {
    PointsPackage pkg =
        packageRepository
            .findById(packageId)
            .orElseThrow(() -> new BusinessException(StandardErrorCode.RESOURCE_NOT_FOUND));

    PaymentOrder order =
        PaymentOrder.builder()
            .userId(userId)
            .orderNo(generateOrderNo())
            .amount(pkg.getPrice())
            .points(pkg.getPoints())
            .paymentMethod(paymentMethod)
            .status("pending")
            .build();

    return orderRepository.save(order);
  }

  @Override
  @Transactional
  public void handlePaymentSuccess(String orderNo, String transactionId) {
    PaymentOrder order =
        orderRepository
            .findByOrderNo(orderNo)
            .orElseThrow(() -> new BusinessException(StandardErrorCode.RESOURCE_NOT_FOUND));

    if ("paid".equals(order.getStatus())) {
      log.info("Order {} already paid", orderNo);
      return;
    }

    order.setStatus("paid");
    order.setTransactionId(transactionId);
    order.setPaidAt(LocalDateTime.now());
    orderRepository.save(order);

    // 充值积分
    pointsService.addPoints(order.getUserId(), order.getPoints(), "recharge", "充值订单: " + orderNo);
  }

  @Override
  public PaymentOrder getOrder(String orderNo) {
    return orderRepository
        .findByOrderNo(orderNo)
        .orElseThrow(() -> new BusinessException(StandardErrorCode.RESOURCE_NOT_FOUND));
  }

  @Override
  @Transactional
  public Map<String, String> createCheckoutSession(
      String userId, String packageId, String successUrl, String cancelUrl) {
    PointsPackage pkg =
        packageRepository
            .findById(packageId)
            .orElseThrow(() -> new BusinessException(StandardErrorCode.RESOURCE_NOT_FOUND));

    // 创建订单
    PaymentOrder order =
        PaymentOrder.builder()
            .userId(userId)
            .orderNo(generateOrderNo())
            .amount(pkg.getPrice())
            .points(pkg.getPoints())
            .paymentMethod("stripe")
            .status("pending")
            .build();

    orderRepository.save(order);

    try {
      // 创建 Stripe Checkout Session
      SessionCreateParams params =
          SessionCreateParams.builder()
              .setMode(SessionCreateParams.Mode.PAYMENT)
              .setSuccessUrl(successUrl)
              .setCancelUrl(cancelUrl)
              .addLineItem(
                  SessionCreateParams.LineItem.builder()
                      .setPriceData(
                          SessionCreateParams.LineItem.PriceData.builder()
                              .setCurrency("usd")
                              .setUnitAmount((long) pkg.getPrice())
                              .setProductData(
                                  SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                      .setName(pkg.getName())
                                      .setDescription(pkg.getPoints() + " 积分")
                                      .build())
                              .build())
                      .setQuantity(1L)
                      .build())
              .putMetadata("orderId", order.getId())
              .putMetadata("orderNo", order.getOrderNo())
              .putMetadata("userId", userId)
              .build();

      Session session = Session.create(params);

      // 更新订单,保存 session ID
      order.setStripeSessionId(session.getId());
      orderRepository.save(order);

      Map<String, String> result = new HashMap<>();
      result.put("sessionId", session.getId());
      result.put("checkoutUrl", session.getUrl()); // 前端期望 checkoutUrl
      result.put("orderNo", order.getOrderNo());

      return result;

    } catch (StripeException e) {
      log.error("Failed to create Stripe checkout session", e);
      throw new BusinessException(StandardErrorCode.SYSTEM_ERROR.getCode(), "创建支付会话失败");
    }
  }

  @Override
  @Transactional
  public void handleWebhook(String payload, String signature) {
    String webhookSecret = stripeConfig.getWebhookSecret();

    if (webhookSecret == null || webhookSecret.isEmpty()) {
      log.warn("Stripe webhook secret not configured, skipping signature verification");
      return;
    }

    Event event;
    try {
      event = Webhook.constructEvent(payload, signature, webhookSecret);
    } catch (SignatureVerificationException e) {
      log.error("Invalid webhook signature", e);
      throw new BusinessException(StandardErrorCode.FORBIDDEN.getCode(), "无效的 Webhook 签名");
    }

    // 处理事件
    if ("checkout.session.completed".equals(event.getType())) {
      Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
      if (session != null && "paid".equals(session.getPaymentStatus())) {
        handleCheckoutSessionCompleted(session);
      }
    }
  }

  @Override
  public PaymentOrder getOrderBySessionIdOrOrderNo(String identifier) {
    // 首先尝试通过 Stripe Session ID 查询
    Optional<PaymentOrder> orderBySessionId = orderRepository.findByStripeSessionId(identifier);
    if (orderBySessionId.isPresent()) {
      return orderBySessionId.get();
    }
    
    // 如果找不到，尝试通过订单号查询
    return orderRepository
        .findByOrderNo(identifier)
        .orElseThrow(() -> new BusinessException(StandardErrorCode.RESOURCE_NOT_FOUND));
  }

  private void handleCheckoutSessionCompleted(Session session) {
    String sessionId = session.getId();
    String orderNo = session.getMetadata().get("orderNo");

    if (orderNo == null) {
      log.error("Order number not found in session metadata: {}", sessionId);
      return;
    }

    PaymentOrder order =
        orderRepository
            .findByOrderNo(orderNo)
            .orElseThrow(() -> new BusinessException(StandardErrorCode.RESOURCE_NOT_FOUND));

    // 幂等性检查
    if ("paid".equals(order.getStatus())) {
      log.info("Order {} already paid, skipping", orderNo);
      return;
    }

    // 更新订单状态
    order.setStatus("paid");
    order.setTransactionId(session.getPaymentIntent());
    order.setPaidAt(LocalDateTime.now());
    orderRepository.save(order);

    // 充值积分
    pointsService.addPoints(order.getUserId(), order.getPoints(), "recharge", "充值订单: " + orderNo);

    log.info("Order {} payment completed successfully", orderNo);
  }

  @Override
  @Transactional
  public Map<String, Object> createMazfuOrder(String userId, String packageId, String device) {
    if (!mazfuConfig.isConfigured()) {
      throw new BusinessException(StandardErrorCode.SYSTEM_ERROR.getCode(), "码支付服务未配置");
    }

    PointsPackage pkg =
        packageRepository
            .findById(packageId)
            .orElseThrow(() -> new BusinessException(StandardErrorCode.RESOURCE_NOT_FOUND));

    if (!pkg.getIsActive()) {
      throw new BusinessException(StandardErrorCode.FORBIDDEN.getCode(), "该套餐已下架");
    }

    // 创建订单
    PaymentOrder order =
        PaymentOrder.builder()
            .userId(userId)
            .orderNo(generateOrderNo())
            .amount(pkg.getPrice())
            .points(pkg.getPoints())
            .paymentMethod("alipay_qrcode")
            .status("pending")
            .build();

    orderRepository.save(order);

    // 码支付二维码优惠 50 分
    int qrcodeDiscount = 50;
    int actualPayAmount = Math.max(pkg.getPrice() - qrcodeDiscount, 1);

    // 调用码支付服务创建支付
    MazfuCreatePaymentRequest request =
        MazfuCreatePaymentRequest.builder()
            .orderNo(order.getOrderNo())
            .amount(actualPayAmount)
            .productName(pkg.getName())
            .device(device)
            .build();

    MazfuCreatePaymentResult result = mazfuService.createPayment(request);

    if (!result.getSuccess()) {
      order.setStatus("failed");
      orderRepository.save(order);
      throw new BusinessException(
          StandardErrorCode.SYSTEM_ERROR.getCode(),
          result.getMessage() != null ? result.getMessage() : "创建支付请求失败");
    }

    // 更新订单，保存码支付订单号
    if (result.getTradeNo() != null) {
      order.setTransactionId(result.getTradeNo());
      orderRepository.save(order);
    }

    // 构造返回结果
    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("orderNo", order.getOrderNo());
    response.put("amount", pkg.getPrice());
    response.put("points", pkg.getPoints());
    if (result.getQrcode() != null) response.put("qrcode", result.getQrcode());
    if (result.getPayurl() != null) response.put("payurl", result.getPayurl());
    if (result.getMoney() != null) response.put("money", result.getMoney());

    return response;
  }

  private String generateOrderNo() {
    return LocalDateTime.now().toString().replace("-", "").replace(":", "").replace(".", "")
        + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }
}
