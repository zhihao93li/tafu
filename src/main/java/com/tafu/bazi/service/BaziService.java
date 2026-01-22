package com.tafu.bazi.service;

import com.tafu.bazi.dto.request.BaziCalculateRequest;
import java.util.Map;

/**
 * BaziService Interface
 *
 * <p>描述: 八字排盘核心计算服务接口。
 *
 * <p>包含内容: 1. 计算八字排盘数据 (calculate)
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
public interface BaziService {

  /**
   * 计算八字排盘
   *
   * @param request 出生信息
   * @return 八字排盘数据 (Map 结构对应前端 TS 接口 BaziData)
   */
  Map<String, Object> calculate(BaziCalculateRequest request);
}
