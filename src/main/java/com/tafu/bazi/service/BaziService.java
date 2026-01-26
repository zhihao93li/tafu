package com.tafu.bazi.service;

import com.tafu.bazi.dto.request.BaziCalculateRequest;
import java.util.Map;

/**
 * BaziService Interface
 *
 * <p>描述: 八字排盘核心计算服务接口。
 *
 * <p>包含内容: 1. 计算八字排盘数据 (calculate) 2. 获取年份闰月信息 (getLeapMonth) 3. 获取地点经纬度
 * (getCoordinates)
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

  /**
   * 获取指定年份的闰月信息
   *
   * @param year 农历年份
   * @return 闰月月份 (0 表示无闰月, 1-12 表示闰几月)
   */
  int getLeapMonth(int year);

  /**
   * 获取地点的经纬度信息
   *
   * @param location 地点字符串 (格式: 省/市/区)
   * @return 包含 lng 和 lat 的 Map
   */
  Map<String, Double> getCoordinates(String location);
}
