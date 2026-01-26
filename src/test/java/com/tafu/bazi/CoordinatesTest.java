package com.tafu.bazi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tafu.bazi.utils.LunarUtils;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

/** 测试经纬度数据加载和查询 */
public class CoordinatesTest {

  @Test
  public void testLoadCityGeoData() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    ClassPathResource resource = new ClassPathResource("city-geo-data.json");

    System.out.println("文件是否存在: " + resource.exists());

    if (resource.exists()) {
      List<LunarUtils.CityGeoItem> data =
          mapper.readValue(
              resource.getInputStream(), new TypeReference<List<LunarUtils.CityGeoItem>>() {});

      System.out.println("总记录数: " + data.size());

      // 查找台北市中山区
      String targetProvince = "台湾省";
      String targetCity = "台北市";
      String targetArea = "中山区";

      List<LunarUtils.CityGeoItem> result =
          data.stream()
              .filter(
                  item ->
                      targetProvince.equals(item.getProvince())
                          && targetCity.equals(item.getCity())
                          && targetArea.equals(item.getArea()))
              .toList();

      System.out.println("\n查找 " + targetProvince + "/" + targetCity + "/" + targetArea);
      System.out.println("匹配结果数: " + result.size());

      if (!result.isEmpty()) {
        LunarUtils.CityGeoItem item = result.get(0);
        System.out.println("找到:");
        System.out.println("  省份: " + item.getProvince());
        System.out.println("  城市: " + item.getCity());
        System.out.println("  区县: " + item.getArea());
        System.out.println("  经度: " + item.getLng());
        System.out.println("  纬度: " + item.getLat());
      } else {
        System.out.println("❌ 未找到匹配记录");

        // 尝试查找台湾省的所有记录
        System.out.println("\n台湾省的所有记录:");
        data.stream()
            .filter(item -> "台湾省".equals(item.getProvince()))
            .limit(5)
            .forEach(
                item -> {
                  System.out.println(
                      "  " + item.getProvince() + "/" + item.getCity() + "/" + item.getArea());
                });
      }
    }
  }
}
