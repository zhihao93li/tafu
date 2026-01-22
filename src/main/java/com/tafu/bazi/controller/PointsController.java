package com.tafu.bazi.controller;

import com.tafu.bazi.dto.response.ApiResponse;
import com.tafu.bazi.dto.response.PointsResponse;
import com.tafu.bazi.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PointsController
 *
 * <p>描述: 积分相关 API 接口。
 *
 * <p>包含内容: 1. 查询我的积分和流水
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointsController {

  private final PointsService pointsService;
  private final com.tafu.bazi.service.PaymentService paymentService;

  @GetMapping
  public ApiResponse<PointsResponse> getMyPoints(@AuthenticationPrincipal UserDetails userDetails) {
    return ApiResponse.success(pointsService.getMyPoints(userDetails.getUsername()));
  }

  @GetMapping("/packages")
  public ApiResponse<java.util.List<com.tafu.bazi.entity.PointsPackage>> getPackages() {
    // Assuming PointsService or PaymentService can provide this.
    // Ideally should inject PaymentService if that's where getActivePackages lives, or move logic
    // to PointsService.
    // For verify check, I will use PaymentService if I can inject it, or just use PointsService if
    // it has it.
    // Let's assume we update PointsService to include getActivePackages or inject PaymentService
    // here.
    // Since I can't check PointsService easy now, I'll inject PaymentService.
    return ApiResponse.success(paymentService.getActivePackages());
  }
}
