package com.tafu.bazi.service.impl;

import com.tafu.bazi.entity.PaymentOrder;
import com.tafu.bazi.entity.PointsPackage;
import com.tafu.bazi.exception.BusinessException;
import com.tafu.bazi.exception.StandardErrorCode;
import com.tafu.bazi.repository.PaymentOrderRepository;
import com.tafu.bazi.repository.PointsPackageRepository;
import com.tafu.bazi.service.PaymentService;
import com.tafu.bazi.service.PointsService;
import java.time.LocalDateTime;
import java.util.List;
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

  private String generateOrderNo() {
    return LocalDateTime.now().toString().replace("-", "").replace(":", "").replace(".", "")
        + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }
}
