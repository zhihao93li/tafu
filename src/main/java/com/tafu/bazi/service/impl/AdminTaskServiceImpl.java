package com.tafu.bazi.service.impl;

import com.tafu.bazi.entity.Task;
import com.tafu.bazi.exception.BusinessException;
import com.tafu.bazi.exception.StandardErrorCode;
import com.tafu.bazi.repository.TaskRepository;
import com.tafu.bazi.service.AdminTaskService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AdminTaskServiceImpl
 *
 * <p>描述: 管理后台任务服务实现.
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AdminTaskServiceImpl implements AdminTaskService {

  private final TaskRepository taskRepository;

  @Override
  public Map<String, Object> getStats() {
    // Simple counts
    long pending = countByStatus("pending");
    long processing = countByStatus("processing");
    long completed = countByStatus("completed");
    long failed = countByStatus("failed");
    long total = taskRepository.count();

    // Note: Repository needs methods for these or use Specifications.
    // For brevity using simple logic or assuming simple repo additions?
    // Using Specifications or counting in memory is widely inefficient.
    // Let's assume for Parity Audit validation we add custom query methods to repo or use
    // EntityManager.
    // For now, returning placeholder or basic counts to ensure compilation.

    // Return matching structure
    Map<String, Object> queue = new HashMap<>();
    queue.put("pending", pending);
    queue.put("processing", processing);
    queue.put("completed", completed);
    queue.put("failed", failed);
    queue.put("total", total);

    return Map.of("queue", queue, "timestamp", LocalDateTime.now());
  }

  private long countByStatus(String status) {
    return taskRepository.count((root, query, cb) -> cb.equal(root.get("status"), status));
  }

  @Override
  @Transactional
  public Task retryTask(String id) {
    Task task =
        taskRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(StandardErrorCode.RESOURCE_NOT_FOUND));

    if (!"failed".equals(task.getStatus())) {
      throw new IllegalArgumentException("Only failed tasks can be retried");
    }

    task.setStatus("pending");
    task.setError(null);
    task.setStartedAt(null);
    task.setCompletedAt(null);
    return taskRepository.save(task);
  }

  @Override
  @Transactional
  public Task cancelTask(String id) {
    Task task =
        taskRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(StandardErrorCode.RESOURCE_NOT_FOUND));

    if (!"pending".equals(task.getStatus())) {
      throw new IllegalArgumentException("Only pending tasks can be cancelled");
    }

    task.setStatus("failed");
    task.setError("Admin cancelled");
    task.setCompletedAt(LocalDateTime.now());
    return taskRepository.save(task);
  }

  @Override
  @Transactional
  public int retryAllFailed() {
    // Bulk update is better
    List<Task> failedTasks =
        taskRepository.findAll((root, query, cb) -> cb.equal(root.get("status"), "failed"));
    for (Task task : failedTasks) {
      task.setStatus("pending");
      task.setError(null);
      task.setStartedAt(null);
      task.setCompletedAt(null);
    }
    taskRepository.saveAll(failedTasks);
    return failedTasks.size();
  }

  @Override
  @Transactional
  public int cleanup(int days) {
    LocalDateTime threshold = LocalDateTime.now().minusDays(days);
    // Delete where completedAt < threshold AND status in (completed, failed)
    // Need to load and delete
    List<Task> toDelete =
        taskRepository.findAll(
            (root, query, cb) -> {
              return cb.and(
                  cb.lessThan(root.get("completedAt"), threshold),
                  root.get("status").in("completed", "failed"));
            });
    taskRepository.deleteAll(toDelete);
    return toDelete.size();
  }
}
