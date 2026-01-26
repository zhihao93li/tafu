package com.tafu.bazi.service;

import java.util.Map;
import reactor.core.publisher.Flux;

/**
 * FortuneService Interface
 *
 * <p>描述: AI 运势分析服务接口。
 *
 * <p>包含内容: 1. 初步分析 (analyzeInitial) - 返回分析结果 Map 2. 流式初步分析 (analyzeInitialStream) - 返回流式响应 3. 异步分析
 * (analyzeInitialAsync) - 返回任务 ID
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
public interface FortuneService {

  /** 执行初步八字分析 (同步/或内部调用) 系统会自动判断积分扣除逻辑 (当前策略：初步分析可能免费或收费，视业务逻辑而定) */
  Map<String, Object> analyzeInitial(String userId, String subjectId);

  /** 执行初步八字分析 (流式输出) */
  Flux<String> analyzeInitialStream(String userId, String subjectId);
}
