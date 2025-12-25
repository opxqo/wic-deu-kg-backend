/*
 Navicat Premium Data Transfer

 Source Server         : 慈云数据库8.0.43
 Source Server Type    : MySQL
 Source Server Version : 80043
 Source Host           : api.mysql.opxqo.com:3306
 Source Schema         : wic_edu_kg

 Target Server Type    : MySQL
 Target Server Version : 80043
 File Encoding         : 65001

 Date: 25/12/2025 17:04:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_department
-- ----------------------------
DROP TABLE IF EXISTS `chat_department`;
CREATE TABLE `chat_department`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `color` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `sort_order` int NULL DEFAULT 0,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_department
-- ----------------------------
INSERT INTO `chat_department` VALUES (1, 'General', 'General discussion', 'chat', '#3B82F6', 1, '2025-11-30 14:22:38', '2025-11-30 14:22:38');
INSERT INTO `chat_department` VALUES (2, 'Study', 'Study help', 'book', '#10B981', 2, '2025-11-30 14:22:38', '2025-11-30 14:22:38');
INSERT INTO `chat_department` VALUES (3, 'Lost Found', 'Lost and found', 'search', '#F59E0B', 3, '2025-11-30 14:22:38', '2025-11-30 14:22:38');

-- ----------------------------
-- Table structure for chat_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `department_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `message_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'TEXT',
  `attachment_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `reply_to_id` bigint NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_department`(`department_id` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `chat_message_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `chat_department` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `chat_message_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_message
-- ----------------------------

-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学部代码，如 INFO_ENG, MEDICINE',
  `name_zh` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '中文名称',
  `name_en` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '英文名称',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图标名称，对应前端 Lucide 图标',
  `description_zh` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '中文描述',
  `description_en` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '英文描述',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '位置',
  `hot_major_zh` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '热门专业(中文)',
  `hot_major_en` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '热门专业(英文)',
  `online_count` int NULL DEFAULT 0 COMMENT '在线人数',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序顺序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE,
  INDEX `idx_code`(`code` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_sort`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学部表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of department
-- ----------------------------
INSERT INTO `department` VALUES (1, 'INFO_ENG', '信息工程学部', 'Information Engineering', 'Cpu', '天赐哥拥抱数字化未来，培养全栈开发与人工智能领域的顶尖人才。', 'Embrace the digital future, cultivating top talents in full-stack development and AI.', '实验楼 B-402', '计算机科学与技术', 'Software Engineering', 100, 1, 1, '2025-12-01 05:51:14', '2025-12-01 17:09:01');
INSERT INTO `department` VALUES (2, 'MEDICINE', '医学部', 'Medicine', 'Stethoscope', '仁心仁术，专注于现代医疗护理技术与生命科学研究。', 'Benevolence and expertise, focusing on modern healthcare and life sciences.', '医学中心 3F', '护理学', 'Nursing', 356, 2, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');
INSERT INTO `department` VALUES (3, 'ECON_MGMT', '经济与管理学部', 'Economics & Management', 'TrendingUp', '培养具有全球视野的商业领袖与金融精英。', 'Cultivating business leaders and financial elites with global vision.', '文科楼 A-201', '会计学', 'Accounting', 210, 3, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');
INSERT INTO `department` VALUES (4, 'ART_DESIGN', '艺术与设计学部', 'Art & Design', 'Palette', '激发无限创意，用设计美学重塑生活空间。', 'Inspiring unlimited creativity, reshaping living spaces with design aesthetics.', '创意大楼 Studio X', '环境设计', 'Environmental Design', 189, 4, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');
INSERT INTO `department` VALUES (5, 'URBAN_CONST', '城建学部', 'Urban Construction', 'Building2', '建设智慧城市，打造绿色可持续的居住环境。', 'Building smart cities, creating green and sustainable living environments.', '建工楼 505', '土木工程', 'Civil Engineering', 145, 5, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');
INSERT INTO `department` VALUES (6, 'FOREIGN_LANG', '外语学部', 'Foreign Languages', 'Languages', '连接世界的桥梁，培养跨文化交流的高级人才。', 'A bridge connecting the world, cultivating talents for cross-cultural communication.', '国际交流中心', '英语翻译', 'English Translation', 132, 6, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');
INSERT INTO `department` VALUES (7, 'MECH_ELEC', '机电工程学部', 'Mech & Elec Engineering', 'Wrench', '大国工匠的摇篮，专注于智能制造与自动化控制。', 'Cradle of craftsmen, focusing on intelligent manufacturing and automation.', '实训中心 C区', '机械自动化', 'Mechanical Automation', 167, 7, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');
INSERT INTO `department` VALUES (8, 'AUTO_ELEC', '汽车与电子工程', 'Auto & Electronic Engineering', 'Car', '探索新能源与自动驾驶技术的前沿领域。', 'Exploring the frontiers of new energy and autonomous driving technology.', '汽车实验中心', '车辆工程', 'Vehicle Engineering', 98, 8, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');

-- ----------------------------
-- Table structure for department_counselor
-- ----------------------------
DROP TABLE IF EXISTS `department_counselor`;
CREATE TABLE `department_counselor`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `department_id` bigint NOT NULL COMMENT '所属学部ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '辅导员姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '职称',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序顺序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_department`(`department_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `department_counselor_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学部辅导员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of department_counselor
-- ----------------------------
INSERT INTO `department_counselor` VALUES (1, 1, '张伟', NULL, '副教授', NULL, NULL, 1, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');
INSERT INTO `department_counselor` VALUES (2, 1, '李娜', NULL, '讲师', NULL, NULL, 2, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');
INSERT INTO `department_counselor` VALUES (3, 1, '王强', NULL, '辅导员', NULL, NULL, 3, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');
INSERT INTO `department_counselor` VALUES (4, 2, '陈医生', NULL, '主任医师', NULL, NULL, 1, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');
INSERT INTO `department_counselor` VALUES (5, 2, '刘护士长', NULL, '护理部主任', NULL, NULL, 2, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');
INSERT INTO `department_counselor` VALUES (6, 3, '赵敏', NULL, '副教授', NULL, NULL, 1, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');
INSERT INTO `department_counselor` VALUES (7, 3, '周杰', NULL, '讲师', NULL, NULL, 2, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');
INSERT INTO `department_counselor` VALUES (8, 4, '吴艺', NULL, '教授', NULL, NULL, 1, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');
INSERT INTO `department_counselor` VALUES (9, 4, '李设计', NULL, '副教授', NULL, NULL, 2, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');
INSERT INTO `department_counselor` VALUES (10, 5, '孙工', NULL, '高级工程师', NULL, NULL, 1, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');
INSERT INTO `department_counselor` VALUES (11, 5, '钱工', NULL, '工程师', NULL, NULL, 2, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');
INSERT INTO `department_counselor` VALUES (12, 6, 'Ms. Smith', NULL, '外籍教师', NULL, NULL, 1, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');
INSERT INTO `department_counselor` VALUES (13, 6, 'Mr. Brown', NULL, '外籍教师', NULL, NULL, 2, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');
INSERT INTO `department_counselor` VALUES (14, 7, '郑工', NULL, '高级工程师', NULL, NULL, 1, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');
INSERT INTO `department_counselor` VALUES (15, 8, '马斯克(客座)', NULL, '客座教授', NULL, NULL, 1, 1, '2025-12-01 05:51:14', '2025-12-01 05:51:14');

-- ----------------------------
-- Table structure for food_comment
-- ----------------------------
DROP TABLE IF EXISTS `food_comment`;
CREATE TABLE `food_comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '璇勮?ID',
  `product_id` bigint NOT NULL COMMENT '鍟嗗搧ID',
  `user_id` bigint NOT NULL COMMENT '鐢ㄦ埛ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '璇勮?鍐呭?',
  `rating` decimal(2, 1) NOT NULL COMMENT '璇勫垎 1-5',
  `likes` int NULL DEFAULT 0 COMMENT '鐐硅禐鏁',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '鏄?惁鍒犻櫎 0-鍚?1-鏄',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE,
  CONSTRAINT `food_comment_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `food_product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `food_comment_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '缇庨?璇勮?琛' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of food_comment
-- ----------------------------

-- ----------------------------
-- Table structure for food_like
-- ----------------------------
DROP TABLE IF EXISTS `food_like`;
CREATE TABLE `food_like`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '鐐硅禐ID',
  `user_id` bigint NOT NULL COMMENT '鐢ㄦ埛ID',
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鐩?爣绫诲瀷: store/product',
  `target_id` bigint NOT NULL COMMENT '鐩?爣ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_target`(`user_id` ASC, `target_type` ASC, `target_id` ASC) USING BTREE,
  INDEX `idx_target`(`target_type` ASC, `target_id` ASC) USING BTREE,
  CONSTRAINT `food_like_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '缇庨?鐐硅禐琛' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of food_like
-- ----------------------------

-- ----------------------------
-- Table structure for food_product
-- ----------------------------
DROP TABLE IF EXISTS `food_product`;
CREATE TABLE `food_product`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `store_id` bigint NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `price` decimal(10, 2) NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `rating` decimal(2, 1) NULL DEFAULT 0.0,
  `review_count` int NULL DEFAULT 0,
  `likes` int NULL DEFAULT 0,
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `spiciness` tinyint NULL DEFAULT 0,
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `available` tinyint NULL DEFAULT 1,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_store`(`store_id` ASC) USING BTREE,
  CONSTRAINT `food_product_ibfk_1` FOREIGN KEY (`store_id`) REFERENCES `food_store` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of food_product
-- ----------------------------
INSERT INTO `food_product` VALUES (1, 1, 'Beef Rice', 15.00, 'Delicious beef with rice', NULL, 4.6, 0, 0, 'Main', 0, 'Popular', 1, '2025-11-30 14:22:39', '2025-11-30 14:22:39');
INSERT INTO `food_product` VALUES (2, 2, 'Latte', 18.00, 'Fresh coffee', NULL, 4.8, 0, 0, 'Drink', 0, 'Coffee', 1, '2025-11-30 14:22:39', '2025-11-30 14:22:39');
INSERT INTO `food_product` VALUES (3, 1, 'Kung Pao Chicken', 18.00, 'Classic Sichuan dish', 'https://picsum.photos/200/200?random=10', 4.5, 120, 80, 'main', 2, NULL, 1, '2025-12-01 04:29:55', '2025-12-01 04:29:55');
INSERT INTO `food_product` VALUES (4, 1, 'Mapo Tofu', 15.00, 'Spicy tofu dish', 'https://picsum.photos/200/200?random=11', 4.3, 90, 60, 'main', 3, NULL, 1, '2025-12-01 04:29:55', '2025-12-01 04:29:55');
INSERT INTO `food_product` VALUES (5, 1, 'Fried Rice', 12.00, 'Egg fried rice', 'https://picsum.photos/200/200?random=12', 4.2, 200, 100, 'staple', 0, NULL, 1, '2025-12-01 04:29:55', '2025-12-01 04:29:55');
INSERT INTO `food_product` VALUES (6, 2, 'Latte', 18.00, 'Fresh coffee', 'https://picsum.photos/200/200?random=13', 4.6, 150, 100, 'drink', 0, NULL, 1, '2025-12-01 04:29:55', '2025-12-01 04:29:55');
INSERT INTO `food_product` VALUES (7, 2, 'Cheesecake', 22.00, 'New York style', 'https://picsum.photos/200/200?random=14', 4.7, 80, 70, 'dessert', 0, NULL, 1, '2025-12-01 04:29:55', '2025-12-01 04:29:55');
INSERT INTO `food_product` VALUES (8, 3, 'Beef Noodles', 16.00, 'Hand-pulled noodles', 'https://picsum.photos/200/200?random=15', 4.8, 180, 140, 'noodle', 1, NULL, 1, '2025-12-01 04:29:55', '2025-12-01 04:29:55');
INSERT INTO `food_product` VALUES (9, 3, 'Lamb Noodles', 18.00, 'With lamb soup', 'https://picsum.photos/200/200?random=16', 4.6, 120, 90, 'noodle', 1, NULL, 1, '2025-12-01 04:29:55', '2025-12-01 04:29:55');
INSERT INTO `food_product` VALUES (10, 4, 'BBQ Pork Skewer', 8.00, 'Grilled pork', 'https://picsum.photos/200/200?random=17', 4.5, 200, 150, 'bbq', 1, NULL, 1, '2025-12-01 04:30:05', '2025-12-01 04:30:05');
INSERT INTO `food_product` VALUES (11, 4, 'Lamb Skewer', 10.00, 'Grilled lamb', 'https://picsum.photos/200/200?random=18', 4.7, 180, 140, 'bbq', 2, NULL, 1, '2025-12-01 04:30:05', '2025-12-01 04:30:05');
INSERT INTO `food_product` VALUES (12, 5, 'Salmon Sushi', 28.00, 'Fresh salmon', 'https://picsum.photos/200/200?random=19', 4.8, 100, 90, 'sushi', 0, NULL, 1, '2025-12-01 04:30:05', '2025-12-01 04:30:05');
INSERT INTO `food_product` VALUES (13, 5, 'Tuna Roll', 25.00, 'Tuna maki roll', 'https://picsum.photos/200/200?random=20', 4.6, 80, 70, 'sushi', 0, NULL, 1, '2025-12-01 04:30:05', '2025-12-01 04:30:05');
INSERT INTO `food_product` VALUES (14, 6, 'Pearl Milk Tea', 12.00, 'Classic bubble tea', 'https://picsum.photos/200/200?random=21', 4.7, 300, 250, 'tea', 0, NULL, 1, '2025-12-01 04:30:05', '2025-12-01 04:30:05');
INSERT INTO `food_product` VALUES (15, 6, 'Matcha Latte', 15.00, 'Japanese matcha', 'https://picsum.photos/200/200?random=22', 4.5, 150, 120, 'tea', 0, NULL, 1, '2025-12-01 04:30:05', '2025-12-01 04:30:05');

-- ----------------------------
-- Table structure for food_review
-- ----------------------------
DROP TABLE IF EXISTS `food_review`;
CREATE TABLE `food_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `store_id` bigint NULL DEFAULT NULL,
  `product_id` bigint NULL DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `rating` decimal(2, 1) NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `images` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_store`(`store_id` ASC) USING BTREE,
  INDEX `idx_product`(`product_id` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `food_review_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of food_review
-- ----------------------------

-- ----------------------------
-- Table structure for food_store
-- ----------------------------
DROP TABLE IF EXISTS `food_store`;
CREATE TABLE `food_store`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `rating` decimal(2, 1) NULL DEFAULT 0.0,
  `review_count` int NULL DEFAULT 0,
  `likes` int NULL DEFAULT 0,
  `avg_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '浜哄潎浠锋牸',
  `business_hours` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` tinyint NULL DEFAULT 1,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_location`(`location` ASC) USING BTREE,
  INDEX `idx_rating`(`rating` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of food_store
-- ----------------------------
INSERT INTO `food_store` VALUES (1, 'Canteen 1', 'Building A', 'Main canteen', NULL, 4.5, 128, 0, NULL, '06:30-21:00', NULL, 'Chinese,Fast', 1, '2025-11-30 14:22:39', '2025-11-30 14:22:39');
INSERT INTO `food_store` VALUES (2, 'Cafe', 'Library Floor 1', 'Coffee shop', NULL, 4.7, 234, 0, NULL, '08:00-22:00', NULL, 'Coffee,Dessert', 1, '2025-11-30 14:22:39', '2025-11-30 14:22:39');
INSERT INTO `food_store` VALUES (3, 'Noodle House', 'canteen', 'Best hand-pulled noodles', 'https://picsum.photos/400/300?random=3', 4.7, 200, 150, 15.00, NULL, NULL, NULL, 1, '2025-12-01 04:28:41', '2025-12-01 04:28:41');
INSERT INTO `food_store` VALUES (4, 'BBQ Corner', 'street', 'Grilled meat specialty', 'https://picsum.photos/400/300?random=4', 4.5, 180, 120, 25.00, NULL, NULL, NULL, 1, '2025-12-01 04:28:41', '2025-12-01 04:28:41');
INSERT INTO `food_store` VALUES (5, 'Sushi Bar', 'east-gate', 'Fresh Japanese cuisine', 'https://picsum.photos/400/300?random=5', 4.8, 150, 180, 35.00, NULL, NULL, NULL, 1, '2025-12-01 04:28:41', '2025-12-01 04:28:41');
INSERT INTO `food_store` VALUES (6, 'Bubble Tea', 'west-gate', 'Various milk tea flavors', 'https://picsum.photos/400/300?random=6', 4.6, 300, 250, 12.00, NULL, NULL, NULL, 1, '2025-12-01 04:28:41', '2025-12-01 04:28:41');

-- ----------------------------
-- Table structure for gallery_category
-- ----------------------------
DROP TABLE IF EXISTS `gallery_category`;
CREATE TABLE `gallery_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `cover_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `sort_order` int NULL DEFAULT 0,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gallery_category
-- ----------------------------
INSERT INTO `gallery_category` VALUES (1, 'Campus', 'Campus scenery', NULL, 1, '2025-11-30 14:22:39');
INSERT INTO `gallery_category` VALUES (2, 'Activities', 'Campus activities', NULL, 2, '2025-11-30 14:22:39');

-- ----------------------------
-- Table structure for gallery_image
-- ----------------------------
DROP TABLE IF EXISTS `gallery_image`;
CREATE TABLE `gallery_image`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `thumbnail_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `width` int NULL DEFAULT NULL,
  `height` int NULL DEFAULT NULL,
  `file_size` bigint NULL DEFAULT NULL,
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'campus',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `view_count` int NULL DEFAULT 0,
  `like_count` int NULL DEFAULT 0,
  `status` tinyint NULL DEFAULT 0,
  `reject_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `reviewed_by` bigint NULL DEFAULT NULL,
  `reviewed_at` datetime NULL DEFAULT NULL,
  `is_featured` tinyint NULL DEFAULT 0,
  `sort_order` int NULL DEFAULT 0,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_is_featured`(`is_featured` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gallery_image
-- ----------------------------
INSERT INTO `gallery_image` VALUES (14, 13, '撒大苏打', '阿三大苏打', '撒大苏打的', 'https://r2.wic.edu.kg/gallery/2025/12/01/08279128777d44f29acad3f9b8d42e0c.gif', 'https://r2.wic.edu.kg/gallery/thumbnails/2025/12/01/c090b320560849319dde2951c4a5486d.gif', 900, 900, 422338, 'image/gif', 'other', '阿三大苏打', 0, 0, 1, NULL, 13, '2025-12-01 22:32:16', 0, 0, '2025-12-01 22:31:23', '2025-12-01 22:32:16');
INSERT INTO `gallery_image` VALUES (15, 13, '胡天赐', '最帅深情大王', '南一544', 'https://r2.wic.edu.kg/gallery/2025/12/01/624c8c8c7dc049d7892b2b8edc37205b.jpg', 'https://r2.wic.edu.kg/gallery/thumbnails/2025/12/01/9d4aa11a7b0a43109e47b09110d75483.jpg', 1000, 1000, 187438, 'image/jpeg', 'life', NULL, 0, 0, 1, NULL, 13, '2025-12-01 22:45:48', 0, 0, '2025-12-01 22:45:15', '2025-12-01 22:45:48');
INSERT INTO `gallery_image` VALUES (18, 15, '测试图片', '测试图片', '测试图片', 'https://r2.wic.edu.kg/gallery/2025/12/01/cafd781fa37348dcb1f92ee4fd35d38b.png', 'https://r2.wic.edu.kg/gallery/thumbnails/2025/12/01/9c8a4fabafce4119a257b3ea7b19c201.png', 1911, 1002, 2416246, 'image/png', 'other', '测试图片', 0, 0, 0, NULL, NULL, NULL, 0, 0, '2025-12-01 23:39:19', '2025-12-01 23:39:19');
INSERT INTO `gallery_image` VALUES (19, 13, '测试图片', '测试图片', '测试图片', 'https://r2.wic.edu.kg/gallery/2025/12/02/b4d96420018e4e40a2b967565d370c14.png', 'https://r2.wic.edu.kg/gallery/thumbnails/2025/12/02/256df4633e8143feb24c5700ecc0ffb7.png', 3840, 2160, 9794249, 'image/png', 'other', '测试图片', 0, 0, 0, NULL, NULL, NULL, 0, 0, '2025-12-02 00:56:27', '2025-12-02 00:56:27');

-- ----------------------------
-- Table structure for gallery_like
-- ----------------------------
DROP TABLE IF EXISTS `gallery_like`;
CREATE TABLE `gallery_like`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `photo_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_photo_user`(`photo_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `gallery_like_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `gallery_like_image_fk` FOREIGN KEY (`photo_id`) REFERENCES `gallery_image` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gallery_like
-- ----------------------------

-- ----------------------------
-- Table structure for library_book
-- ----------------------------
DROP TABLE IF EXISTS `library_book`;
CREATE TABLE `library_book`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `author` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `isbn` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `publisher` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `publish_date` date NULL DEFAULT NULL,
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `cover_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `total_copies` int NULL DEFAULT 1,
  `available_copies` int NULL DEFAULT 1,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_title`(`title` ASC) USING BTREE,
  INDEX `idx_author`(`author` ASC) USING BTREE,
  INDEX `idx_isbn`(`isbn` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of library_book
-- ----------------------------
INSERT INTO `library_book` VALUES (1, 'Computer Systems', 'Bryant', '978-7-111-54493-7', 'Classic CS book', 'Publisher', NULL, 'CS', NULL, 'A3', 5, 3, '2025-11-30 14:22:39', '2025-11-30 14:22:39');
INSERT INTO `library_book` VALUES (2, 'Algorithms', 'Cormen', '978-7-111-40701-0', 'Algorithm bible', 'Publisher', NULL, 'CS', NULL, 'A3', 3, 1, '2025-11-30 14:22:39', '2025-11-30 14:22:39');

-- ----------------------------
-- Table structure for library_borrow
-- ----------------------------
DROP TABLE IF EXISTS `library_borrow`;
CREATE TABLE `library_borrow`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `book_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `borrow_date` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `due_date` datetime NOT NULL,
  `return_date` datetime NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'BORROWING',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_book`(`book_id` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `library_borrow_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `library_book` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `library_borrow_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of library_borrow
-- ----------------------------

-- ----------------------------
-- Table structure for senior_note
-- ----------------------------
DROP TABLE IF EXISTS `senior_note`;
CREATE TABLE `senior_note`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `views` int NULL DEFAULT 0,
  `likes` int NULL DEFAULT 0,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  CONSTRAINT `senior_note_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of senior_note
-- ----------------------------
INSERT INTO `senior_note` VALUES (1, 2, 'Exam Tips', 'Study tips for finals...', 'Study', 'exam,study', 0, 0, '2025-11-30 14:22:39', '2025-11-30 14:22:39');
INSERT INTO `senior_note` VALUES (2, 3, 'Freshman Guide', 'Tips for new students', 'Guide', 'freshman,tips', 0, 0, '2025-11-30 14:22:39', '2025-11-30 14:22:39');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `student_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `department` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `status` tinyint NULL DEFAULT 1,
  `role` int NOT NULL DEFAULT 3 COMMENT '用户角色: 1-组织者, 2-管理员, 3-普通用户',
  `deleted` tinyint NULL DEFAULT 0,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `student_id`(`student_id` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '20210001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt/AB/O', 'Admin', 'admin@wic.edu.kg', NULL, 'IT', 'CS', NULL, 1, 3, 0, '2025-11-30 14:22:21', '2025-11-30 14:28:52');
INSERT INTO `sys_user` VALUES (2, 'xiaoming', '20210002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt/AB/O', 'Zhang Ming', 'student1@wic.edu.kg', NULL, 'IT', 'SE', NULL, 1, 3, 0, '2025-11-30 14:22:21', '2025-11-30 14:28:52');
INSERT INTO `sys_user` VALUES (3, 'xiaohong', '20210003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt/AB/O', 'Li Hong', 'student2@wic.edu.kg', NULL, 'Foreign Lang', 'English', NULL, 1, 3, 0, '2025-11-30 14:22:21', '2025-11-30 14:28:52');
INSERT INTO `sys_user` VALUES (5, 'alex_zhang', '202103058', '$2a$10$zgW9.HmEOTl7zg2mUBLejuyDh1Ixz3Mih9UcGTBUfHiMJBjpq9WiS', '张三', 'alex.zhang@wic.edu.kg', NULL, '信息工程学院', '软件工程', NULL, 1, 3, 0, '2025-12-01 01:28:56', '2025-12-01 01:36:26');
INSERT INTO `sys_user` VALUES (6, 'test_email', '202103100', '$2a$10$YjGHxk7Ps2dZfbomVTlk0e3sfWfXiE22wSRaYHLAEI7VVyDJ8X7M.', '邮件测试用户', 'leafoneforum@163.com', NULL, '计算机学院', '软件工程', NULL, 0, 3, 0, '2025-12-01 01:45:42', '2025-12-01 01:45:42');
INSERT INTO `sys_user` VALUES (7, 'testlinkuser', '20250002', '$2a$10$VdiFuXJ4T.CftlOfM8.LZerlXoToQuLWv0rj.v8SvD..Kpi/8JdIS', 'LinkTest', 'test123456@163.com', NULL, 'CS', 'SE', NULL, 0, 3, 0, '2025-12-01 01:59:41', '2025-12-01 01:59:41');
INSERT INTO `sys_user` VALUES (8, 'linkactivatetest', '20250099', '$2a$10$FpXMtwWaQNT34tzUryAjAutyk8DDwhTTjHDyKL9fE5ExQBMToUgDa', 'LinkActivateTest', 'linktest@test.com', NULL, 'CS', 'SE', NULL, 0, 3, 0, '2025-12-01 01:59:56', '2025-12-01 01:59:56');
INSERT INTO `sys_user` VALUES (9, 'testadmin', '20250101', '$2a$10$zly/3IEGfcXIr15DixgkQ.TKmbwdxSnBEKYyx1sVXKOi5IHeK/UyS', 'TestAdmin', 'testadmin@test.com', NULL, 'CS', 'SE', NULL, 0, 3, 0, '2025-12-01 02:21:09', '2025-12-01 02:21:09');
INSERT INTO `sys_user` VALUES (13, 'luotao', '32432432', '$2a$10$s7tM/DsAyRgjySVy1SmOLePv8wT3Xs7rq5voEe.p3rmPTfNywHFIK', 'luotao', '18727430326@163.com', 'https://r2.wic.edu.kg/gallery/thumbnails/2025/12/01/%E6%A0%A1%E5%BE%BD.jpg', 'asgasgas', 'dgasgsdgasd', 'asdgasdgdasg', 1, 2, 0, '2025-12-01 02:34:24', '2025-12-02 00:25:30');
INSERT INTO `sys_user` VALUES (15, '深情大王', '202310137628', '$2a$10$CN5us6TjVvtgk4xK5e8xn.8ZQ2cUEZ7sQdsLneMwzm9/6MGrQ5DlW', '胡天赐', 'postmaster@luotao.qzz.io', 'https://r2.wic.edu.kg/avatar/2025/12/02/f4026d3a0db74331aa333e8bd166f583.gif', '信息工程', '计算机科学与技术', '深情大王', 1, 3, 0, '2025-12-01 21:43:07', '2025-12-02 00:24:41');

SET FOREIGN_KEY_CHECKS = 1;
