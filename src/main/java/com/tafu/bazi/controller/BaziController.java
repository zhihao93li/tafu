package com.tafu.bazi.controller;

import com.tafu.bazi.dto.request.BaziCalculateRequest;
import com.tafu.bazi.dto.response.ApiResponse;
import com.tafu.bazi.service.BaziService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * BaziController
 *
 * <p>描述: 八字排盘 API 接口。
 *
 * <p>包含内容: 1. 八字排盘计算 2. 获取年份闰月信息 3. 获取地点经纬度
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

  /**
   * 获取指定年份的闰月信息
   *
   * @param year 农历年份
   * @return 包含 leapMonth 字段的响应 (0 表示无闰月, 1-12 表示闰几月)
   */
  @GetMapping("/leap-month/{year}")
  public ApiResponse<Map<String, Integer>> getLeapMonth(@PathVariable int year) {
    int leapMonth = baziService.getLeapMonth(year);
    Map<String, Integer> result = new HashMap<>();
    result.put("leapMonth", leapMonth);
    return ApiResponse.success(result);
  }

  /**
   * 获取地点的经纬度信息
   *
   * @param location 地点字符串 (格式: 省/市/区)
   * @return 包含 coordinates 对象的响应，coordinates 包含 lng 和 lat
   */
  @GetMapping("/coordinates")
  public ApiResponse<Map<String, Object>> getCoordinates(@RequestParam String location) {
    Map<String, Double> coordinates = baziService.getCoordinates(location);
    Map<String, Object> result = new HashMap<>();
    result.put("coordinates", coordinates);
    return ApiResponse.success(result);
  }
}
