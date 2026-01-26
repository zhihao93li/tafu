package com.tafu.bazi.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tafu.bazi.config.MazfuConfig;
import com.tafu.bazi.dto.MazfuCreatePaymentRequest;
import com.tafu.bazi.dto.MazfuCreatePaymentResult;
import com.tafu.bazi.dto.MazfuNotifyParams;
import com.tafu.bazi.entity.PaymentOrder;
import com.tafu.bazi.repository.PaymentOrderRepository;
import com.tafu.bazi.service.MazfuService;
import com.tafu.bazi.service.PointsService;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * MazfuServiceImpl
 *
 * <p>描述: 码支付业务逻辑实现。
 *
 * <p>包含内容: 1. MD5 签名生成与验证 2. 创建支付请求（调用码支付 API） 3. 处理异步通知回调
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class MazfuServiceImpl implements MazfuService {

  private final MazfuConfig mazfuConfig;
  private final PaymentOrderRepository orderRepository;
  private final PointsService pointsService;
  private final ObjectMapper objectMapper;
  private final HttpClient httpClient = HttpClient.newBuilder().build();

  @Override
  public MazfuCreatePaymentResult createPayment(MazfuCreatePaymentRequest request) {
    if (!mazfuConfig.isConfigured()) {
      return MazfuCreatePaymentResult.builder()
          .success(false)
          .message("码支付未配置，请检查环境变量")
          .build();
    }

    // 金额从分转换为元，保留两位小数
    String moneyInYuan = String.format("%.2f", request.getAmount() / 100.0);

    // 构建请求参数（统一使用 PC 模式，返回二维码）
    Map<String, String> params = new TreeMap<>();
    params.put("pid", mazfuConfig.getPid());
    params.put("type", "alipay");
    params.put("out_trade_no", request.getOrderNo());
    params.put("notify_url", mazfuConfig.getNotifyUrl());
    params.put("return_url", mazfuConfig.getReturnUrl());
    params.put("name", request.getProductName());
    params.put("money", moneyInYuan);
    params.put("device", "pc");

    if (request.getClientIp() != null && !request.getClientIp().isEmpty()) {
      params.put("clientip", request.getClientIp());
    }

    // 生成签名
    String sign = generateSign(params, mazfuConfig.getKey());
    params.put("sign", sign);
    params.put("sign_type", "MD5");

    try {
      String apiUrl = mazfuConfig.getApiBaseUrl() + "/xpay/epay/mapi.php";
      String formBody =
          params.entrySet().stream()
              .map(
                  e ->
                      URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8)
                          + "="
                          + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
              .collect(Collectors.joining("&"));

      log.info("[Mazfu] Requesting payment with order: {}", request.getOrderNo());

      HttpRequest httpRequest =
          HttpRequest.newBuilder()
              .uri(URI.create(apiUrl))
              .header("Content-Type", "application/x-www-form-urlencoded")
              .POST(HttpRequest.BodyPublishers.ofString(formBody))
              .build();

      HttpResponse<String> response =
          httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200) {
        return MazfuCreatePaymentResult.builder()
            .success(false)
            .message("HTTP 错误: " + response.statusCode())
            .build();
      }

      JsonNode jsonNode = objectMapper.readTree(response.body());
      log.info("[Mazfu] Response: {}", jsonNode);

      int code = jsonNode.has("code") ? jsonNode.get("code").asInt() : -1;
      if (code != 1) {
        String msg = jsonNode.has("msg") ? jsonNode.get("msg").asText() : "支付请求失败";
        return MazfuCreatePaymentResult.builder().success(false).message(msg).build();
      }

      return MazfuCreatePaymentResult.builder()
          .success(true)
          .tradeNo(jsonNode.has("trade_no") ? jsonNode.get("trade_no").asText() : null)
          .qrcode(jsonNode.has("qrcode") ? jsonNode.get("qrcode").asText() : null)
          .payurl(jsonNode.has("payurl") ? jsonNode.get("payurl").asText() : null)
          .money(jsonNode.has("money") ? jsonNode.get("money").asText() : null)
          .build();

    } catch (IOException | InterruptedException e) {
      log.error("[Mazfu] Request failed", e);
      return MazfuCreatePaymentResult.builder()
          .success(false)
          .message("请求失败: " + e.getMessage())
          .build();
    }
  }

  @Override
  public boolean verifySign(MazfuNotifyParams params) {
    if (!mazfuConfig.isConfigured()) {
      log.warn("[Mazfu] Config not set, skipping signature verification");
      return false;
    }

    Map<String, String> paramMap = new TreeMap<>();
    if (params.getPid() != null) paramMap.put("pid", params.getPid());
    if (params.getTradeNo() != null) paramMap.put("trade_no", params.getTradeNo());
    if (params.getOutTradeNo() != null) paramMap.put("out_trade_no", params.getOutTradeNo());
    if (params.getType() != null) paramMap.put("type", params.getType());
    if (params.getName() != null) paramMap.put("name", params.getName());
    if (params.getMoney() != null) paramMap.put("money", params.getMoney());
    if (params.getTradeStatus() != null) paramMap.put("trade_status", params.getTradeStatus());
    if (params.getParam() != null && !params.getParam().isEmpty())
      paramMap.put("param", params.getParam());

    String calculatedSign = generateSign(paramMap, mazfuConfig.getKey());
    boolean valid = calculatedSign.equalsIgnoreCase(params.getSign());

    if (!valid) {
      log.error(
          "[Mazfu] Signature verification failed. Expected: {}, Got: {}",
          calculatedSign,
          params.getSign());
    }

    return valid;
  }

  @Override
  @Transactional
  public String handleNotify(MazfuNotifyParams params) {
    try {
      // 验证签名
      if (!verifySign(params)) {
        log.error("[Mazfu] Notify signature verification failed: {}", params.getOutTradeNo());
        return "fail";
      }

      // 只处理支付成功的通知
      if (!"TRADE_SUCCESS".equals(params.getTradeStatus())) {
        log.info("[Mazfu] Notify status not success: {}", params.getTradeStatus());
        return "success";
      }

      String orderNo = params.getOutTradeNo();
      PaymentOrder order =
          orderRepository
              .findByOrderNo(orderNo)
              .orElseThrow(() -> new RuntimeException("Order not found: " + orderNo));

      // 幂等性检查
      if ("paid".equals(order.getStatus())) {
        log.info("[Mazfu] Order {} already paid, skipping", orderNo);
        return "success";
      }

      // 更新订单状态
      order.setStatus("paid");
      order.setTransactionId(params.getTradeNo());
      order.setPaidAt(LocalDateTime.now());
      orderRepository.save(order);

      // 充值积分
      pointsService.addPoints(
          order.getUserId(), order.getPoints(), "recharge", "充值订单: " + orderNo);

      log.info("[Mazfu] Order {} payment completed successfully", orderNo);
      return "success";

    } catch (Exception e) {
      log.error("[Mazfu] Notify processing error", e);
      return "fail";
    }
  }

  /**
   * 生成 MD5 签名
   *
   * <p>算法： 1. 按 ASCII 码升序排序所有参数 2. 排除 sign、sign_type 和空值参数 3. 拼接为 URL 键值对格式（a=b&c=d&e=f） 4.
   * 追加商户 KEY 并计算小写 MD5
   */
  private String generateSign(Map<String, String> params, String key) {
    String queryString =
        params.entrySet().stream()
            .filter(e -> !e.getKey().equals("sign") && !e.getKey().equals("sign_type"))
            .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("&"));

    String signString = queryString + key;

    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] digest = md.digest(signString.getBytes(StandardCharsets.UTF_8));
      StringBuilder hexString = new StringBuilder();
      for (byte b : digest) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) hexString.append('0');
        hexString.append(hex);
      }
      return hexString.toString().toLowerCase();
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate MD5 signature", e);
    }
  }
}
