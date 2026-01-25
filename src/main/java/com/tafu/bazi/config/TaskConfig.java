package com.tafu.bazi.config;

import javax.sql.DataSource;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * TaskConfig
 *
 * <p>描述: 异步任务和分布式锁配置。
 *
 * <p>包含内容: 1. 线程池配置 (ThreadPoolTaskExecutor) 2. 分布式锁配置 (ShedLock)
 *
 * @author Zhihao Li
 * @since 2026-01-25
 */
@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
public class TaskConfig {

  @Value("${app.task.pool-size:5}")
  private int poolSize;

  @Bean
  public ThreadPoolTaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(poolSize);
    executor.setMaxPoolSize(poolSize * 2);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("task-");
    executor.initialize();
    return executor;
  }

  @Bean
  public LockProvider lockProvider(DataSource dataSource) {
    return new JdbcTemplateLockProvider(
        JdbcTemplateLockProvider.Configuration.builder()
            .withJdbcTemplate(new JdbcTemplate(dataSource))
            .usingDbTime() // Works on PostgreSQL, MySQL, MariaDB, MS SQL, Oracle, DB2, HSQL and H2
            .build());
  }
}
