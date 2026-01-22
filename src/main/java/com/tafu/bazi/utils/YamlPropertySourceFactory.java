package com.tafu.bazi.utils;

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
 * <p>描述: 用于加载 YAML配置文件的工厂类。
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@SuppressWarnings("null")
public class YamlPropertySourceFactory implements PropertySourceFactory {

  @Override
  public PropertySource<?> createPropertySource(String name, EncodedResource resource)
      throws IOException {
    YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
    factory.setResources(resource.getResource());
    Properties properties = factory.getObject();
    String sourceName = name != null ? name : resource.getResource().getFilename();
    if (sourceName == null) {
      sourceName = "yamlPropertySource";
    }
    return new PropertiesPropertySource(
        sourceName, properties != null ? properties : new Properties());
  }
}
