package com.tafu.bazi.controller.admin;

import com.tafu.bazi.dto.response.ApiResponse;
import com.tafu.bazi.entity.PointsTransaction;
import com.tafu.bazi.repository.PointsTransactionRepository;
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
 * AdminPointsTransactionController
 *
 * <p>描述: 管理后台-积分流水管理 API。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@RestController
@RequestMapping("/admin/points-transactions")
@RequiredArgsConstructor
public class AdminPointsTransactionController {

  private final PointsTransactionRepository pointsTransactionRepository;

  @GetMapping
  public ApiResponse<Page<PointsTransaction>> list(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(required = false) String userId,
      @RequestParam(required = false) String type) {

    Specification<PointsTransaction> spec =
        (root, query, cb) -> {
          List<Predicate> predicates = new ArrayList<>();
          if (StringUtils.hasText(userId)) {
            predicates.add(cb.equal(root.get("userId"), userId));
          }
          if (StringUtils.hasText(type)) {
            predicates.add(cb.equal(root.get("type"), type));
          }
          return cb.and(predicates.toArray(new Predicate[0]));
        };

    return ApiResponse.success(
        pointsTransactionRepository.findAll(
            spec, PageRequest.of(page - 1, limit, Sort.by("createdAt").descending())));
  }
}
