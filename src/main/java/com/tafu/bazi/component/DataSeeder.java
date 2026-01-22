package com.tafu.bazi.component;

import com.tafu.bazi.entity.PointsPackage;
import com.tafu.bazi.entity.ThemePricing;
import com.tafu.bazi.repository.PointsPackageRepository;
import com.tafu.bazi.repository.ThemePricingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataSeeder
 *
 * <p>描述: 数据初始化组件 (Command Line Runner)。 负责应用启动时初始化基础数据 (如积分套餐、主题定价)。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("null")
public class DataSeeder implements CommandLineRunner {

  private final PointsPackageRepository pointsPackageRepository;
  private final ThemePricingRepository themePricingRepository;

  @Override
  public void run(String... args) throws Exception {
    seedPointsPackages();
    seedThemePricing();
  }

  private void seedPointsPackages() {
    if (pointsPackageRepository.count() > 0) return;

    log.info("Seeding Points Packages...");
    List<PointsPackage> packages =
        List.of(
            createPackage("基础套餐", 100, 990, 1),
            createPackage("超值套餐", 300, 2490, 2),
            createPackage("尊享套餐", 1000, 6490, 3));
    pointsPackageRepository.saveAll(packages);
  }

  private void seedThemePricing() {
    if (themePricingRepository.count() > 0) return;

    log.info("Seeding Theme Pricing...");
    List<ThemePricing> themes =
        List.of(
            createTheme("life_color", "生命色彩", "解读你的生命底色与核心特质", 20, 1),
            createTheme("relationship", "情感关系", "洞察你的亲密关系模式与正缘", 30, 2),
            createTheme("career_wealth", "事业财富", "分析事业发展方向与财富运势", 30, 3),
            createTheme("health", "健康运势", "关注身心健康与潜在风险", 20, 4),
            createTheme("life_lesson", "贵人小人", "识别命中的贵人与潜在阻碍", 20, 5),
            createTheme("yearly_fortune", "流年运势", "通过流年运势把握当下的机遇", 40, 6));
    themePricingRepository.saveAll(themes);
  }

  private PointsPackage createPackage(String name, int points, int price, int sort) {
    PointsPackage pkg = new PointsPackage();
    pkg.setName(name);
    pkg.setPoints(points);
    pkg.setPrice(price);
    pkg.setIsActive(true);
    pkg.setSortOrder(sort);
    return pkg;
  }

  private ThemePricing createTheme(String id, String name, String desc, int price, int sort) {
    ThemePricing t = new ThemePricing();
    t.setTheme(id);
    t.setName(name);
    t.setDescription(desc);
    t.setPrice(price);
    t.setIsActive(true);
    t.setSortOrder(sort);
    return t;
  }
}
