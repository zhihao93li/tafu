-- PostgreSQL - 完全重建数据库
-- ⚠️ 警告：这会删除所有数据！仅在开发环境使用！

-- 1. 断开所有现有连接（需要超级用户权限）
SELECT pg_terminate_backend(pg_stat_activity.pid)
FROM pg_stat_activity
WHERE pg_stat_activity.datname = 'tafu_dev'
  AND pid <> pg_backend_pid();

-- 2. 删除数据库
DROP DATABASE IF EXISTS tafu_dev;

-- 3. 重新创建数据库
CREATE DATABASE tafu_dev
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- 执行完成后，重启 Spring Boot 应用
-- Flyway 会自动创建表结构，DataSeeder 会初始化所有数据
