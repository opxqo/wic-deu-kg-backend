-- 学院动态表 (Article)
-- 用于存储学校新闻、公告、动态等内容

CREATE TABLE `article` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `subtitle` varchar(500) DEFAULT NULL COMMENT '副标题',
  `author` varchar(100) DEFAULT NULL COMMENT '作者',
  `publish_date` date DEFAULT NULL COMMENT '发布日期',
  `read_time` varchar(50) DEFAULT NULL COMMENT '阅读时长',
  `cover_image` varchar(500) DEFAULT NULL COMMENT '封面图片URL',
  `tags` varchar(500) DEFAULT NULL COMMENT '标签(逗号分隔)',
  `content` text COMMENT 'HTML内容',
  `view_count` int DEFAULT 0 COMMENT '浏览次数',
  `status` tinyint DEFAULT 1 COMMENT '状态: 0-草稿, 1-已发布, 2-已下架',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_publish_date` (`publish_date` DESC),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学院动态表';

-- 插入示例数据
INSERT INTO `article` (`title`, `subtitle`, `author`, `publish_date`, `read_time`, `cover_image`, `tags`, `content`, `view_count`, `status`) VALUES
('我校AI实验室团队在国际机器人大赛中荣获金奖', '地平线理工学院代表队凭借自主研发的救援机器人夺冠', 'WIC 宣传部', '2024-03-15', '5 min read', 'https://images.unsplash.com/photo-1485827404703-89b55fcc595e?q=80&w=2070&auto=format&fit=crop', '学术成就,人工智能,机器人', '<p>在刚刚结束的 <strong>ICRA 2024</strong> 会议上，我校AI实验室团队凭借自主研发的救援机器人在激烈的国际竞争中脱颖而出，荣获金奖。</p><h2>赛场直击</h2><p>本次比赛共吸引了来自全球50多个国家和地区的300多支队伍参加，竞争异常激烈。我校团队精心准备了近一年时间，最终凭借优异的表现获得评委一致好评。</p><h2>技术亮点</h2><p>团队研发的救援机器人采用了先进的视觉识别系统和自主导航算法，能够在复杂环境中快速定位并救援被困人员。</p>', 128, 1),
('2024年春季学期开学典礼圆满举行', '校长发表重要讲话，寄语全体师生', 'WIC 宣传部', '2024-02-26', '3 min read', 'https://images.unsplash.com/photo-1523050854058-8df90110c9f1?q=80&w=2070&auto=format&fit=crop', '校园活动,开学典礼', '<p>2024年2月26日上午，我校2024年春季学期开学典礼在大礼堂隆重举行。</p><p>校长在讲话中强调，新学期要继续深化教育改革，提升教学质量，培养更多优秀人才。</p>', 256, 1),
('信息工程学部与华为公司签署战略合作协议', '产学研深度融合，共育创新人才', 'WIC 宣传部', '2024-03-10', '4 min read', 'https://images.unsplash.com/photo-1560179707-f14e90ef3623?q=80&w=2073&auto=format&fit=crop', '校企合作,信息工程', '<p>3月10日，信息工程学部与华为技术有限公司正式签署战略合作协议。</p><p>双方将在人才培养、技术研发、实习就业等方面开展深入合作，共同推动产学研深度融合。</p>', 189, 1);
