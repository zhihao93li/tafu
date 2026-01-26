package com.tafu.bazi.controller;

import com.tafu.bazi.dto.MazfuNotifyParams;
import com.tafu.bazi.dto.response.ApiResponse;
import com.tafu.bazi.entity.PaymentOrder;
import com.tafu.bazi.entity.PointsPackage;
import com.tafu.bazi.service.MazfuService;
import com.tafu.bazi.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * PaymentController
 *
 * <p>描述: 支付 API。
 *
 * <p>包含内容: 1. 积分套餐列表 2. 创建订单 3. Stripe Checkout 会话创建 4. Stripe Webhook 回调 5. 码支付订单创建 6.
 * 码支付异步通知 7. 码支付同步跳转 8. 订单状态查询 9. Mock 支付回调
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Slf4j
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;
  private final MazfuService mazfuService;

  @Value("${app.frontend-url:http://localhost:5173}")
  private String frontendUrl;

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

  // 创建码支付订单
  @PostMapping("/create-mazfu")
  public ApiResponse<Map<String, Object>> createMazfuOrder(
      @AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> request) {
    String packageId = request.get("packageId");
    String device = request.getOrDefault("device", "pc");
    return ApiResponse.success(
        paymentService.createMazfuOrder(userDetails.getUsername(), packageId, device));
  }

  // 码支付异步通知回调 (POST)
  @PostMapping("/mazfu-notify")
  public ResponseEntity<String> handleMazfuNotifyPost(@RequestBody Map<String, String> params) {
    MazfuNotifyParams notifyParams = convertToNotifyParams(params);
    String result = mazfuService.handleNotify(notifyParams);
    return ResponseEntity.ok(result);
  }

  // 码支付异步通知回调 (GET，兼容)
  @GetMapping("/mazfu-notify")
  public ResponseEntity<String> handleMazfuNotifyGet(@RequestParam Map<String, String> params) {
    MazfuNotifyParams notifyParams = convertToNotifyParams(params);
    String result = mazfuService.handleNotify(notifyParams);
    return ResponseEntity.ok(result);
  }

  // 码支付同步跳转回调
  @GetMapping("/mazfu-return")
  public ResponseEntity<Void> handleMazfuReturn(
      @RequestParam Map<String, String> params, HttpServletRequest request) {
    MazfuNotifyParams notifyParams = convertToNotifyParams(params);

    // 验证签名
    boolean valid = mazfuService.verifySign(notifyParams);
    String orderNo = notifyParams.getOutTradeNo();

    String redirectUrl;
    if (!valid) {
      log.warn("Mazfu return signature verification failed: {}", orderNo);
      redirectUrl =
          frontendUrl
              + "/payment/result?order_no="
              + orderNo
              + "&status=failed&error=invalid_signature";
    } else {
      String status = "TRADE_SUCCESS".equals(notifyParams.getTradeStatus()) ? "success" : "pending";
      redirectUrl = frontendUrl + "/payment/result?order_no=" + orderNo + "&status=" + status;
    }

    return ResponseEntity.status(302).header("Location", redirectUrl).build();
  }

  private MazfuNotifyParams convertToNotifyParams(Map<String, String> params) {
    return MazfuNotifyParams.builder()
        .pid(params.get("pid"))
        .tradeNo(params.get("trade_no"))
        .outTradeNo(params.get("out_trade_no"))
        .type(params.get("type"))
        .name(params.get("name"))
        .money(params.get("money"))
        .tradeStatus(params.get("trade_status"))
        .param(params.get("param"))
        .sign(params.get("sign"))
        .signType(params.get("sign_type"))
        .build();
  }
}
