package com.tafu.bazi;

import com.nlf.calendar.Lunar;
import com.nlf.calendar.Solar;
import org.junit.jupiter.api.Test;

public class GanZhiTest {

  @Test
  public void testYearGanZhi() {
    System.out.println("=== 测试流年干支 (使用7月1日) ===");
    for (int year = 2023; year <= 2027; year++) {
      Solar solar = Solar.fromYmd(year, 7, 1);
      Lunar lunar = solar.getLunar();
      String ganZhi = lunar.getYearInGanZhiExact();
      System.out.println(year + "年: " + ganZhi);
    }

    System.out.println("\n=== 测试流年干支 (使用1月1日) ===");
    for (int year = 2023; year <= 2027; year++) {
      Solar solar = Solar.fromYmd(year, 1, 1);
      Lunar lunar = solar.getLunar();
      String ganZhi = lunar.getYearInGanZhiExact();
      System.out.println(year + "年: " + ganZhi);
    }

    System.out.println("\n=== 专业网站标准答案 ===");
    System.out.println("2023年: 癸卯");
    System.out.println("2024年: 甲辰");
    System.out.println("2025年: 乙巳");
    System.out.println("2026年: 丙午");
    System.out.println("2027年: 丁未");
  }
}
