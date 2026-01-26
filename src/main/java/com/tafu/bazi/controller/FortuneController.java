package com.tafu.bazi.controller;

import com.tafu.bazi.dto.response.ApiResponse;
import com.tafu.bazi.service.FortuneService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * FortuneController
 *
 * <p>描述: 运势分析 API。
 *
 * <p>包含内容: 1. 初步分析 (同步) 2. 初步分析 (流式)
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@RestController
@RequestMapping("/fortune")
@RequiredArgsConstructor
public class FortuneController {

  private final FortuneService fortuneService;

  @PostMapping("/analyze")
  public ApiResponse<Map<String, Object>> analyzeInitial(
      @AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> request) {
    String subjectId = request.get("subjectId");
    return ApiResponse.success(fortuneService.analyzeInitial(userDetails.getUsername(), subjectId));
  }

  @PostMapping(value = "/analyze-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<String> analyzeInitialStream(
      @AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> request) {
    String subjectId = request.get("subjectId");
    return fortuneService.analyzeInitialStream(userDetails.getUsername(), subjectId);
  }
}
