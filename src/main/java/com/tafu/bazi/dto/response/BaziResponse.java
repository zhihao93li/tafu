package com.tafu.bazi.dto.response;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * 八字计算响应 DTO（顶层）
 *
 * <p>统一前后端数据格式，提供强类型数据契约
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Data
@Builder
public class BaziResponse {
  // ===== 基本信息 =====

  /** 性别："male" | "female" */
  private String gender;

  /** 公历日期时间 */
  private String solarDate;

  /** 农历日期描述 */
  private String lunarDate;

  /** 真太阳时信息 */
  private TrueSolarTimeDTO trueSolarTime;

  // ===== 核心八字数据 =====

  /** 四柱（年月日时） */
  private FourPillarsDTO fourPillars;

  /** 十神分布 */
  private Map<String, String> fourPillarsShiShen;

  /** 空亡信息 */
  private Map<String, String> fourPillarsXunKong;

  // ===== 分析数据 =====

  /** 日主强弱分析 */
  private DayMasterDTO dayMaster;

  /** 五行统计 */
  private FiveElementsDTO fiveElements;

  /** 十神分析 */
  private TenGodsDTO tenGods;

  /** 格局判断 */
  private PatternDTO pattern;

  /** 大运信息 */
  private YunInfoDTO yun;

  // ===== 其他信息 =====

  /** 神煞 ✅ 已实现 */
  private ShenShaDTO shenSha;

  /** 生肖 */
  private String shengXiao;

  /** 胎元 */
  private String taiYuan;

  /** 命宫 */
  private String mingGong;

  /** 身宫 */
  private String shenGong;

  /** 空亡 */
  private String xunKong;

  /** 日主特征描述 */
  private List<String> dayMasterCharacteristics;
}
