package com.tafu.bazi.mapper;

import com.tafu.bazi.dto.response.*;
import com.tafu.bazi.model.BaziResult.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Bazi 数据映射器（重构版）
 *
 * <p>职责：
 *
 * <ul>
 *   <li>将强类型 BaziResult 模型映射为 DTO
 *   <li>委托 MapToDtoMapper 处理 Map 数据（数据库读取场景）
 * </ul>
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Component
@RequiredArgsConstructor
public class BaziMapper {

  private final MapToDtoMapper mapToDtoMapper;

  /**
   * 映射单柱数据（从 Map 结构）
   *
   * @param pillarMap Map 结构的柱数据
   * @return PillarDTO
   */
  public PillarDTO mapPillar(Map<String, Object> pillarMap) {
    if (pillarMap == null) return null;

    @SuppressWarnings("unchecked")
    Map<String, Object> hsMap = (Map<String, Object>) pillarMap.get("heavenlyStem");
    @SuppressWarnings("unchecked")
    Map<String, Object> ebMap = (Map<String, Object>) pillarMap.get("earthlyBranch");
    @SuppressWarnings("unchecked")
    List<Map<String, String>> hiddenStemsRaw =
        (List<Map<String, String>>) pillarMap.get("hiddenStems");

    HeavenlyStemDTO heavenlyStem =
        HeavenlyStemDTO.builder()
            .chinese((String) hsMap.get("chinese"))
            .element((String) hsMap.get("element"))
            .yinYang((String) hsMap.get("yinYang"))
            .build();

    EarthlyBranchDTO earthlyBranch =
        EarthlyBranchDTO.builder()
            .chinese((String) ebMap.get("chinese"))
            .element((String) ebMap.get("element"))
            .build();

    // 将藏干 Map 列表转换为 HiddenStemDTO 列表
    List<HiddenStemDTO> hiddenStems = null;
    if (hiddenStemsRaw != null) {
      hiddenStems =
          hiddenStemsRaw.stream()
              .map(
                  stemMap ->
                      HiddenStemDTO.builder()
                          .chinese(stemMap.get("chinese"))
                          .element(stemMap.get("element"))
                          .yinYang(stemMap.get("yinYang"))
                          .tenGod(stemMap.get("tenGod"))
                          .build())
              .collect(Collectors.toList());
    }

    return PillarDTO.builder()
        .heavenlyStem(heavenlyStem)
        .earthlyBranch(earthlyBranch)
        .naYin((String) pillarMap.get("naYin"))
        .hiddenStems(hiddenStems)
        .tenGod((String) pillarMap.get("tenGod"))
        .build();
  }

  /**
   * 映射四柱数据
   *
   * @param fourPillarsMap Map 结构的四柱数据
   * @return FourPillarsDTO
   */
  public FourPillarsDTO mapFourPillars(Map<String, Object> fourPillarsMap) {
    if (fourPillarsMap == null) return null;

    @SuppressWarnings("unchecked")
    Map<String, Object> yearMap = (Map<String, Object>) fourPillarsMap.get("year");
    @SuppressWarnings("unchecked")
    Map<String, Object> monthMap = (Map<String, Object>) fourPillarsMap.get("month");
    @SuppressWarnings("unchecked")
    Map<String, Object> dayMap = (Map<String, Object>) fourPillarsMap.get("day");
    @SuppressWarnings("unchecked")
    Map<String, Object> hourMap = (Map<String, Object>) fourPillarsMap.get("hour");

    return FourPillarsDTO.builder()
        .year(mapPillar(yearMap))
        .month(mapPillar(monthMap))
        .day(mapPillar(dayMap))
        .hour(mapPillar(hourMap))
        .build();
  }

  /**
   * 映射日主分析数据（重构版）
   *
   * <p>使用方法重载处理不同输入类型
   */
  public DayMasterDTO mapDayMaster(DayMaster dayMaster) {
    if (dayMaster == null) return null;

    DayMasterAnalysis analysis = dayMaster.getAnalysis();
    DayMasterAnalysisDTO analysisDTO = null;

    if (analysis != null) {
      analysisDTO =
          DayMasterAnalysisDTO.builder()
              .deLing(analysis.getDeLing())
              .deLingDesc(analysis.getDeLingDesc())
              .deDi(analysis.getDeDi())
              .deDiDesc(analysis.getDeDiDesc())
              .tianGanHelp(analysis.getTianGanHelp())
              .tianGanHelpDesc(analysis.getTianGanHelpDesc())
              .totalScore(analysis.getTotalScore())
              .build();
    }

    return DayMasterDTO.builder()
        .gan(dayMaster.getGan())
        .strength(dayMaster.getStrength())
        .analysis(analysisDTO)
        .build();
  }

  /** 从 Map 映射日主数据（委托给专用 Mapper） */
  @SuppressWarnings("unchecked")
  public DayMasterDTO mapDayMasterFromMap(Map<String, Object> map) {
    return mapToDtoMapper.mapDayMaster(map);
  }

  /** 多态入口（兼容旧代码） */
  @SuppressWarnings("unchecked")
  public DayMasterDTO mapDayMaster(Object dayMasterObj) {
    if (dayMasterObj == null) return null;
    if (dayMasterObj instanceof DayMaster) {
      return mapDayMaster((DayMaster) dayMasterObj);
    }
    if (dayMasterObj instanceof Map) {
      return mapDayMasterFromMap((Map<String, Object>) dayMasterObj);
    }
    throw new IllegalArgumentException("Unsupported type: " + dayMasterObj.getClass().getName());
  }

  /** 映射五行分析数据 */
  public FiveElementsDTO mapFiveElements(FiveElementsAnalysis fiveElements) {
    if (fiveElements == null) return null;

    return FiveElementsDTO.builder()
        .distribution(fiveElements.getDistribution())
        .counts(fiveElements.getCounts())
        .strongest(fiveElements.getStrongest())
        .weakest(fiveElements.getWeakest())
        .favorable(fiveElements.getFavorable())
        .unfavorable(fiveElements.getUnfavorable())
        .elementStates(fiveElements.getElementStates())
        .monthElement(fiveElements.getMonthElement())
        .build();
  }

  /** 多态入口 */
  @SuppressWarnings("unchecked")
  public FiveElementsDTO mapFiveElements(Object fiveElementsObj) {
    if (fiveElementsObj == null) return null;
    if (fiveElementsObj instanceof FiveElementsAnalysis) {
      return mapFiveElements((FiveElementsAnalysis) fiveElementsObj);
    }
    if (fiveElementsObj instanceof Map) {
      return mapToDtoMapper.mapFiveElements((Map<String, Object>) fiveElementsObj);
    }
    throw new IllegalArgumentException("Unsupported type: " + fiveElementsObj.getClass().getName());
  }

  /**
   * 映射十神信息
   *
   * @param tenGodInfo TenGodInfo 模型
   * @return TenGodInfoDTO
   */
  public TenGodInfoDTO mapTenGodInfo(TenGodsAnalysis.TenGodInfo tenGodInfo) {
    if (tenGodInfo == null) return null;

    return TenGodInfoDTO.builder()
        .name(tenGodInfo.getName())
        .count(tenGodInfo.getCount())
        .positions(tenGodInfo.getPositions())
        .build();
  }

  /** 映射十神分析数据 */
  public TenGodsDTO mapTenGods(TenGodsAnalysis tenGods) {
    if (tenGods == null || tenGods.getGods() == null) return null;

    Map<String, TenGodInfoDTO> godsDTOMap =
        tenGods.getGods().entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> mapTenGodInfo(e.getValue())));

    return TenGodsDTO.builder().gods(godsDTOMap).build();
  }

  /** 多态入口 */
  @SuppressWarnings("unchecked")
  public TenGodsDTO mapTenGods(Object tenGodsObj) {
    if (tenGodsObj == null) return null;
    if (tenGodsObj instanceof TenGodsAnalysis) {
      return mapTenGods((TenGodsAnalysis) tenGodsObj);
    }
    if (tenGodsObj instanceof Map) {
      return mapToDtoMapper.mapTenGods((Map<String, Object>) tenGodsObj);
    }
    throw new IllegalArgumentException("Unsupported type: " + tenGodsObj.getClass().getName());
  }

  /** 映射格局信息 */
  public PatternDTO mapPattern(PatternInfo pattern) {
    if (pattern == null) return null;

    return PatternDTO.builder()
        .name(pattern.getName())
        .category(pattern.getCategory())
        .description(pattern.getDescription())
        .monthStem(pattern.getMonthStem())
        .monthStemTenGod(pattern.getMonthStemTenGod())
        .isTransparent(pattern.isTransparent())
        .build();
  }

  /** 多态入口 */
  @SuppressWarnings("unchecked")
  public PatternDTO mapPattern(Object patternObj) {
    if (patternObj == null) return null;
    if (patternObj instanceof PatternInfo) {
      return mapPattern((PatternInfo) patternObj);
    }
    if (patternObj instanceof Map) {
      return mapToDtoMapper.mapPattern((Map<String, Object>) patternObj);
    }
    throw new IllegalArgumentException("Unsupported type: " + patternObj.getClass().getName());
  }

  /**
   * 映射大运数据（添加 gan/zhi 字段和流年列表）
   *
   * @param daYun DaYun 模型
   * @return DaYunDTO
   */
  public DaYunDTO mapDaYun(DaYun daYun) {
    if (daYun == null) return null;

    String ganZhi = daYun.getGanZhi();
    String gan = daYun.getGan();
    String zhi = daYun.getZhi();

    // 映射流年列表
    List<LiuNianDTO> liuNianList =
        daYun.getLiuNian() != null
            ? daYun.getLiuNian().stream().map(this::mapLiuNian).collect(Collectors.toList())
            : null;

    return DaYunDTO.builder()
        .index(daYun.getIndex())
        .startAge(daYun.getStartAge())
        .endAge(daYun.getEndAge())
        .ganZhi(ganZhi)
        .gan(gan)
        .zhi(zhi)
        .startYear(daYun.getStartYear())
        .endYear(daYun.getEndYear())
        .liuNian(liuNianList)
        .build();
  }

  /**
   * 映射流年数据
   *
   * @param liuNian LiuNian 模型
   * @return LiuNianDTO
   */
  public LiuNianDTO mapLiuNian(LiuNian liuNian) {
    if (liuNian == null) return null;

    return LiuNianDTO.builder()
        .year(liuNian.getYear())
        .age(liuNian.getAge())
        .ganZhi(liuNian.getGanZhi())
        .gan(liuNian.getGan())
        .zhi(liuNian.getZhi())
        .build();
  }

  /** 映射大运信息 */
  public YunInfoDTO mapYunInfo(YunInfo yunInfo) {
    if (yunInfo == null) return null;

    List<DaYunDTO> daYunList =
        yunInfo.getDaYunList() != null
            ? yunInfo.getDaYunList().stream().map(this::mapDaYun).collect(Collectors.toList())
            : null;

    return YunInfoDTO.builder()
        .startAge(yunInfo.getStartAge())
        .forward(yunInfo.isForward())
        .daYunList(daYunList)
        .build();
  }

  /** 多态入口 */
  @SuppressWarnings("unchecked")
  public YunInfoDTO mapYunInfo(Object yunInfoObj) {
    if (yunInfoObj == null) return null;
    if (yunInfoObj instanceof YunInfo) {
      return mapYunInfo((YunInfo) yunInfoObj);
    }
    if (yunInfoObj instanceof Map) {
      return mapToDtoMapper.mapYunInfo((Map<String, Object>) yunInfoObj);
    }
    throw new IllegalArgumentException("Unsupported type: " + yunInfoObj.getClass().getName());
  }

  /** 映射神煞信息 */
  public ShenShaDTO mapShenSha(ShenShaInfo shenSha) {
    if (shenSha == null) return null;

    return ShenShaDTO.builder()
        .year(shenSha.getYear())
        .month(shenSha.getMonth())
        .day(shenSha.getDay())
        .hour(shenSha.getHour())
        .build();
  }

  /** 多态入口 */
  @SuppressWarnings("unchecked")
  public ShenShaDTO mapShenSha(Object shenShaObj) {
    if (shenShaObj == null) return null;
    if (shenShaObj instanceof ShenShaInfo) {
      return mapShenSha((ShenShaInfo) shenShaObj);
    }
    if (shenShaObj instanceof Map) {
      return mapToDtoMapper.mapShenSha((Map<String, Object>) shenShaObj);
    }
    throw new IllegalArgumentException("Unsupported type: " + shenShaObj.getClass().getName());
  }

  /**
   * 映射真太阳时信息
   *
   * @param trueSolarTimeMap Map 结构的真太阳时数据
   * @return TrueSolarTimeDTO
   */
  public TrueSolarTimeDTO mapTrueSolarTime(Map<String, Object> trueSolarTimeMap) {
    if (trueSolarTimeMap == null) return null;

    return TrueSolarTimeDTO.builder()
        .year((Integer) trueSolarTimeMap.get("year"))
        .month((Integer) trueSolarTimeMap.get("month"))
        .day((Integer) trueSolarTimeMap.get("day"))
        .hour((Integer) trueSolarTimeMap.get("hour"))
        .minute((Integer) trueSolarTimeMap.get("minute"))
        .build();
  }

  /**
   * 映射完整的八字响应数据
   *
   * @param resultMap 从 BaziServiceImpl.calculate() 返回的 Map 或从数据库读取的 Map
   * @return BaziResponse
   */
  @SuppressWarnings("unchecked")
  public BaziResponse mapToBaziResponse(Map<String, Object> resultMap) {
    if (resultMap == null) return null;

    return BaziResponse.builder()
        .gender((String) resultMap.get("gender"))
        .solarDate((String) resultMap.get("solarDate"))
        .lunarDate((String) resultMap.get("lunarDate"))
        .trueSolarTime(mapTrueSolarTime((Map<String, Object>) resultMap.get("trueSolarTime")))
        .fourPillars(mapFourPillars((Map<String, Object>) resultMap.get("fourPillars")))
        .fourPillarsShiShen((Map<String, String>) resultMap.get("fourPillarsShiShen"))
        .fourPillarsXunKong((Map<String, String>) resultMap.get("fourPillarsXunKong"))
        .dayMaster(mapDayMaster(resultMap.get("dayMaster")))
        .fiveElements(mapFiveElements(resultMap.get("fiveElements")))
        .tenGods(mapTenGods(resultMap.get("tenGods")))
        .pattern(mapPattern(resultMap.get("pattern")))
        .yun(mapYunInfo(resultMap.get("yun")))
        .shenSha(mapShenSha(resultMap.get("shenSha")))
        .shengXiao((String) resultMap.get("shengXiao"))
        .taiYuan((String) resultMap.get("taiYuan"))
        .mingGong((String) resultMap.get("mingGong"))
        .shenGong((String) resultMap.get("shenGong"))
        .xunKong((String) resultMap.get("xunKong"))
        .dayMasterCharacteristics((List<String>) resultMap.get("dayMasterCharacteristics"))
        .build();
  }
}
