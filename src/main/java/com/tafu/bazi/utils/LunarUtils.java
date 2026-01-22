package com.tafu.bazi.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

/**
 * LunarUtils
 *
 * <p>描述: 农历/八字辅助工具类。 包含: 藏干映射、五行属性、城市经度查询。 核心升级: 1. 从 city-geo-data.json 加载完整经度数据 (Ported from
 * geo-utils.ts) 2. 实现省市区三级匹配及模糊匹配
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Slf4j
public class LunarUtils {

  /** 地支藏干映射表 */
  private static final Map<String, List<String>> HIDDEN_STEMS_MAP = new HashMap<>();

  static {
    HIDDEN_STEMS_MAP.put("子", List.of("癸"));
    HIDDEN_STEMS_MAP.put("丑", List.of("己", "癸", "辛"));
    HIDDEN_STEMS_MAP.put("寅", List.of("甲", "丙", "戊"));
    HIDDEN_STEMS_MAP.put("卯", List.of("乙"));
    HIDDEN_STEMS_MAP.put("辰", List.of("戊", "乙", "癸"));
    HIDDEN_STEMS_MAP.put("巳", List.of("丙", "庚", "戊"));
    HIDDEN_STEMS_MAP.put("午", List.of("丁", "己"));
    HIDDEN_STEMS_MAP.put("未", List.of("己", "丁", "乙"));
    HIDDEN_STEMS_MAP.put("申", List.of("庚", "壬", "戊"));
    HIDDEN_STEMS_MAP.put("酉", List.of("辛"));
    HIDDEN_STEMS_MAP.put("戌", List.of("戊", "辛", "丁"));
    HIDDEN_STEMS_MAP.put("亥", List.of("壬", "甲"));
  }

  // 经度数据缓存
  private static List<CityGeoItem> cityGeoData = new ArrayList<>();
  private static final Map<String, Double> areaIndex = new HashMap<>();
  private static final Map<String, Double> cityIndex = new HashMap<>();
  private static final Map<String, Double> provinceIndex = new HashMap<>();

  // 默认经度 (北京)
  private static final double DEFAULT_LONGITUDE = 116.4;

  // 额外补充数据 (from geo-utils.ts)
  private static final Map<String, Double> EXTRA_LONGITUDES =
      Map.ofEntries(
          Map.entry("香港特别行政区", 114.2),
          Map.entry("澳门特别行政区", 113.5),
          Map.entry("台湾省", 121.5),
          Map.entry("金门县", 118.3774),
          Map.entry("澳门半岛", 113.5429)
          // ... simplified list, add more if critically needed
          );

  // 静态初始化块加载 JSON
  static {
    try {
      ObjectMapper mapper = new ObjectMapper();
      ClassPathResource resource = new ClassPathResource("city-geo-data.json");
      if (resource.exists()) {
        cityGeoData =
            mapper.readValue(resource.getInputStream(), new TypeReference<List<CityGeoItem>>() {});
        initIndexes();
      } else {
        log.warn("city-geo-data.json not found in classpath");
      }
    } catch (IOException e) {
      log.error("Failed to load city-geo-data.json", e);
    }
  }

  private static void initIndexes() {
    for (CityGeoItem item : cityGeoData) {
      try {
        double lng = Double.parseDouble(item.getLng());
        if (!provinceIndex.containsKey(item.getProvince()))
          provinceIndex.put(item.getProvince(), lng);
        if (!cityIndex.containsKey(item.getCity())) cityIndex.put(item.getCity(), lng);
        if (!areaIndex.containsKey(item.getArea())) areaIndex.put(item.getArea(), lng);
      } catch (NumberFormatException ignored) {
      }
    }

    EXTRA_LONGITUDES.forEach(
        (name, lng) -> {
          areaIndex.putIfAbsent(name, lng);
          cityIndex.putIfAbsent(name, lng);
          if (name.matches(".*(省|自治区|特别行政区)$")) {
            provinceIndex.putIfAbsent(name, lng);
          }
        });
  }

  /** 获取地支藏干 */
  public static List<String> getHiddenStems(String zhi) {
    return HIDDEN_STEMS_MAP.getOrDefault(zhi, Collections.emptyList());
  }

  /** 获取城市经度 (Full implementation ported from geo-utils.ts) */
  public static double getLongitude(String location) {
    if (location == null || location.isBlank()) return DEFAULT_LONGITUDE;

    // 尝试按 "/" 分割（三级结构）
    String[] parts = location.split("/");

    if (parts.length >= 3) {
      double lng = findLongitudeByName(parts[2].trim(), "area");
      if (lng != DEFAULT_LONGITUDE) return lng;
    }

    if (parts.length >= 2) {
      double lng = findLongitudeByName(parts[1].trim(), "city");
      if (lng != DEFAULT_LONGITUDE) return lng;
    }

    if (parts.length >= 1) {
      String name = parts[0].trim();
      double lng = findLongitudeByName(name, "area");
      if (lng != DEFAULT_LONGITUDE) return lng;

      lng = findLongitudeByName(name, "city");
      if (lng != DEFAULT_LONGITUDE) return lng;

      lng = findLongitudeByName(name, "province");
      if (lng != DEFAULT_LONGITUDE) return lng;
    }

    // 最后尝试模糊匹配
    return fuzzyMatch(location);
  }

  private static double findLongitudeByName(String name, String type) {
    if (name == null || name.isBlank()) return DEFAULT_LONGITUDE;

    Map<String, Double> index =
        switch (type) {
          case "province" -> provinceIndex;
          case "city" -> cityIndex;
          default -> areaIndex;
        };

    if (index.containsKey(name)) return index.get(name);

    String[] suffixes = {"市", "区", "县", "地区", "州", "盟"};

    // 尝试添加后缀
    for (String suffix : suffixes) {
      if (!name.endsWith(suffix)) {
        if (index.containsKey(name + suffix)) return index.get(name + suffix);
      }
    }

    // 尝试移除后缀
    for (String suffix : suffixes) {
      if (name.endsWith(suffix)) {
        String withoutSuffix = name.substring(0, name.length() - suffix.length());
        if (index.containsKey(withoutSuffix)) return index.get(withoutSuffix);
        // 替换后缀
        for (String otherSuffix : suffixes) {
          if (!otherSuffix.equals(suffix)) {
            if (index.containsKey(withoutSuffix + otherSuffix))
              return index.get(withoutSuffix + otherSuffix);
          }
        }
      }
    }

    return DEFAULT_LONGITUDE;
  }

  private static double fuzzyMatch(String location) {
    for (CityGeoItem item : cityGeoData) {
      try {
        double lng = Double.parseDouble(item.getLng());
        if (item.getArea() != null
            && (location.contains(item.getArea()) || item.getArea().contains(location))) return lng;
        if (item.getCity() != null
            && (location.contains(item.getCity()) || item.getCity().contains(location))) return lng;
      } catch (NumberFormatException ignored) {
      }
    }
    return DEFAULT_LONGITUDE;
  }

  @Data
  public static class CityGeoItem {
    private String area;
    private String city;
    private String province;
    private String lat;
    private String lng;
    private String country;
  }
}
