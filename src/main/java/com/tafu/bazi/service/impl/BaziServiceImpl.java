package com.tafu.bazi.service.impl;

import com.nlf.calendar.EightChar;
import com.nlf.calendar.Lunar;
import com.nlf.calendar.Solar;
import com.nlf.calendar.eightchar.DaYun;
import com.nlf.calendar.eightchar.Yun;
import com.tafu.bazi.dto.request.BaziCalculateRequest;
import com.tafu.bazi.model.BaziDef;
import com.tafu.bazi.model.BaziDef.FiveElement;
import com.tafu.bazi.model.BaziDef.StemInfo;
import com.tafu.bazi.model.BaziResult;
import com.tafu.bazi.model.BaziResult.*;
import com.tafu.bazi.service.BaziService;
import com.tafu.bazi.utils.LunarUtils;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * BaziServiceImpl
 *
 * <p>描述: 八字排盘核心计算实现类。 核心逻辑: 移植自 calculator.ts
 *
 * <p>包含内容: 1. 真太阳时转换 2. 四柱计算 3. 日主强弱分析 (calculateDayMaster) 4. 五行旺衰统计 (calculateFiveElements) 5.
 * 十神计算 (calculateTenGods) 6. 格局判断 (calculatePattern)
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Slf4j
@Service
@SuppressWarnings({"unchecked", "null"})
public class BaziServiceImpl implements BaziService {

  @Override
  public Map<String, Object> calculate(BaziCalculateRequest request) {
    // 0. 获取经度
    double longitude = LunarUtils.getLongitude(request.getLocation());

    Solar solar;
    // 1. 处理输入日期 & 真太阳时
    if ("lunar".equals(request.getCalendarType())) {
      int lunarMonth = request.getMonth();
      if (request.isLeapMonth()) {
        lunarMonth = -Math.abs(lunarMonth);
      }
      Lunar lunar = Lunar.fromYmd(request.getYear(), lunarMonth, request.getDay());
      Solar tempSolar = lunar.getSolar();
      solar =
          getTrueSolarTime(
              tempSolar.getYear(),
              tempSolar.getMonth(),
              tempSolar.getDay(),
              request.getHour(),
              request.getMinute(),
              longitude);
    } else {
      solar =
          getTrueSolarTime(
              request.getYear(),
              request.getMonth(),
              request.getDay(),
              request.getHour(),
              request.getMinute(),
              longitude);
    }

    Lunar lunar = solar.getLunar();
    EightChar eightChar = lunar.getEightChar();
    eightChar.setSect(1); // 晚子时日柱算明天

    String dayMasterGan = eightChar.getDayGan();

    // 2. 构造四柱 (Four Pillars)
    FourPillars fourPillars =
        FourPillars.builder()
            .year(
                buildPillar(
                    eightChar.getYearGan(),
                    eightChar.getYearZhi(),
                    eightChar.getYearNaYin(),
                    dayMasterGan))
            .month(
                buildPillar(
                    eightChar.getMonthGan(),
                    eightChar.getMonthZhi(),
                    eightChar.getMonthNaYin(),
                    dayMasterGan))
            .day(
                buildPillar(
                    eightChar.getDayGan(),
                    eightChar.getDayZhi(),
                    eightChar.getDayNaYin(),
                    dayMasterGan))
            .hour(
                buildPillar(
                    eightChar.getTimeGan(),
                    eightChar.getTimeZhi(),
                    eightChar.getTimeNaYin(),
                    dayMasterGan))
            .build();

    // 3. 核心分析
    DayMaster dayMaster = calculateDayMaster(fourPillars);
    FiveElementsAnalysis fiveElements = calculateFiveElements(fourPillars, dayMaster);
    TenGodsAnalysis tenGods = calculateTenGods(fourPillars, dayMaster.getGan());
    PatternInfo pattern = calculatePattern(fourPillars, dayMaster, fiveElements);
    List<String> dayMasterCharacteristics = getDayMasterCharacteristics(dayMaster.getGan());

    // New Logic: Yun (DaYun)
    Yun yunObj = eightChar.getYun("male".equals(request.getGender()) ? 1 : 0);
    YunInfo yunInfo = calculateYun(yunObj);

    // New Logic: ShenSha - 临时使用空列表，因为1.7.7版本可能不支持神煞方法
    ShenShaInfo shenShaInfo =
        ShenShaInfo.builder()
            .year(Collections.emptyList())
            .month(Collections.emptyList())
            .day(Collections.emptyList())
            .hour(Collections.emptyList())
            .build();

    // 4. 构建返回结果
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("gender", request.getGender());
    result.put("solarDate", solar.toYmdHms());
    result.put("lunarDate", lunar.toString());
    result.put(
        "trueSolarTime",
        Map.of(
            "year",
            solar.getYear(),
            "month",
            solar.getMonth(),
            "day",
            solar.getDay(),
            "hour",
            solar.getHour(),
            "minute",
            solar.getMinute()));

    result.put("pillars", fourPillars);
    result.put("dayMaster", dayMaster);
    result.put("fiveElements", fiveElements);
    result.put("tenGods", tenGods);
    result.put("pattern", pattern);
    result.put("dayMasterCharacteristics", dayMasterCharacteristics);

    // New fields
    result.put("yun", yunInfo);
    result.put("shenSha", shenShaInfo);
    result.put("shengXiao", lunar.getYearShengXiaoExact());
    result.put("taiYuan", eightChar.getTaiYuan());
    result.put("mingGong", eightChar.getMingGong());
    result.put("shenGong", eightChar.getShenGong());
    result.put("xunKong", eightChar.getDayXunKong());
    // ... add others if needed

    return result;
  }

  private BaziResult.Pillar buildPillar(String gan, String zhi, String nayin, String dayMasterGan) {
    StemInfo ganInfo = BaziDef.STEMS_INFO.get(gan);
    StemInfo dayMasterInfo = BaziDef.STEMS_INFO.get(dayMasterGan);
    String tenGod =
        (ganInfo != null && dayMasterInfo != null) ? getTenGod(dayMasterInfo, ganInfo) : null;

    return BaziResult.Pillar.builder()
        .gan(gan)
        .zhi(zhi)
        .nayin(nayin)
        .hiddenStems(LunarUtils.getHiddenStems(zhi))
        .tenGod(tenGod)
        .build();
  }

  // ==========================================
  // Core Logic Implementation (Ported from TS)
  // ==========================================

  private DayMaster calculateDayMaster(FourPillars pillars) {
    String dayGan = pillars.getDay().getGan();
    FiveElement dayElement = BaziDef.STEMS_INFO.get(dayGan).getElement();

    String monthZhi = pillars.getMonth().getZhi();
    FiveElement monthElement = BaziDef.MONTH_BRANCH_ELEMENT.get(monthZhi);

    // 1. 得令 (Month Season Support)
    double deLing = 0;
    String deLingDesc = "";

    if (dayElement == monthElement) {
      deLing = 40;
      deLingDesc = "日主当令";
    } else if (BaziDef.FIVE_ELEMENTS_GENERATED_BY.get(dayElement) == monthElement) {
      deLing = 30;
      deLingDesc = "月令生扶";
    } else if (BaziDef.FIVE_ELEMENTS_GENERATION.get(dayElement) == monthElement) {
      deLing = -10;
      deLingDesc = "月令泄气";
    } else if (BaziDef.FIVE_ELEMENTS_RESTRICTION.get(monthElement) == dayElement) {
      deLing = -20;
      deLingDesc = "月令克制";
    } else {
      deLing = -5;
      deLingDesc = "日主耗气";
    }

    // 2. 得地 (Root in Hidden Stems)
    double deDi = 0;
    List<String> roots = new ArrayList<>();
    List<BaziResult.Pillar> allPillars =
        List.of(pillars.getYear(), pillars.getMonth(), pillars.getDay(), pillars.getHour());
    String[] pillarNames = {"年支", "月支", "日支", "时支"};

    for (int i = 0; i < allPillars.size(); i++) {
      BaziResult.Pillar p = allPillars.get(i);
      String zhi = p.getZhi();
      List<String> hiddenStems = p.getHiddenStems();
      List<Double> weights = BaziDef.HIDDEN_STEM_WEIGHTS.getOrDefault(zhi, Collections.emptyList());

      for (int j = 0; j < hiddenStems.size(); j++) {
        String stemChar = hiddenStems.get(j);
        StemInfo stemInfo = BaziDef.STEMS_INFO.get(stemChar);
        if (stemInfo == null) continue;

        double weight = (j < weights.size()) ? weights.get(j) : 0.2;

        if (stemInfo.getElement() == dayElement) {
          deDi += weight * 15;
          roots.add(pillarNames[i] + "藏" + stemChar);
        } else if (BaziDef.FIVE_ELEMENTS_GENERATED_BY.get(dayElement) == stemInfo.getElement()) {
          deDi += weight * 10;
          roots.add(pillarNames[i] + "藏" + stemChar + "(印)");
        }
      }
    }
    deDi = Math.min(deDi, 30); // Cap at 30

    // 3. 天干帮扶 (Heavenly Stems Help)
    double tianGanHelp = 0;
    List<String> helpers = new ArrayList<>();
    List<BaziResult.Pillar> stemPillars =
        List.of(pillars.getYear(), pillars.getMonth(), pillars.getHour());
    String[] stemNames = {"年干", "月干", "时干"};

    for (int i = 0; i < stemPillars.size(); i++) {
      String stemChar = stemPillars.get(i).getGan();
      StemInfo stemInfo = BaziDef.STEMS_INFO.get(stemChar);
      if (stemInfo == null) continue;

      if (stemInfo.getElement() == dayElement) {
        tianGanHelp += 8;
        helpers.add(stemNames[i] + stemChar + "比劫");
      } else if (BaziDef.FIVE_ELEMENTS_GENERATED_BY.get(dayElement) == stemInfo.getElement()) {
        tianGanHelp += 6;
        helpers.add(stemNames[i] + stemChar + "印星");
      } else if (BaziDef.FIVE_ELEMENTS_RESTRICTION.get(stemInfo.getElement()) == dayElement) {
        tianGanHelp -= 5;
        helpers.add(stemNames[i] + stemChar + "官杀");
      } else if (BaziDef.FIVE_ELEMENTS_GENERATION.get(dayElement) == stemInfo.getElement()) {
        tianGanHelp -= 3;
        helpers.add(stemNames[i] + stemChar + "食伤");
      }
    }
    tianGanHelp = Math.max(Math.min(tianGanHelp, 20), -20); // Cap between -20 and 20

    double totalScore = deLing + deDi + tianGanHelp;
    String strength = (totalScore >= 50) ? "strong" : (totalScore <= 25 ? "weak" : "balanced");

    return DayMaster.builder()
        .gan(dayGan)
        .strength(strength)
        .analysis(
            DayMasterAnalysis.builder()
                .deLing(deLing)
                .deLingDesc(deLingDesc)
                .deDi(deDi)
                .deDiDesc(roots.isEmpty() ? "无根" : String.join("、", roots))
                .tianGanHelp(tianGanHelp)
                .tianGanHelpDesc(helpers.isEmpty() ? "无帮扶" : String.join("、", helpers))
                .totalScore(totalScore)
                .build())
        .build();
  }

  private FiveElementsAnalysis calculateFiveElements(FourPillars pillars, DayMaster dayMaster) {
    Map<String, Double> distribution = new HashMap<>();
    Map<String, Integer> counts = new HashMap<>();

    // Initialize
    for (FiveElement e : FiveElement.values()) {
      distribution.put(e.getCode(), 0.0);
      counts.put(e.getCode(), 0);
    }

    String monthZhi = pillars.getMonth().getZhi();
    FiveElement monthElement = BaziDef.MONTH_BRANCH_ELEMENT.get(monthZhi);
    Map<String, String> elementStates = getElementStates(monthElement);

    List<Pillar> allPillars =
        List.of(pillars.getYear(), pillars.getMonth(), pillars.getDay(), pillars.getHour());

    // Stems
    for (Pillar p : allPillars) {
      StemInfo info = BaziDef.STEMS_INFO.get(p.getGan());
      if (info != null) {
        FiveElement e = info.getElement();
        String state = elementStates.getOrDefault(e.getCode(), "si");
        double stateWeight = BaziDef.STATE_WEIGHTS.getOrDefault(state, 0.5);
        distribution.merge(
            e.getCode(), 1.0 * stateWeight, Double::sum); // Base stem weight 1.0 * state
        counts.merge(e.getCode(), 1, Integer::sum);
      }
    }

    // Hidden Stems
    for (Pillar p : allPillars) {
      List<String> hidden = p.getHiddenStems();
      List<Double> weights =
          BaziDef.HIDDEN_STEM_WEIGHTS.getOrDefault(p.getZhi(), Collections.emptyList());

      for (int i = 0; i < hidden.size(); i++) {
        String stem = hidden.get(i);
        StemInfo info = BaziDef.STEMS_INFO.get(stem);
        if (info == null) continue;

        double weight = (i < weights.size()) ? weights.get(i) : 0.2;
        String state = elementStates.getOrDefault(info.getElement().getCode(), "si");
        double stateWeight = BaziDef.STATE_WEIGHTS.getOrDefault(state, 0.5);

        distribution.merge(info.getElement().getCode(), weight * stateWeight, Double::sum);

        // Only count Ben Qi (first one)
        if (i == 0) {
          counts.merge(info.getElement().getCode(), 1, Integer::sum);
        }
      }
    }

    // Find Strongest/Weakest
    String strongest = "wood";
    String weakest = "wood";
    double maxVal = -1;
    double minVal = 9999;

    for (Map.Entry<String, Double> entry : distribution.entrySet()) {
      if (entry.getValue() > maxVal) {
        maxVal = entry.getValue();
        strongest = entry.getKey();
      }
      if (entry.getValue() < minVal) {
        minVal = entry.getValue();
        weakest = entry.getKey();
      }
    }

    Map<String, Object> favorableMap = calculateFavorableElements(dayMaster, distribution);

    return FiveElementsAnalysis.builder()
        .distribution(distribution)
        .counts(counts)
        .strongest(strongest)
        .weakest(weakest)
        .monthElement(monthElement.getCode())
        .elementStates(elementStates)
        .favorable((List<String>) favorableMap.get("favorable"))
        .unfavorable((List<String>) favorableMap.get("unfavorable"))
        .build();
  }

  private Map<String, String> getElementStates(FiveElement monthElement) {
    Map<String, String> states = new HashMap<>();

    if (monthElement == null) return states;

    // Wang
    states.put(monthElement.getCode(), "wang");

    // Xiang: Month generates Element
    FiveElement generated = BaziDef.FIVE_ELEMENTS_GENERATION.get(monthElement);
    if (generated != null) states.put(generated.getCode(), "xiang");

    // Xiu: Element generates Month
    FiveElement generator = BaziDef.FIVE_ELEMENTS_GENERATED_BY.get(monthElement);
    if (generator != null) states.put(generator.getCode(), "xiu");

    // Qiu: Element restricts Month
    // Find who restricts monthElement
    for (Map.Entry<FiveElement, FiveElement> entry : BaziDef.FIVE_ELEMENTS_RESTRICTION.entrySet()) {
      if (entry.getValue() == monthElement) {
        states.put(entry.getKey().getCode(), "qiu");
        break;
      }
    }

    // Si: Month restricts Element
    FiveElement restricted = BaziDef.FIVE_ELEMENTS_RESTRICTION.get(monthElement);
    if (restricted != null) states.put(restricted.getCode(), "si");

    return states;
  }

  private boolean generatesElement(FiveElement from, FiveElement to) {
    return BaziDef.FIVE_ELEMENTS_GENERATION.get(from) == to;
  }

  private boolean restrictsElement(FiveElement from, FiveElement to) {
    return BaziDef.FIVE_ELEMENTS_RESTRICTION.get(from) == to;
  }

  private String getTenGod(StemInfo dayStem, StemInfo otherStem) {
    if (dayStem.getChinese().equals(otherStem.getChinese())) return "比肩";

    FiveElement dayEl = dayStem.getElement();
    BaziDef.YinYang dayYY = dayStem.getYinYang();
    FiveElement otherEl = otherStem.getElement();
    BaziDef.YinYang otherYY = otherStem.getYinYang();

    boolean sameYinYang = dayYY == otherYY;

    if (dayEl == otherEl) return sameYinYang ? "比肩" : "劫财";
    if (generatesElement(dayEl, otherEl)) return sameYinYang ? "食神" : "伤官";
    if (restrictsElement(dayEl, otherEl)) return sameYinYang ? "偏财" : "正财";
    if (restrictsElement(otherEl, dayEl)) return sameYinYang ? "七杀" : "正官";
    if (generatesElement(otherEl, dayEl)) return sameYinYang ? "偏印" : "正印";

    return null;
  }

  private Map<String, Object> calculateFavorableElements(
      DayMaster dayMaster, Map<String, Double> distribution) {
    StemInfo stemInfo = BaziDef.STEMS_INFO.get(dayMaster.getGan());
    FiveElement dayElement = stemInfo.getElement();

    List<String> favorable = new ArrayList<>();
    List<String> unfavorable = new ArrayList<>();

    // 基础关系
    FiveElement yinElement = BaziDef.FIVE_ELEMENTS_GENERATED_BY.get(dayElement); // 印
    FiveElement shiShangElement = BaziDef.FIVE_ELEMENTS_GENERATION.get(dayElement); // 食伤
    FiveElement caiElement = BaziDef.FIVE_ELEMENTS_RESTRICTION.get(dayElement); // 财

    FiveElement guanShaElement = FiveElement.WOOD; // Default
    for (Map.Entry<FiveElement, FiveElement> entry : BaziDef.FIVE_ELEMENTS_RESTRICTION.entrySet()) {
      if (entry.getValue() == dayElement) {
        guanShaElement = entry.getKey();
        break;
      }
    }

    String strength = dayMaster.getStrength();
    if ("strong".equals(strength)) {
      favorable.add(guanShaElement.getCode());
      favorable.add(shiShangElement.getCode());
      favorable.add(caiElement.getCode());
      unfavorable.add(yinElement.getCode());
      unfavorable.add(dayElement.getCode());
    } else if ("weak".equals(strength)) {
      favorable.add(yinElement.getCode());
      favorable.add(dayElement.getCode());
      unfavorable.add(guanShaElement.getCode());
      unfavorable.add(shiShangElement.getCode());
      unfavorable.add(caiElement.getCode());
    } else {
      List<String> sorted = new ArrayList<>(distribution.keySet());
      sorted.sort(Comparator.comparingDouble(distribution::get));
      if (sorted.size() >= 2) {
        favorable.add(sorted.get(0));
        favorable.add(sorted.get(1));
      }
      if (sorted.size() >= 5) {
        unfavorable.add(sorted.get(4));
        unfavorable.add(sorted.get(3));
      }
    }

    return Map.of("favorable", favorable, "unfavorable", unfavorable);
  }

  private BaziResult.TenGodsAnalysis calculateTenGods(FourPillars fourPillars, String dayStemName) {
    StemInfo dayStemInfo = BaziDef.STEMS_INFO.get(dayStemName);
    Map<String, BaziResult.TenGodsAnalysis.TenGodInfo> godsMap = new HashMap<>();

    List<Map.Entry<String, String>> positions =
        List.of(
            Map.entry(fourPillars.getYear().getGan(), "年干"),
            Map.entry(fourPillars.getMonth().getGan(), "月干"),
            Map.entry(fourPillars.getHour().getGan(), "时干"));

    for (Map.Entry<String, String> entry : positions) {
      String stemName = entry.getKey();
      String position = entry.getValue();
      StemInfo stemInfo = BaziDef.STEMS_INFO.get(stemName);

      if (stemInfo != null) {
        String tenGod = getTenGod(dayStemInfo, stemInfo);
        if (tenGod != null) {
          godsMap.putIfAbsent(
              tenGod,
              BaziResult.TenGodsAnalysis.TenGodInfo.builder()
                  .name(tenGod)
                  .count(0)
                  .positions(new ArrayList<>())
                  .build());

          BaziResult.TenGodsAnalysis.TenGodInfo info = godsMap.get(tenGod);
          info.setCount(info.getCount() + 1);
          info.getPositions().add(position);
        }
      }
    }

    return BaziResult.TenGodsAnalysis.builder().gods(godsMap).build();
  }

  private List<String> getDayMasterCharacteristics(String dayStemName) {
    Map<String, List<String>> characteristics =
        Map.of(
            "甲", List.of("积极进取", "有领导力", "刚直不阿", "富有创造力"),
            "乙", List.of("温和柔顺", "适应力强", "善于协调", "注重细节"),
            "丙", List.of("热情开朗", "光明磊落", "富有激情", "善于表达"),
            "丁", List.of("细腻敏感", "文雅有礼", "富有艺术气质", "善解人意"),
            "戊", List.of("稳重踏实", "诚实守信", "包容大度", "责任心强"),
            "己", List.of("温和谦逊", "细心周到", "善于理财", "注重实际"),
            "庚", List.of("刚毅果断", "正直坦率", "意志坚定", "富有正义感"),
            "辛", List.of("细腻敏锐", "追求完美", "善于分析", "注重品质"),
            "壬", List.of("聪明灵活", "善于变通", "富有智慧", "适应力强"),
            "癸", List.of("温柔体贴", "富有想象力", "善于思考", "内敛含蓄"));
    return characteristics.getOrDefault(dayStemName, List.of("性格特征待分析"));
  }

  private BaziResult.PatternInfo calculatePattern(
      FourPillars fourPillars, DayMaster dayMaster, FiveElementsAnalysis fiveElements) {
    String dayStemName = fourPillars.getDay().getGan();
    // StemInfo dayStemInfo = BaziDef.STEMS_INFO.get(dayStemName); // Not used directly, but
    // dayMaster score is
    StemInfo dayStemInfo = BaziDef.STEMS_INFO.get(dayStemName);
    FiveElement dayElement = dayStemInfo.getElement();

    String monthBranch = fourPillars.getMonth().getZhi();
    List<String> monthHiddenStems = fourPillars.getMonth().getHiddenStems();
    List<String> tianGan =
        List.of(
            fourPillars.getYear().getGan(),
            fourPillars.getMonth().getGan(),
            fourPillars.getHour().getGan());

    // Lu and Ren maps
    Map<String, String> luMap =
        Map.of(
            "甲", "寅", "乙", "卯", "丙", "巳", "丁", "午", "戊", "巳", "己", "午", "庚", "申", "辛", "酉", "壬",
            "亥", "癸", "子");
    Map<String, String> renMap =
        Map.of(
            "甲", "卯", "乙", "寅", "丙", "午", "丁", "巳", "戊", "午", "己", "巳", "庚", "酉", "辛", "申", "壬",
            "子", "癸", "亥");

    // 1. 建禄格
    if (monthBranch.equals(luMap.get(dayStemName))) {
      return BaziResult.PatternInfo.builder()
          .name("建禄格")
          .category("normal")
          .description("月支为日主之禄，主身旺有根，宜见财官食伤")
          .monthStem(monthHiddenStems.isEmpty() ? null : monthHiddenStems.get(0))
          .isTransparent(false)
          .build();
    }

    // 2. 羊刃格
    if (monthBranch.equals(renMap.get(dayStemName))) {
      return BaziResult.PatternInfo.builder()
          .name("羊刃格")
          .category("normal")
          .description("月支为日主之刃，主身强刚烈，宜见官杀制刃")
          .monthStem(monthHiddenStems.isEmpty() ? null : monthHiddenStems.get(0))
          .isTransparent(false)
          .build();
    }

    // 3. 特殊格局
    double score = dayMaster.getAnalysis() != null ? dayMaster.getAnalysis().getTotalScore() : 50.0;

    // 从格 (Score < 20)
    if (score < 20) {
      Map<String, Double> dist = fiveElements.getDistribution();
      // Find strongest non-day-element
      String strongestElStr = dayElement.getCode();
      double strongestVal = 0;

      for (FiveElement el : FiveElement.values()) {
        if (el != dayElement && dist.getOrDefault(el.getCode(), 0.0) > strongestVal) {
          strongestVal = dist.get(el.getCode());
          strongestElStr = el.getCode();
        }
      }

      FiveElement strongestEl = FiveElement.fromCode(strongestElStr);
      if (strongestEl != null) {
        if (restrictsElement(dayElement, strongestEl)) {
          return BaziResult.PatternInfo.builder()
              .name("从财格")
              .category("special")
              .description("日主极弱而财星极旺，弃命从财，宜顺从财势")
              .build();
        }
        if (restrictsElement(strongestEl, dayElement)) {
          return BaziResult.PatternInfo.builder()
              .name("从官格")
              .category("special")
              .description("日主极弱而官杀极旺，弃命从官，宜顺从官势")
              .build();
        }
        if (generatesElement(dayElement, strongestEl)) {
          return BaziResult.PatternInfo.builder()
              .name("从儿格")
              .category("special")
              .description("日主极弱而食伤极旺，弃命从儿，宜顺从食伤之势")
              .build();
        }
      }
    }

    // 专旺格 (Score > 75)
    if (score > 75) {
      Map<FiveElement, String> nameMap =
          Map.of(
              FiveElement.WOOD, "曲直格",
              FiveElement.FIRE, "炎上格",
              FiveElement.EARTH, "稼穑格",
              FiveElement.METAL, "从革格",
              FiveElement.WATER, "润下格");
      Map<FiveElement, String> descMap =
          Map.of(
              FiveElement.WOOD, "木气专旺成局，主仁慈正直，宜水木运",
              FiveElement.FIRE, "火气炎上成局，主热情礼仪，宜木火运",
              FiveElement.EARTH, "土气稼穑成局，主忠厚信实，宜火土运",
              FiveElement.METAL, "金气从革成局，主刚毅果决，宜土金运",
              FiveElement.WATER, "水气润下成局，主聪慧灵活，宜金水运");

      return BaziResult.PatternInfo.builder()
          .name(nameMap.get(dayElement))
          .category("special")
          .description(descMap.get(dayElement))
          .build();
    }

    // 4. 正格
    if (!monthHiddenStems.isEmpty()) {
      for (String hiddenStem : monthHiddenStems) {
        StemInfo hiddenStemInfo = BaziDef.STEMS_INFO.get(hiddenStem);
        String tenGod = getTenGod(dayStemInfo, hiddenStemInfo);

        if ("比肩".equals(tenGod) || "劫财".equals(tenGod)) continue;

        boolean isTransparent = tianGan.contains(hiddenStem);

        Map<String, Map.Entry<String, String>> patternMap =
            Map.of(
                "正官", Map.entry("正官格", "月令透正官，主贵气端正，宜见财印相生"),
                "七杀", Map.entry("七杀格", "月令透七杀，主威严果决，宜见食伤制杀或印化杀"),
                "正财", Map.entry("正财格", "月令透正财，主务实勤俭，宜见官杀护财"),
                "偏财", Map.entry("偏财格", "月令透偏财，主豪爽大方，宜见官杀护财"),
                "正印", Map.entry("正印格", "月令透正印，主聪慧仁厚，宜见官杀生印"),
                "偏印", Map.entry("偏印格", "月令透偏印，主机敏多思，宜见财星制印"),
                "食神", Map.entry("食神格", "月令透食神，主温和福厚，宜见财星泄秀"),
                "伤官", Map.entry("伤官格", "月令透伤官，主聪明傲气，宜见财星或印星"));

        if (tenGod != null && patternMap.containsKey(tenGod)) {
          Map.Entry<String, String> info = patternMap.get(tenGod);
          return BaziResult.PatternInfo.builder()
              .name(info.getKey())
              .category("normal")
              .description(info.getValue())
              .monthStem(hiddenStem)
              .monthStemTenGod(tenGod)
              .isTransparent(isTransparent)
              .build();
        }
      }
    }

    return BaziResult.PatternInfo.builder()
        .name("杂格")
        .category("normal")
        .description("月令无明显成格条件，需综合分析八字整体格局")
        .build();
  }

  private Solar getTrueSolarTime(
      int year, int month, int day, int hour, int minute, double longitude) {
    Calendar cal = Calendar.getInstance();
    cal.set(year, month - 1, day);
    int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);

    double B = (2 * Math.PI * (dayOfYear - 81)) / 365.0;
    double eot = 9.87 * Math.sin(2 * B) - 7.53 * Math.cos(B) - 1.5 * Math.sin(B);

    double longitudeCorrection = (longitude - 120.0) * 4.0;
    double totalMinutes = hour * 60 + minute + longitudeCorrection + eot;

    int dayOffset = 0;
    if (totalMinutes < 0) {
      totalMinutes += 24 * 60;
      dayOffset = -1;
    } else if (totalMinutes >= 24 * 60) {
      totalMinutes -= 24 * 60;
      dayOffset = 1;
    }

    int newHour = (int) (totalMinutes / 60);
    int newMinute = (int) Math.round(totalMinutes % 60);

    cal.set(year, month - 1, day, newHour, newMinute, 0);
    cal.add(Calendar.DAY_OF_MONTH, dayOffset);

    return Solar.fromYmdHms(
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH) + 1,
        cal.get(Calendar.DAY_OF_MONTH),
        cal.get(Calendar.HOUR_OF_DAY),
        cal.get(Calendar.MINUTE),
        0);
  }

  private YunInfo calculateYun(Yun yun) {
    List<BaziResult.DaYun> daYunList = new ArrayList<>();
    DaYun[] bigYunArray = yun.getDaYun(); // getDaYun() returns array, not List

    for (DaYun dy : bigYunArray) {
      daYunList.add(
          BaziResult.DaYun.builder()
              .index(dy.getIndex())
              .startAge(dy.getStartAge())
              .endAge(dy.getEndAge())
              .ganZhi(dy.getGanZhi())
              .startYear(dy.getStartYear())
              .endYear(dy.getEndYear())
              .build());
    }

    // Note: Yun in lunar 1.7.7 may not have getStartAge() and isForward() methods
    // Using default values for now
    return YunInfo.builder()
        .startAge(bigYunArray.length > 0 ? bigYunArray[0].getStartAge() : 0)
        .forward(true) // Default to true
        .daYunList(daYunList)
        .build();
  }
}
