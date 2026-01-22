package com.tafu.bazi.controller.admin;

import com.tafu.bazi.dto.response.ApiResponse;
import com.tafu.bazi.entity.Task;
import com.tafu.bazi.exception.BusinessException;
import com.tafu.bazi.exception.StandardErrorCode;
import com.tafu.bazi.repository.TaskRepository;
import com.tafu.bazi.service.AdminTaskService;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * AdminTaskController
 *
 * <p>描述: 管理后台-任务管理 API。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@RestController
@RequestMapping("/admin/tasks")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AdminTaskController {

  private final TaskRepository taskRepository;
  private final AdminTaskService adminTaskService;

  @GetMapping("/stats")
  public ApiResponse<Map<String, Object>> getStats() {
    return ApiResponse.success(adminTaskService.getStats());
  }

  @GetMapping
  public ApiResponse<Page<Task>> list(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) String userId) {

    Specification<Task> spec =
        (root, query, cb) -> {
          List<Predicate> predicates = new ArrayList<>();
          if (StringUtils.hasText(status)) {
            predicates.add(cb.equal(root.get("status"), status));
          }
          if (StringUtils.hasText(type)) {
            predicates.add(cb.equal(root.get("type"), type));
          }
          if (StringUtils.hasText(userId)) {
            predicates.add(cb.equal(root.get("userId"), userId));
          }
          return cb.and(predicates.toArray(new Predicate[0]));
        };

    return ApiResponse.success(
        taskRepository.findAll(
            spec, PageRequest.of(page - 1, limit, Sort.by("createdAt").descending())));
  }

  @GetMapping("/{id}")
  public ApiResponse<Task> get(@PathVariable String id) {
    return ApiResponse.success(
        taskRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(StandardErrorCode.RESOURCE_NOT_FOUND)));
  }

  @PostMapping("/{id}/retry")
  public ApiResponse<Task> retry(@PathVariable String id) {
    return ApiResponse.success(adminTaskService.retryTask(id));
  }

  @PostMapping("/{id}/cancel")
  public ApiResponse<Task> cancel(@PathVariable String id) {
    return ApiResponse.success(adminTaskService.cancelTask(id));
  }

  @PostMapping("/retry-all-failed")
  public ApiResponse<Map<String, Object>> retryAllFailed() {
    int count = adminTaskService.retryAllFailed();
    return ApiResponse.success(Map.of("message", "已重试 " + count + " 个失败任务", "count", count));
  }

  @PostMapping("/cleanup")
  public ApiResponse<Map<String, Object>> cleanup(@RequestBody Map<String, Integer> body) {
    int days = body.getOrDefault("days", 7);
    int count = adminTaskService.cleanup(days);
    return ApiResponse.success(Map.of("message", "已清理 " + count + " 个过期任务", "count", count));
  }
}
