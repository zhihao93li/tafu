package com.tafu.bazi;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Bazi Application
 *
 * <p>描述: Spring Boot 应用程序主入口类。
 *
 * <p>包含内容: 1. main 方法启动 Spring Application
 *
 * <p>维护说明: 当这个文件/文件夹发生改动时，同步改动说明文件以及上一层文件夹对本文件/文件夹的描述。
 *
 * @author Zhihao Li
 * @since 2026-01-22
 */
@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class BaziApplication {

  public static void main(String[] args) {
    // Load .env file and set as system properties
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

    SpringApplication.run(BaziApplication.class, args);
  }
}
