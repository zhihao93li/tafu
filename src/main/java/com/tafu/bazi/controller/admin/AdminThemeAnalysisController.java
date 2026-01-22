package com.tafu.bazi.controller.admin;

import com.tafu.bazi.dto.response.ApiResponse;
import com.tafu.bazi.entity.ThemeAnalysis;
import com.tafu.bazi.repository.ThemeAnalysisRepository;
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
 * AdminThemeAnalysisController
 *
 * <p>描述: 管理后台-主题测算记录管理 API。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@RestController
@RequestMapping("/admin/theme-analyses")
@RequiredArgsConstructor
public class AdminThemeAnalysisController {

  private final ThemeAnalysisRepository themeAnalysisRepository;

  @GetMapping
  public ApiResponse<Page<ThemeAnalysis>> list(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(required = false) String userId,
      @RequestParam(required = false) String theme) {

    Specification<ThemeAnalysis> spec =
        (root, query, cb) -> {
          List<Predicate> predicates = new ArrayList<>();
          if (StringUtils.hasText(userId)) {
            predicates.add(cb.equal(root.get("userId"), userId));
          }
          if (StringUtils.hasText(theme)) {
            predicates.add(cb.equal(root.get("theme"), theme));
          }
          return cb.and(predicates.toArray(new Predicate[0]));
        };

    return ApiResponse.success(
        themeAnalysisRepository.findAll(
            spec, PageRequest.of(page - 1, limit, Sort.by("createdAt").descending())));
  }
}
