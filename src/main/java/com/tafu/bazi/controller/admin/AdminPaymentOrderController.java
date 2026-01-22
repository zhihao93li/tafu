package com.tafu.bazi.controller.admin;

import com.tafu.bazi.dto.response.ApiResponse;
import com.tafu.bazi.entity.PaymentOrder;
import com.tafu.bazi.exception.BusinessException;
import com.tafu.bazi.exception.StandardErrorCode;
import com.tafu.bazi.repository.PaymentOrderRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * AdminPaymentOrderController
 *
 * <p>描述: 管理后台-支付订单管理 API。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@RestController
@RequestMapping("/admin/payment-orders")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AdminPaymentOrderController {

  private final PaymentOrderRepository paymentOrderRepository;

  @GetMapping
  public ApiResponse<Page<PaymentOrder>> list(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String paymentMethod,
      @RequestParam(required = false) String userId,
      @RequestParam(required = false) String orderNo) {

    Specification<PaymentOrder> spec =
        (root, query, cb) -> {
          List<Predicate> predicates = new ArrayList<>();
          if (StringUtils.hasText(status)) {
            predicates.add(cb.equal(root.get("status"), status));
          }
          if (StringUtils.hasText(paymentMethod)) {
            predicates.add(cb.equal(root.get("paymentMethod"), paymentMethod));
          }
          if (StringUtils.hasText(userId)) {
            predicates.add(cb.equal(root.get("userId"), userId));
          }
          if (StringUtils.hasText(orderNo)) {
            predicates.add(cb.like(root.get("orderNo"), "%" + orderNo + "%"));
          }
          return cb.and(predicates.toArray(new Predicate[0]));
        };

    return ApiResponse.success(
        paymentOrderRepository.findAll(
            spec, PageRequest.of(page - 1, limit, Sort.by("createdAt").descending())));
  }

  @GetMapping("/{id}")
  public ApiResponse<PaymentOrder> get(@PathVariable String id) {
    return ApiResponse.success(
        paymentOrderRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(StandardErrorCode.RESOURCE_NOT_FOUND)));
  }
}
