-- =====================================================
-- 仪表盘测试虚拟数据
-- 数据库: wic_edu_kg
-- 生成日期: 2025-12-31
-- =====================================================

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
INSERT INTO sys_user (username, student_id, password, name, email, department, status, role, deleted, created_at, updated_at)
VALUES
-- 今天新增用户
('user_today_1', '2024001001', '$2a$10$N.zmdrINx4cYPvnGqe0d3.hfL0gR3ks7L2sQX5pHXJKjzZ9xNpIrO', '张三', 'user1@wic.edu.cn', '信息工程学部', 1, 3, 0, NOW(), NOW()),
('user_today_2', '2024001002', '$2a$10$N.zmdrINx4cYPvnGqe0d3.hfL0gR3ks7L2sQX5pHXJKjzZ9xNpIrO', '李四', 'user2@wic.edu.cn', '信息工程学部', 1, 3, 0, NOW(), NOW()),
('user_today_3', '2024001003', '$2a$10$N.zmdrINx4cYPvnGqe0d3.hfL0gR3ks7L2sQX5pHXJKjzZ9xNpIrO', '王五', 'user3@wic.edu.cn', '经济与管理学部', 1, 3, 0, NOW(), NOW()),
-- 昨天新增用户
('user_d1_1', '2024002001', '$2a$10$N.zmdrINx4cYPvnGqe0d3.hfL0gR3ks7L2sQX5pHXJKjzZ9xNpIrO', '赵六', 'user4@wic.edu.cn', '城市建设学部', 1, 3, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
('user_d1_2', '2024002002', '$2a$10$N.zmdrINx4cYPvnGqe0d3.hfL0gR3ks7L2sQX5pHXJKjzZ9xNpIrO', '钱七', 'user5@wic.edu.cn', '医学部', 1, 3, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
-- 2天前新增用户
('user_d2_1', '2024003001', '$2a$10$N.zmdrINx4cYPvnGqe0d3.hfL0gR3ks7L2sQX5pHXJKjzZ9xNpIrO', '孙八', 'user6@wic.edu.cn', '艺术学部', 1, 3, 0, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
-- 3天前新增用户
('user_d3_1', '2024004001', '$2a$10$N.zmdrINx4cYPvnGqe0d3.hfL0gR3ks7L2sQX5pHXJKjzZ9xNpIrO', '周九', 'user7@wic.edu.cn', '外国语学部', 1, 3, 0, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
('user_d3_2', '2024004002', '$2a$10$N.zmdrINx4cYPvnGqe0d3.hfL0gR3ks7L2sQX5pHXJKjzZ9xNpIrO', '吴十', 'user8@wic.edu.cn', '信息工程学部', 1, 3, 0, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
('user_d3_3', '2024004003', '$2a$10$N.zmdrINx4cYPvnGqe0d3.hfL0gR3ks7L2sQX5pHXJKjzZ9xNpIrO', '郑十一', 'user9@wic.edu.cn', '经济与管理学部', 1, 3, 0, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
-- 5天前新增用户
('user_d5_1', '2024005001', '$2a$10$N.zmdrINx4cYPvnGqe0d3.hfL0gR3ks7L2sQX5pHXJKjzZ9xNpIrO', '王十二', 'user10@wic.edu.cn', '城市建设学部', 1, 3, 0, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
-- 禁用用户
('user_disabled', '2024006001', '$2a$10$N.zmdrINx4cYPvnGqe0d3.hfL0gR3ks7L2sQX5pHXJKjzZ9xNpIrO', '禁用用户', 'disabled@wic.edu.cn', '信息工程学部', 2, 3, 0, DATE_SUB(NOW(), INTERVAL 10 DAY), NOW());

-- 插入测试文章
INSERT INTO article (title, subtitle, author, publish_date, read_time, cover_image, tags, content, view_count, status, created_at, updated_at)
VALUES
-- 已发布文章
('校园秋景摄影大赛圆满落幕', '百余幅作品展现校园之美', '校园新闻中心', CURDATE(), '5分钟', 'https://r2.wic.edu.kg/covers/autumn.jpg', '活动,摄影,校园', '<p>校园秋景摄影大赛...</p>', 256, 1, NOW(), NOW()),
('2025年新年迎新晚会即将开启', '精彩节目静待同学们到来', '学生会', CURDATE(), '3分钟', 'https://r2.wic.edu.kg/covers/newyear.jpg', '活动,晚会', '<p>新年晚会介绍...</p>', 512, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
('图书馆延长开放时间通知', '考试周期间延长至晚11点', '图书馆', DATE_SUB(CURDATE(), INTERVAL 2 DAY), '2分钟', 'https://r2.wic.edu.kg/covers/library.jpg', '通知,图书馆', '<p>考试周图书馆...</p>', 1024, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
('计算机学院学术讲座预告', '人工智能前沿技术探讨', '信息工程学部', DATE_SUB(CURDATE(), INTERVAL 3 DAY), '4分钟', 'https://r2.wic.edu.kg/covers/lecture.jpg', '讲座,AI,学术', '<p>学术讲座内容...</p>', 320, 1, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
('校运动会报名开始', '各项目报名截止本月底', '体育部', DATE_SUB(CURDATE(), INTERVAL 5 DAY), '3分钟', 'https://r2.wic.edu.kg/covers/sports.jpg', '活动,运动会', '<p>运动会报名...</p>', 480, 1, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
-- 草稿文章
('元旦假期安排(草稿)', '待完善', '校办公室', NULL, '2分钟', NULL, '通知', '<p>草稿内容...</p>', 0, 0, NOW(), NOW()),
('寒假实习招聘汇总(草稿)', '待发布', '就业指导中心', NULL, '10分钟', NULL, '招聘,实习', '<p>草稿内容...</p>', 0, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 插入测试图片
INSERT INTO gallery_image (user_id, title, description, location, image_url, thumbnail_url, width, height, file_size, file_type, category, tags, view_count, like_count, status, is_featured, sort_order, created_at, updated_at)
VALUES
-- 已审核通过的图片
(1, '校园秋景', '图书馆前的银杏大道', '图书馆', 'https://r2.wic.edu.kg/gallery/autumn1.jpg', 'https://r2.wic.edu.kg/gallery/thumb/autumn1.jpg', 1920, 1080, 524288, 'image/jpeg', 'campus', '秋天,银杏', 150, 25, 1, 1, 1, NOW(), NOW()),
(1, '教学楼夜景', '信息楼灯火通明', '信息楼', 'https://r2.wic.edu.kg/gallery/night1.jpg', 'https://r2.wic.edu.kg/gallery/thumb/night1.jpg', 1920, 1080, 486000, 'image/jpeg', 'campus', '夜景,教学楼', 88, 12, 1, 0, 2, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, '运动会精彩瞬间', '百米冲刺决赛', '体育场', 'https://r2.wic.edu.kg/gallery/sports1.jpg', 'https://r2.wic.edu.kg/gallery/thumb/sports1.jpg', 1920, 1280, 620000, 'image/jpeg', 'activity', '运动会,跑步', 220, 45, 1, 1, 3, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
-- 待审核图片
(3, '食堂新菜品', '红烧肉很不错', '一食堂', 'https://r2.wic.edu.kg/gallery/food1.jpg', 'https://r2.wic.edu.kg/gallery/thumb/food1.jpg', 1080, 1080, 380000, 'image/jpeg', 'life', '美食,食堂', 0, 0, 0, 0, 0, NOW(), NOW()),
(4, '宿舍楼下风景', '春天的花开了', '学生宿舍', 'https://r2.wic.edu.kg/gallery/dorm1.jpg', 'https://r2.wic.edu.kg/gallery/thumb/dorm1.jpg', 1200, 900, 290000, 'image/jpeg', 'campus', '春天,宿舍', 0, 0, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, '晚霞', '校园的日落很美', '操场', 'https://r2.wic.edu.kg/gallery/sunset1.jpg', 'https://r2.wic.edu.kg/gallery/thumb/sunset1.jpg', 1600, 1000, 410000, 'image/jpeg', 'landscape', '晚霞,日落', 0, 0, 0, 0, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 插入测试留言
INSERT INTO senior_message (user_id, content, signature, card_color, ink_color, font_id, status, like_count, created_at, updated_at)
VALUES
-- 已发布留言
(1, '欢迎来到武汉城市学院！这里将是你人生中最难忘的四年，好好珍惜每一天。', '2020级学长', '#FFF9C4', '#5D4037', 1, 1, 42, NOW(), NOW()),
(2, '图书馆三楼靠窗的位置最适合自习，记得早点去占位哦！', '学姐寄语', '#E3F2FD', '#1565C0', 1, 1, 28, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, '食堂二楼的麻辣香锅绝对不要错过，经济实惠又好吃！', '吃货学长', '#FFECB3', '#E65100', 1, 1, 56, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(4, '多参加社团活动，你会认识很多志同道合的朋友。', '社团负责人', '#E8F5E9', '#2E7D32', 1, 1, 33, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
-- 待审核留言
(5, '这是一条待审核的留言内容。', '新生', '#FCE4EC', '#C2185B', 1, 0, 0, NOW(), NOW()),
(6, '又一条待审核的测试留言。', '测试用户', '#F3E5F5', '#7B1FA2', 1, 0, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(7, '第三条待审核留言。', '匿名', '#E0F7FA', '#00838F', 1, 0, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 完成
SELECT '虚拟数据插入完成！' AS Message;
