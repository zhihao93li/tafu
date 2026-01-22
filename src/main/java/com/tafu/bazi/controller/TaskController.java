package com.tafu.bazi.controller;

import com.tafu.bazi.dto.response.ApiResponse;
import com.tafu.bazi.entity.Task;
import com.tafu.bazi.exception.BusinessException;
import com.tafu.bazi.exception.StandardErrorCode;
import com.tafu.bazi.repository.TaskRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * TaskController
 *
 * <p>描述: 用户任务查询 API。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class TaskController {

  private final TaskRepository taskRepository;

  @GetMapping("/{id}")
  public ApiResponse<Map<String, Object>> getTask(
      @AuthenticationPrincipal UserDetails userDetails, @PathVariable String id) {

    Task task =
        taskRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(StandardErrorCode.RESOURCE_NOT_FOUND));

    if (!task.getUserId().equals(userDetails.getUsername())) {
      throw new BusinessException(StandardErrorCode.FORBIDDEN);
    }

    // Return status similar to source tasks.ts
    Map<String, Object> response = new java.util.HashMap<>();
    response.put("taskId", task.getId());
    response.put("status", task.getStatus());
    if ("completed".equals(task.getStatus())) {
      response.put("content", task.getResult());
    }
    if ("failed".equals(task.getStatus())) {
      response.put("error", task.getError());
    }

    return ApiResponse.success(response);
  }
}
