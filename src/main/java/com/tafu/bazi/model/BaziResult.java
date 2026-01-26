package com.tafu.bazi.model;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * BaziResult
 *
 * <p>描述: 八字计算结果实体类，定义已排好的八字数据结构。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Data
@Builder
public class BaziResult {

  private FourPillars pillars;
  private DayMaster dayMaster;
  private FiveElementsAnalysis fiveElements;

  // other fields like lunarDate, etc. can be added as needed or kept in Map for loose structure

  // ===========================
  // Inner Classes
  // ===========================

  @Data
  @Builder
  public static class Pillar {
    private String gan; // Chinese char
    private String zhi; // Chinese char
    private String nayin;
    private List<String> hiddenStems;
    private String tenGod; // For Day Master relation
  }

  @Data
  @Builder
  public static class FourPillars {
    private Pillar year;
    private Pillar month;
    private Pillar day;
    private Pillar hour;
  }

  @Data
  @Builder
  public static class DayMaster {
    private String gan; // 天干
    private String strength; // strong, weak, balanced
    private DayMasterAnalysis analysis;
  }

  @Data
  @Builder
  public static class DayMasterAnalysis {
    private double deLing;
    private String deLingDesc;
    private double deDi;
    private String deDiDesc;
    private double tianGanHelp;
    private String tianGanHelpDesc;
    private double totalScore;
  }

  @Data
  @Builder
  public static class FiveElementsAnalysis {
    private Map<String, Double> distribution; // Element -> Score
    private Map<String, Integer> counts; // Element -> Count
    private String strongest;
    private String weakest;
    private List<String> favorable;
    private List<String> unfavorable;
    private Map<String, String> elementStates; // Element -> State (wang, xiang, etc.)
    private String monthElement;
  }

  @Data
  @Builder
  public static class TenGodsAnalysis {
    private Map<String, TenGodInfo> gods; // TenGod Name -> Info

    @Data
    @Builder
    public static class TenGodInfo {
      private String name;
      private int count;
      private List<String> positions;
    }
  }

  @Data
  @Builder
  public static class PatternInfo {
    private String name;
    private String category; // normal, special
    private String description;
    private String monthStem; // Optional
    private String monthStemTenGod; // Optional
    private boolean isTransparent; // Optional
  }

  // Add these fields to the main class
  private TenGodsAnalysis tenGods;
  private PatternInfo pattern;
  private List<String> dayMasterCharacteristics;

  // New fields for full alignment
  private YunInfo yun;
  private ShenShaInfo shenSha;
  private String shengXiao;
  private String lunarDate; // formatted string
  private String taiYuan;
  private String mingGong;
  private String shenGong;

  @Data
  @Builder
  public static class YunInfo {
    private int startAge;
    private boolean forward;
    private List<DaYun> daYunList;
  }

  @Data
  @Builder
  public static class DaYun {
    private int index;
    private int startAge;
    private int endAge;
    private String ganZhi;
    private String gan;
    private String zhi;
    private int startYear;
    private int endYear;
    private List<LiuNian> liuNian;
  }

  @Data
  @Builder
  public static class LiuNian {
    private int year;
    private int age;
    private String ganZhi;
    private String gan;
    private String zhi;
  }

  @Data
  @Builder
  public static class ShenShaInfo {
    private List<String> year;
    private List<String> month;
    private List<String> day;
    private List<String> hour;
  }
}
