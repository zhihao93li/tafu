-- 更新积分套餐数据以匹配前端期望
-- 执行此脚本之前请备份数据库！

-- 删除旧套餐（如果存在）
DELETE FROM points_packages WHERE name IN ('基础套餐', '超值套餐', '尊享套餐');

-- 插入新套餐
-- 注意：价格单位是分（cents），所以 ¥19.90 = 1990 分
INSERT INTO points_packages (id, name, points, price, is_active, sort_order) VALUES
  (UUID(), '基础套餐', 200, 1990, true, 1),
  (UUID(), '超值套餐', 500, 4490, true, 2),
  (UUID(), '尊享套餐', 1000, 7990, true, 3);

-- 验证结果
SELECT * FROM points_packages ORDER BY sort_order;
