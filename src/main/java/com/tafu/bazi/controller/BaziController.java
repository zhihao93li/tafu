package com.tafu.bazi.controller;

import com.tafu.bazi.dto.request.BaziCalculateRequest;
import com.tafu.bazi.dto.response.ApiResponse;
import com.tafu.bazi.service.BaziService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * BaziController
 *
 * <p>描述: 八字排盘 API 接口。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@RestController
@RequestMapping("/bazi")
@RequiredArgsConstructor
public class BaziController {

  private final BaziService baziService;

  @PostMapping("/calculate")
  public ApiResponse<Map<String, Object>> calculate(
      @RequestBody @Valid BaziCalculateRequest request) {
    return ApiResponse.success(baziService.calculate(request));
  }
}
