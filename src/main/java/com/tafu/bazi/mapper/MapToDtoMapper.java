package com.tafu.bazi.mapper;

import com.tafu.bazi.dto.response.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Map 到 DTO 转换器
 *
 * <p>专门处理从数据库 JSONB 反序列化的 Map 数据
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Component
public class MapToDtoMapper {

  /** 从 Map 构建 DayMasterDTO */
  @SuppressWarnings("unchecked")
  public DayMasterDTO mapDayMaster(Map<String, Object> map) {
    if (map == null) return null;

    String gan = getStringValue(map, "gan");
    String strength = getStringValue(map, "strength");
    Map<String, Object> analysisMap = (Map<String, Object>) map.get("analysis");

    DayMasterAnalysisDTO analysisDTO = null;
    if (analysisMap != null) {
      analysisDTO =
          DayMasterAnalysisDTO.builder()
              .deLing(getDoubleValue(analysisMap, "deLing"))
              .deLingDesc(getStringValue(analysisMap, "deLingDesc"))
              .deDi(getDoubleValue(analysisMap, "deDi"))
              .deDiDesc(getStringValue(analysisMap, "deDiDesc"))
              .tianGanHelp(getDoubleValue(analysisMap, "tianGanHelp"))
              .tianGanHelpDesc(getStringValue(analysisMap, "tianGanHelpDesc"))
              .totalScore(getDoubleValue(analysisMap, "totalScore"))
              .build();
    }

    return DayMasterDTO.builder().gan(gan).strength(strength).analysis(analysisDTO).build();
  }

  /** 从 Map 构建 FiveElementsDTO */
  @SuppressWarnings("unchecked")
  public FiveElementsDTO mapFiveElements(Map<String, Object> map) {
    if (map == null) return null;

    return FiveElementsDTO.builder()
        .distribution((Map<String, Double>) map.get("distribution"))
        .counts((Map<String, Integer>) map.get("counts"))
        .strongest(getStringValue(map, "strongest"))
        .weakest(getStringValue(map, "weakest"))
        .favorable((List<String>) map.get("favorable"))
        .unfavorable((List<String>) map.get("unfavorable"))
        .elementStates((Map<String, String>) map.get("elementStates"))
        .monthElement(getStringValue(map, "monthElement"))
        .build();
  }

  /** 从 Map 构建 TenGodsDTO */
  @SuppressWarnings("unchecked")
  public TenGodsDTO mapTenGods(Map<String, Object> map) {
    if (map == null) return null;

    Map<String, Object> godsMap = (Map<String, Object>) map.get("gods");
    if (godsMap == null) return null;

    Map<String, TenGodInfoDTO> godsDTOMap =
        godsMap.entrySet().stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey, e -> mapTenGodInfo((Map<String, Object>) e.getValue())));

    return TenGodsDTO.builder().gods(godsDTOMap).build();
  }

  /** 从 Map 构建 TenGodInfoDTO */
  @SuppressWarnings("unchecked")
  private TenGodInfoDTO mapTenGodInfo(Map<String, Object> map) {
    if (map == null) return null;
    return TenGodInfoDTO.builder()
        .name(getStringValue(map, "name"))
        .count(getIntValue(map, "count"))
        .positions((List<String>) map.get("positions"))
        .build();
  }

  /** 从 Map 构建 PatternDTO */
  public PatternDTO mapPattern(Map<String, Object> map) {
    if (map == null) return null;

    return PatternDTO.builder()
        .name(getStringValue(map, "name"))
        .category(getStringValue(map, "category"))
        .description(getStringValue(map, "description"))
        .monthStem(getStringValue(map, "monthStem"))
        .monthStemTenGod(getStringValue(map, "monthStemTenGod"))
        .isTransparent(getBooleanValue(map, "transparent", false))
        .build();
  }

  /** 从 Map 构建 YunInfoDTO */
  @SuppressWarnings("unchecked")
  public YunInfoDTO mapYunInfo(Map<String, Object> map) {
    if (map == null) return null;

    List<Map<String, Object>> daYunListRaw = (List<Map<String, Object>>) map.get("daYunList");
    List<DaYunDTO> daYunList =
        daYunListRaw != null
            ? daYunListRaw.stream().map(this::mapDaYun).collect(Collectors.toList())
            : null;

    return YunInfoDTO.builder()
        .startAge(getIntValue(map, "startAge"))
        .forward(getBooleanValue(map, "forward", false))
        .daYunList(daYunList)
        .build();
  }

  /** 从 Map 构建 DaYunDTO */
  @SuppressWarnings("unchecked")
  public DaYunDTO mapDaYun(Map<String, Object> map) {
    if (map == null) return null;

    List<Map<String, Object>> liuNianListRaw = (List<Map<String, Object>>) map.get("liuNian");
    List<LiuNianDTO> liuNianList =
        liuNianListRaw != null
            ? liuNianListRaw.stream().map(this::mapLiuNian).collect(Collectors.toList())
            : null;

    return DaYunDTO.builder()
        .index(getIntValue(map, "index"))
        .startAge(getIntValue(map, "startAge"))
        .endAge(getIntValue(map, "endAge"))
        .ganZhi(getStringValue(map, "ganZhi"))
        .gan(getStringValue(map, "gan"))
        .zhi(getStringValue(map, "zhi"))
        .startYear(getIntValue(map, "startYear"))
        .endYear(getIntValue(map, "endYear"))
        .liuNian(liuNianList)
        .build();
  }

  /** 从 Map 构建 LiuNianDTO */
  public LiuNianDTO mapLiuNian(Map<String, Object> map) {
    if (map == null) return null;

    return LiuNianDTO.builder()
        .year(getIntValue(map, "year"))
        .age(getIntValue(map, "age"))
        .ganZhi(getStringValue(map, "ganZhi"))
        .gan(getStringValue(map, "gan"))
        .zhi(getStringValue(map, "zhi"))
        .build();
  }

  /** 从 Map 构建 ShenShaDTO */
  @SuppressWarnings("unchecked")
  public ShenShaDTO mapShenSha(Map<String, Object> map) {
    if (map == null) return null;

    return ShenShaDTO.builder()
        .year((List<String>) map.get("year"))
        .month((List<String>) map.get("month"))
        .day((List<String>) map.get("day"))
        .hour((List<String>) map.get("hour"))
        .build();
  }

  // ==================== 工具方法 ====================

  private String getStringValue(Map<String, Object> map, String key) {
    Object value = map.get(key);
    return value != null ? value.toString() : null;
  }

  private int getIntValue(Map<String, Object> map, String key) {
    Object value = map.get(key);
    if (value == null) return 0;
    if (value instanceof Number) {
      return ((Number) value).intValue();
    }
    return 0;
  }

  private double getDoubleValue(Map<String, Object> map, String key) {
    Object value = map.get(key);
    if (value == null) return 0.0;
    if (value instanceof Number) {
      return ((Number) value).doubleValue();
    }
    return 0.0;
  }

  private boolean getBooleanValue(Map<String, Object> map, String key, boolean defaultValue) {
    Object value = map.get(key);
    if (value == null) return defaultValue;
    if (value instanceof Boolean) {
      return (Boolean) value;
    }
    return defaultValue;
  }
}
