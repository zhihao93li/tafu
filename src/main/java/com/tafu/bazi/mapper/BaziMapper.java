package com.tafu.bazi.mapper;

import com.tafu.bazi.dto.response.*;
import com.tafu.bazi.model.BaziResult.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Bazi 数据映射器
 *
 * <p>将内部 BaziResult 模型映射为 DTO 响应对象
 *
 * @author Zhihao Li
 * @since 2026-01-26
 */
@Component
public class BaziMapper {

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
   * 映射日主分析数据
   *
   * @param dayMaster DayMaster 模型
   * @return DayMasterDTO
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

  /**
   * 映射五行分析数据
   *
   * @param fiveElements FiveElementsAnalysis 模型
   * @return FiveElementsDTO
   */
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

  /**
   * 映射十神分析数据
   *
   * @param tenGods TenGodsAnalysis 模型
   * @return TenGodsDTO
   */
  public TenGodsDTO mapTenGods(TenGodsAnalysis tenGods) {
    if (tenGods == null || tenGods.getGods() == null) return null;

    Map<String, TenGodInfoDTO> godsDTOMap =
        tenGods.getGods().entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> mapTenGodInfo(e.getValue())));

    return TenGodsDTO.builder().gods(godsDTOMap).build();
  }

  /**
   * 映射格局信息
   *
   * @param pattern PatternInfo 模型
   * @return PatternDTO
   */
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

  /**
   * 映射大运信息
   *
   * @param yunInfo YunInfo 模型
   * @return YunInfoDTO
   */
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

  /**
   * 映射神煞信息
   *
   * @param shenSha ShenShaInfo 模型
   * @return ShenShaDTO
   */
  public ShenShaDTO mapShenSha(ShenShaInfo shenSha) {
    if (shenSha == null) return null;

    return ShenShaDTO.builder()
        .year(shenSha.getYear())
        .month(shenSha.getMonth())
        .day(shenSha.getDay())
        .hour(shenSha.getHour())
        .build();
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
   * @param resultMap 从 BaziServiceImpl.calculate() 返回的 Map
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
        .dayMaster(mapDayMaster((DayMaster) resultMap.get("dayMaster")))
        .fiveElements(mapFiveElements((FiveElementsAnalysis) resultMap.get("fiveElements")))
        .tenGods(mapTenGods((TenGodsAnalysis) resultMap.get("tenGods")))
        .pattern(mapPattern((PatternInfo) resultMap.get("pattern")))
        .yun(mapYunInfo((YunInfo) resultMap.get("yun")))
        .shenSha(mapShenSha((ShenShaInfo) resultMap.get("shenSha")))
        .shengXiao((String) resultMap.get("shengXiao"))
        .taiYuan((String) resultMap.get("taiYuan"))
        .mingGong((String) resultMap.get("mingGong"))
        .shenGong((String) resultMap.get("shenGong"))
        .xunKong((String) resultMap.get("xunKong"))
        .dayMasterCharacteristics((List<String>) resultMap.get("dayMasterCharacteristics"))
        .build();
  }
}
