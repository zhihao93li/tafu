package com.tafu.bazi.controller;

import com.tafu.bazi.dto.response.ApiResponse;
import com.tafu.bazi.entity.PaymentOrder;
import com.tafu.bazi.entity.PointsPackage;
import com.tafu.bazi.service.PaymentService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * PaymentController
 *
 * <p>描述: 支付 API。
 *
 * <p>包含内容: 1. 积分套餐列表 2. 创建订单 3. Stripe Checkout 会话创建 4. Stripe Webhook 回调 5. 订单状态查询 6. Mock 支付回调
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @GetMapping("/packages")
  public ApiResponse<List<PointsPackage>> getPackages() {
    return ApiResponse.success(paymentService.getActivePackages());
  }

  @PostMapping("/create")
  public ApiResponse<PaymentOrder> createOrder(
      @AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> request) {
    String packageId = request.get("packageId");
    String paymentMethod = request.getOrDefault("paymentMethod", "manual");
    return ApiResponse.success(
        paymentService.createOrder(userDetails.getUsername(), packageId, paymentMethod));
  }

  // Mock callback endpoint for testing
  @PostMapping("/mock-callback")
  public ApiResponse<Void> mockCallback(@RequestBody Map<String, String> request) {
    String orderNo = request.get("orderNo");
    paymentService.handlePaymentSuccess(orderNo, "MOCK_txn_" + System.currentTimeMillis());
    return ApiResponse.success();
  }

  // Stripe Checkout 会话创建
  @PostMapping("/checkout")
  public ApiResponse<Map<String, String>> createCheckout(
      @AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> request) {
    String packageId = request.get("packageId");
    String successUrl = request.getOrDefault("successUrl", "http://localhost:5173/payment/success");
    String cancelUrl = request.getOrDefault("cancelUrl", "http://localhost:5173/payment/cancel");
    return ApiResponse.success(
        paymentService.createCheckoutSession(
            userDetails.getUsername(), packageId, successUrl, cancelUrl));
  }

  // Stripe Webhook 回调处理
  @PostMapping("/webhook")
  public ApiResponse<Void> handleWebhook(
      @RequestBody String payload, @RequestHeader("Stripe-Signature") String signature) {
    paymentService.handleWebhook(payload, signature);
    return ApiResponse.success();
  }

  // 根据 Stripe Session ID 查询订单状态
  @GetMapping("/status/{sessionId}")
  public ApiResponse<PaymentOrder> getOrderStatus(@PathVariable String sessionId) {
    return ApiResponse.success(paymentService.getOrderBySessionId(sessionId));
  }
}
