-- PostgreSQL - 清除并重置积分套餐表
-- 用途：删除旧的积分套餐数据，让 DataSeeder 重新初始化新数据

-- 1. 清空积分套餐表
TRUNCATE TABLE points_packages CASCADE;

-- 2. 重置自增序列（如果使用了序列）
ALTER SEQUENCE IF EXISTS points_packages_id_seq RESTART WITH 1;

-- 3. 验证清空结果
SELECT COUNT(*) as remaining_packages FROM points_packages;

-- 执行完成后，重启 Spring Boot 应用，DataSeeder 会自动插入新的套餐数据
