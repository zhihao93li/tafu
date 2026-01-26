package com.tafu.bazi.controller;

import com.tafu.bazi.dto.response.ApiResponse;
import com.tafu.bazi.service.ThemeService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * ThemeController
 *
 * <p>描述: 主题 API。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@RestController
@RequestMapping("/themes")
@RequiredArgsConstructor
public class ThemeController {

  private final ThemeService themeService;

  @GetMapping("/pricing")
  public ApiResponse<Map<String, Object>> getPricing() {
    List<Map<String, Object>> pricing = themeService.getThemePricing();
    return ApiResponse.success(Map.of("pricing", pricing));
  }

  @GetMapping("/status/{subjectId}")
  public ApiResponse<Map<String, Object>> getStatus(
      @AuthenticationPrincipal UserDetails userDetails, @PathVariable String subjectId) {
    List<Map<String, Object>> status =
        themeService.getThemeStatus(userDetails.getUsername(), subjectId);
    return ApiResponse.success(Map.of("status", status));
  }

  @PostMapping("/batch")
  public ApiResponse<Map<String, Object>> getBatch(
      @AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, Object> request) {
    String subjectId = (String) request.get("subjectId");
    @SuppressWarnings("unchecked")
    List<String> themes = (List<String>) request.get("themes");

    List<Map<String, Object>> result =
        themeService.getThemesBatch(userDetails.getUsername(), subjectId, themes);
    return ApiResponse.success(Map.of("themes", result));
  }

  @PostMapping("/unlock")
  public ApiResponse<String> unlock(
      @AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> request) {
    String subjectId = request.get("subjectId");
    String theme = request.get("theme");
    return ApiResponse.success(
        themeService.unlockTheme(userDetails.getUsername(), subjectId, theme));
  }

  @GetMapping("/{subjectId}/{theme}")
  public ApiResponse<Map<String, Object>> getContent(
      @AuthenticationPrincipal UserDetails userDetails,
      @PathVariable String subjectId,
      @PathVariable String theme) {
    return ApiResponse.success(
        themeService.getThemeContent(userDetails.getUsername(), subjectId, theme));
  }
}
