package com.tafu.bazi.model;

import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * BaziDef
 *
 * <p>描述: 八字基础定义 (天干、地支、五行等常量与枚举)。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
public class BaziDef {

  // ===================================
  // 枚举定义
  // ===================================

  @Getter
  @AllArgsConstructor
  public enum FiveElement {
    METAL("metal", "金"),
    WOOD("wood", "木"),
    WATER("water", "水"),
    FIRE("fire", "火"),
    EARTH("earth", "土");

    private final String code;
    private final String chinese;

    public static FiveElement fromCode(String code) {
      return Arrays.stream(values()).filter(e -> e.code.equals(code)).findFirst().orElse(null);
    }
  }

  @Getter
  @AllArgsConstructor
  public enum YinYang {
    YIN("yin", "阴"),
    YANG("yang", "阳");

    private final String code;
    private final String chinese;
  }

  // ===================================
  // 静态常量映射
  // ===================================

  // 五行相生: Key生Value
  public static final Map<FiveElement, FiveElement> FIVE_ELEMENTS_GENERATION =
      Map.of(
          FiveElement.WOOD, FiveElement.FIRE,
          FiveElement.FIRE, FiveElement.EARTH,
          FiveElement.EARTH, FiveElement.METAL,
          FiveElement.METAL, FiveElement.WATER,
          FiveElement.WATER, FiveElement.WOOD);

  // 五行相克: Key克Value
  public static final Map<FiveElement, FiveElement> FIVE_ELEMENTS_RESTRICTION =
      Map.of(
          FiveElement.WOOD, FiveElement.EARTH,
          FiveElement.EARTH, FiveElement.WATER,
          FiveElement.WATER, FiveElement.FIRE,
          FiveElement.FIRE, FiveElement.METAL,
          FiveElement.METAL, FiveElement.WOOD);

  // 五行被生: Value生Key (反向查找)
  public static final Map<FiveElement, FiveElement> FIVE_ELEMENTS_GENERATED_BY =
      Map.of(
          FiveElement.WOOD, FiveElement.WATER,
          FiveElement.FIRE, FiveElement.WOOD,
          FiveElement.EARTH, FiveElement.FIRE,
          FiveElement.METAL, FiveElement.EARTH,
          FiveElement.WATER, FiveElement.METAL);

  // 月支本气五行 (用于得令判断)
  public static final Map<String, FiveElement> MONTH_BRANCH_ELEMENT = new HashMap<>();

  static {
    MONTH_BRANCH_ELEMENT.put("寅", FiveElement.WOOD);
    MONTH_BRANCH_ELEMENT.put("卯", FiveElement.WOOD);
    MONTH_BRANCH_ELEMENT.put("辰", FiveElement.EARTH);
    MONTH_BRANCH_ELEMENT.put("巳", FiveElement.FIRE);
    MONTH_BRANCH_ELEMENT.put("午", FiveElement.FIRE);
    MONTH_BRANCH_ELEMENT.put("未", FiveElement.EARTH);
    MONTH_BRANCH_ELEMENT.put("申", FiveElement.METAL);
    MONTH_BRANCH_ELEMENT.put("酉", FiveElement.METAL);
    MONTH_BRANCH_ELEMENT.put("戌", FiveElement.EARTH);
    MONTH_BRANCH_ELEMENT.put("亥", FiveElement.WATER);
    MONTH_BRANCH_ELEMENT.put("子", FiveElement.WATER);
    MONTH_BRANCH_ELEMENT.put("丑", FiveElement.EARTH);
  }

  // 藏干权重 (本气/中气/余气)
  public static final Map<String, List<Double>> HIDDEN_STEM_WEIGHTS = new HashMap<>();

  static {
    HIDDEN_STEM_WEIGHTS.put("子", List.of(1.0));
    HIDDEN_STEM_WEIGHTS.put("丑", List.of(0.6, 0.2, 0.2));
    HIDDEN_STEM_WEIGHTS.put("寅", List.of(0.6, 0.2, 0.2));
    HIDDEN_STEM_WEIGHTS.put("卯", List.of(1.0));
    HIDDEN_STEM_WEIGHTS.put("辰", List.of(0.6, 0.2, 0.2));
    HIDDEN_STEM_WEIGHTS.put("巳", List.of(0.6, 0.2, 0.2));
    HIDDEN_STEM_WEIGHTS.put("午", List.of(0.7, 0.3));
    HIDDEN_STEM_WEIGHTS.put("未", List.of(0.6, 0.2, 0.2));
    HIDDEN_STEM_WEIGHTS.put("申", List.of(0.6, 0.2, 0.2));
    HIDDEN_STEM_WEIGHTS.put("酉", List.of(1.0));
    HIDDEN_STEM_WEIGHTS.put("戌", List.of(0.6, 0.2, 0.2));
    HIDDEN_STEM_WEIGHTS.put("亥", List.of(0.7, 0.3));
  }

  // 五行状态权重 (旺相休囚死)
  public static final Map<String, Double> STATE_WEIGHTS =
      Map.of(
          "wang", 1.5,
          "xiang", 1.2,
          "xiu", 1.0,
          "qiu", 0.7,
          "si", 0.5);

  // 天干基础属性表 (Map for quick lookup)
  @Data
  @AllArgsConstructor
  public static class StemInfo {
    private String chinese;
    private FiveElement element;
    private YinYang yinYang;
  }

  public static final Map<String, StemInfo> STEMS_INFO = new HashMap<>();

  static {
    STEMS_INFO.put("甲", new StemInfo("甲", FiveElement.WOOD, YinYang.YANG));
    STEMS_INFO.put("乙", new StemInfo("乙", FiveElement.WOOD, YinYang.YIN));
    STEMS_INFO.put("丙", new StemInfo("丙", FiveElement.FIRE, YinYang.YANG));
    STEMS_INFO.put("丁", new StemInfo("丁", FiveElement.FIRE, YinYang.YIN));
    STEMS_INFO.put("戊", new StemInfo("戊", FiveElement.EARTH, YinYang.YANG));
    STEMS_INFO.put("己", new StemInfo("己", FiveElement.EARTH, YinYang.YIN));
    STEMS_INFO.put("庚", new StemInfo("庚", FiveElement.METAL, YinYang.YANG));
    STEMS_INFO.put("辛", new StemInfo("辛", FiveElement.METAL, YinYang.YIN));
    STEMS_INFO.put("壬", new StemInfo("壬", FiveElement.WATER, YinYang.YANG));
    STEMS_INFO.put("癸", new StemInfo("癸", FiveElement.WATER, YinYang.YIN));
  }

  // 十神
  public static final List<String> TEN_GODS =
      List.of("比肩", "劫财", "食神", "伤官", "偏财", "正财", "七杀", "正官", "偏印", "正印");
}
