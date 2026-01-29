package com.tafu.bazi.config;

import java.io.IOException;
import java.util.Properties;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

/**
 * YamlPropertySourceFactory
 *
 * <p>用于加载 YAML 配置文件的工厂类
 *
 * @author Zhihao Li
 * @since 2026-01-29
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

  @Override
  public PropertySource<?> createPropertySource(String name, EncodedResource resource)
      throws IOException {
    YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
    factory.setResources(resource.getResource());

    Properties properties = factory.getObject();

    return new PropertiesPropertySource(
        resource.getResource().getFilename() != null
            ? resource.getResource().getFilename()
            : "yaml-properties",
        properties);
  }
}
