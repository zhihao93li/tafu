package com.tafu.bazi.dto.ai;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * MinimalBaziData
 *
 * <p>描述: 极简八字数据结构 (AI 调用专用)。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Data
@Builder
public class MinimalBaziData {
  private FourPillarsSimple fourPillars;
  private HiddenStemsSimple hiddenStems;
  private DayMasterSimple dayMaster;
  private FiveElementsSimple fiveElements;
  private String tenGods; // 已格式化的字符串
  private YunSimple yun;
  private ShenShaSimple shenSha;

  private String shengXiao;
  private String lunarDate;
  private String taiYuan;
  private String mingGong;
  private String shenGong;
  private String xunKong;

  @Data
  @Builder
  public static class FourPillarsSimple {
    private String year;
    private String month;
    private String day;
    private String hour;
    private NaYinSimple naYin;
  }

  @Data
  @Builder
  public static class NaYinSimple {
    private String year;
    private String month;
    private String day;
    private String hour;
  }

  @Data
  @Builder
  public static class HiddenStemsSimple {
    private String year;
    private String month;
    private String day;
    private String hour;
  }

  @Data
  @Builder
  public static class DayMasterSimple {
    private String stem;
    private String element;
    private String strength;
    private String characteristics;
  }

  @Data
  @Builder
  public static class FiveElementsSimple {
    private String distribution;
    private String strongest;
    private String weakest;
    private String favorable;
    private String unfavorable;
  }

  @Data
  @Builder
  public static class YunSimple {
    private int startAge;
    private boolean forward;
    private String currentDaYun;
    private String currentDaYunAge;
    private List<String> adjacentDaYun;
    private String currentLiuNian;
  }

  @Data
  @Builder
  public static class ShenShaSimple {
    private String year;
    private String month;
    private String day;
    private String hour;
  }
}
