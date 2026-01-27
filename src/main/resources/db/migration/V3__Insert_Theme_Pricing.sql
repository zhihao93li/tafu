-- Insert Theme Pricing Configuration
-- 插入主题价格配置
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO theme_pricing (id, theme, name, description, price, original_price, is_active, sort_order)
VALUES 
    (uuid_generate_v4(), 'life_color', '生命色彩', '发现你的生命色彩与能量特质', 20, 30, true, 1),
    (uuid_generate_v4(), 'relationship', '情感婚恋', '探索你的情感模式与关系发展', 20, 30, true, 2),
    (uuid_generate_v4(), 'career_wealth', '事业财富', '解析你的事业方向与财富机遇', 20, 30, true, 3),
    (uuid_generate_v4(), 'health', '健康养生', '了解你的健康状况与养生建议', 20, 30, true, 4),
    (uuid_generate_v4(), 'life_lesson', '人生课题', '认识你的人生使命与成长方向', 20, 30, true, 5),
    (uuid_generate_v4(), 'yearly_fortune', '流年运势', '查看你的年度运势与重要时机', 30, 50, true, 6),
    (uuid_generate_v4(), 'synastry', '合盘分析', '分析两人关系的相合度与互动模式', 50, 80, true, 7),
    (uuid_generate_v4(), 'soul_song', '灵魂歌曲', '发现与你灵魂共振的音乐', 100, 150, true, 8)
ON CONFLICT (theme) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    price = EXCLUDED.price,
    original_price = EXCLUDED.original_price,
    is_active = EXCLUDED.is_active,
    sort_order = EXCLUDED.sort_order;
