package com.tafu.bazi.service;

import com.tafu.bazi.entity.Task;
import java.util.Map;

/**
 * AdminTaskService
 *
 * <p>描述: 管理后台任务服务接口.
 */
public interface AdminTaskService {

  Map<String, Object> getStats();

  Task retryTask(String id);

  Task cancelTask(String id);

  int retryAllFailed();

  int cleanup(int days);
}
