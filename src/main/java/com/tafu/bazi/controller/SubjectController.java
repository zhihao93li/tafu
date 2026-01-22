package com.tafu.bazi.controller;

import com.tafu.bazi.dto.request.SubjectRequest;
import com.tafu.bazi.dto.response.ApiResponse;
import com.tafu.bazi.dto.response.SubjectResponse;
import com.tafu.bazi.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * SubjectController
 *
 * <p>描述: 测算对象管理 API 接口。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

  private final SubjectService subjectService;

  @GetMapping
  public ApiResponse<Page<SubjectResponse>> list(
      @AuthenticationPrincipal UserDetails userDetails,
      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return ApiResponse.success(subjectService.getList(userDetails.getUsername(), pageable));
  }

  @GetMapping("/{id}")
  public ApiResponse<SubjectResponse> detail(
      @AuthenticationPrincipal UserDetails userDetails, @PathVariable String id) {
    return ApiResponse.success(subjectService.getDetail(userDetails.getUsername(), id));
  }

  @PostMapping
  public ApiResponse<SubjectResponse> create(
      @AuthenticationPrincipal UserDetails userDetails,
      @RequestBody @Valid SubjectRequest.Create request) {
    return ApiResponse.success(subjectService.create(userDetails.getUsername(), request));
  }

  @PutMapping("/{id}")
  public ApiResponse<SubjectResponse> update(
      @AuthenticationPrincipal UserDetails userDetails,
      @PathVariable String id,
      @RequestBody @Valid SubjectRequest.Update request) {
    return ApiResponse.success(subjectService.update(userDetails.getUsername(), id, request));
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(
      @AuthenticationPrincipal UserDetails userDetails, @PathVariable String id) {
    subjectService.delete(userDetails.getUsername(), id);
    return ApiResponse.success();
  }

  @GetMapping("/{id}/reports")
  public ApiResponse<java.util.List<com.tafu.bazi.entity.FortuneReport>> getReports(
      @AuthenticationPrincipal UserDetails userDetails, @PathVariable String id) {
    // Need to add getReports to SubjectService
    return ApiResponse.success(subjectService.getReports(userDetails.getUsername(), id));
  }
}
