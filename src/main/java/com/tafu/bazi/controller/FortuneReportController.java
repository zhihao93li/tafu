package com.tafu.bazi.controller;

import com.tafu.bazi.dto.response.ApiResponse;
import com.tafu.bazi.entity.FortuneReport;
import com.tafu.bazi.exception.BusinessException;
import com.tafu.bazi.exception.StandardErrorCode;
import com.tafu.bazi.repository.FortuneReportRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * FortuneReportController
 *
 * <p>描述: 命理报告历史记录 API。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class FortuneReportController {

  private final FortuneReportRepository reportRepository;

  @GetMapping
  public ApiResponse<List<FortuneReport>> list(@AuthenticationPrincipal UserDetails userDetails) {
    // Simple findAll for user, ordered by time desc
    // In real world, use Pagination (Pageable)
    return ApiResponse.success(
        reportRepository.findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(
            userDetails.getUsername()));
  }

  @GetMapping("/{id}")
  public ApiResponse<FortuneReport> getDetail(
      @AuthenticationPrincipal UserDetails userDetails, @PathVariable String id) {
    FortuneReport report =
        reportRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(StandardErrorCode.RESOURCE_NOT_FOUND));

    if (!report.getUserId().equals(userDetails.getUsername())) {
      throw new BusinessException(StandardErrorCode.FORBIDDEN);
    }

    return ApiResponse.success(report);
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(
      @AuthenticationPrincipal UserDetails userDetails, @PathVariable String id) {
    FortuneReport report =
        reportRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(StandardErrorCode.RESOURCE_NOT_FOUND));

    if (!report.getUserId().equals(userDetails.getUsername())) {
      throw new BusinessException(StandardErrorCode.FORBIDDEN);
    }

    report.setDeletedAt(LocalDateTime.now());
    reportRepository.save(report);
    return ApiResponse.success();
  }
}
