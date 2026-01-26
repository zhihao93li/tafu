package com.tafu.bazi.controller;

import com.tafu.bazi.dto.response.ApiResponse;
import com.tafu.bazi.dto.response.SoulSongResponse;
import com.tafu.bazi.dto.response.ThemeUnlockResponse;
import com.tafu.bazi.exception.BusinessException;
import com.tafu.bazi.service.ThemeService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * SoulSongController
 *
 * <p>描述: 灵魂歌曲 API - 本质上是一个特殊的主题分析。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@RestController
@RequestMapping("/soul-song")
@RequiredArgsConstructor
public class SoulSongController {

  private final ThemeService themeService;
  private static final String SOUL_SONG_THEME = "soul_song";

  @GetMapping("/{subjectId}")
  public ApiResponse<SoulSongResponse> getSoulSong(
      @AuthenticationPrincipal UserDetails userDetails, @PathVariable String subjectId) {
    try {
      Map<String, Object> content =
          themeService.getThemeContent(userDetails.getUsername(), subjectId, SOUL_SONG_THEME);
      return ApiResponse.success(SoulSongResponse.unlocked(content));
    } catch (BusinessException e) {
      // 如果是未解锁异常，返回 isUnlocked: false
      return ApiResponse.success(SoulSongResponse.locked());
    }
  }

  @PostMapping("/unlock")
  public ApiResponse<ThemeUnlockResponse> unlockSoulSong(
      @AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> request) {
    String subjectId = request.get("subjectId");
    return ApiResponse.success(
        themeService.unlockTheme(userDetails.getUsername(), subjectId, SOUL_SONG_THEME));
  }

  @GetMapping("/pricing")
  public ApiResponse<Map<String, Object>> getPricing() {
    // 从主题价格配置中获取 soul_song 的价格
    java.util.List<Map<String, Object>> allPricing = themeService.getThemePricing();

    Map<String, Object> soulSongPricing =
        allPricing.stream()
            .filter(p -> SOUL_SONG_THEME.equals(p.get("theme")))
            .findFirst()
            .orElse(Map.of("price", 100, "originalPrice", 150)); // 默认值

    return ApiResponse.success(soulSongPricing);
  }
}
