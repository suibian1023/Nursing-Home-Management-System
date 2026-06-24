-- ============================================================
-- 东软颐养中心 (Neusoft Senior Care Center)
-- 养老管理系统 数据库脚本
-- 数据库名称: yyzx
-- ============================================================

CREATE DATABASE IF NOT EXISTS yyzx
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE yyzx;

-- ============================================================
-- 1. role 角色表
-- ============================================================
DROP TABLE IF EXISTS `rolemenu`;
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
    `id`          INT           NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name`   VARCHAR(50)   NOT NULL                COMMENT '角色名称',
    `description` VARCHAR(255)  DEFAULT NULL            COMMENT '角色描述',
    `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted`  INT           DEFAULT 0               COMMENT '逻辑删除:0=显示,1=隐藏',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ============================================================
-- 2. user 用户表
-- ============================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`           INT           NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`     VARCHAR(50)   NOT NULL                COMMENT '用户名',
    `password`     VARCHAR(255)  NOT NULL                COMMENT '密码',
    `nickname`     VARCHAR(50)   DEFAULT NULL            COMMENT '昵称',
    `email`        VARCHAR(100)  DEFAULT NULL            COMMENT '邮箱',
    `phone_number` VARCHAR(20)   DEFAULT NULL            COMMENT '手机号',
    `sex`          INT           DEFAULT 0               COMMENT '性别:0=男,1=女',
    `role_id`      INT           DEFAULT NULL            COMMENT '角色ID',
    `avatar`       VARCHAR(255)  DEFAULT NULL            COMMENT '头像',
    `create_by`    VARCHAR(50)   DEFAULT NULL            COMMENT '创建人',
    `create_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted`   INT           DEFAULT 0               COMMENT '逻辑删除:0=显示,1=隐藏',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role_id` (`role_id`),
    CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================
-- 3. menu 菜单表
-- ============================================================
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu` (
    `id`          INT           NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `menus_index` VARCHAR(255)  DEFAULT NULL            COMMENT '菜单索引/标识',
    `title`       VARCHAR(100)  NOT NULL                COMMENT '菜单标题',
    `icon`        VARCHAR(255)  DEFAULT NULL            COMMENT '菜单图标',
    `path`        VARCHAR(255)  DEFAULT NULL            COMMENT '菜单路径',
    `parent_id`   INT           DEFAULT NULL            COMMENT '父菜单ID',
    `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted`  INT           DEFAULT 0               COMMENT '逻辑删除:0=显示,1=隐藏',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

-- ============================================================
-- 4. rolemenu 角色菜单关联表
-- ============================================================
CREATE TABLE `rolemenu` (
    `id`          INT      NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `role_id`     INT      NOT NULL                COMMENT '角色ID',
    `menu_id`     INT      NOT NULL                COMMENT '菜单ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted`  INT      DEFAULT 0               COMMENT '逻辑删除:0=显示,1=隐藏',
    PRIMARY KEY (`id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_menu_id` (`menu_id`),
    CONSTRAINT `fk_rolemenu_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
    CONSTRAINT `fk_rolemenu_menu` FOREIGN KEY (`menu_id`) REFERENCES `menu` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- ============================================================
-- 5. nurselevel 护理等级表
-- ============================================================
DROP TABLE IF EXISTS `customernurseitem`;
DROP TABLE IF EXISTS `nurselevelitem`;
DROP TABLE IF EXISTS `nurserecord`;
DROP TABLE IF EXISTS `nurselevel`;
CREATE TABLE `nurselevel` (
    `id`          INT            NOT NULL AUTO_INCREMENT COMMENT '等级ID',
    `level_name`  VARCHAR(50)    NOT NULL                COMMENT '等级名称',
    `description` VARCHAR(255)   DEFAULT NULL            COMMENT '等级描述',
    `price`       DECIMAL(10,2)  DEFAULT 0.00            COMMENT '价格',
    `create_time` DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted`  INT            DEFAULT 0               COMMENT '逻辑删除:0=显示,1=隐藏',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护理等级表';

-- ============================================================
-- 6. nursecontent 护理内容表
-- ============================================================
CREATE TABLE `nursecontent` (
    `id`          INT           NOT NULL AUTO_INCREMENT COMMENT '内容ID',
    `content_name` VARCHAR(100) NOT NULL                COMMENT '内容名称',
    `description` VARCHAR(255)  DEFAULT NULL            COMMENT '内容描述',
    `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted`  INT           DEFAULT 0               COMMENT '逻辑删除:0=显示,1=隐藏',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护理内容表';

-- ============================================================
-- 7. nurselevelitem 护理等级项目关联表
-- ============================================================
CREATE TABLE `nurselevelitem` (
    `id`               INT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `level_id`         INT NOT NULL                COMMENT '护理等级ID',
    `nurse_content_id` INT NOT NULL                COMMENT '护理内容ID',
    `create_time`      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted`       INT      DEFAULT 0               COMMENT '逻辑删除:0=显示,1=隐藏',
    PRIMARY KEY (`id`),
    KEY `idx_level_id` (`level_id`),
    KEY `idx_nurse_content_id` (`nurse_content_id`),
    CONSTRAINT `fk_nli_level` FOREIGN KEY (`level_id`) REFERENCES `nurselevel` (`id`),
    CONSTRAINT `fk_nli_content` FOREIGN KEY (`nurse_content_id`) REFERENCES `nursecontent` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护理等级项目关联表';

-- ============================================================
-- 8. room 房间表
-- ============================================================
DROP TABLE IF EXISTS `beddetails`;
DROP TABLE IF EXISTS `bed`;
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room` (
    `id`          INT          NOT NULL AUTO_INCREMENT COMMENT '房间ID',
    `room_no`     VARCHAR(50)  NOT NULL                COMMENT '房间号',
    `building_no` VARCHAR(50)  DEFAULT NULL            COMMENT '楼栋号',
    `floor`       VARCHAR(50)  DEFAULT NULL            COMMENT '楼层',
    `room_type`   VARCHAR(50)  DEFAULT NULL            COMMENT '房间类型',
    `bed_count`   INT          DEFAULT 0               COMMENT '床位数量',
    `empty_bed`   INT          DEFAULT 0               COMMENT '空床数量',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted`  INT          DEFAULT 0               COMMENT '逻辑删除:0=显示,1=隐藏',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_room_no` (`room_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房间表';

-- ============================================================
-- 9. bed 床位表
-- ============================================================
CREATE TABLE `bed` (
    `id`          INT          NOT NULL AUTO_INCREMENT COMMENT '床位ID',
    `bed_no`      VARCHAR(50)  NOT NULL                COMMENT '床位号',
    `room_no`     VARCHAR(50)  NOT NULL                COMMENT '房间号',
    `is_used`     INT          DEFAULT 0               COMMENT '使用状态:0=空闲,1=占用',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted`  INT          DEFAULT 0               COMMENT '逻辑删除:0=显示,1=隐藏',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_bed_no` (`bed_no`),
    KEY `idx_room_no` (`room_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='床位表';

-- ============================================================
-- 10. customer 客户(老人)表
-- ============================================================
DROP TABLE IF EXISTS `outward`;
DROP TABLE IF EXISTS `backdown`;
DROP TABLE IF EXISTS `customernurseitem`;
DROP TABLE IF EXISTS `nurserecord`;
DROP TABLE IF EXISTS `customerpreference`;
DROP TABLE IF EXISTS `meal`;
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
    `id`             INT          NOT NULL AUTO_INCREMENT COMMENT '客户ID',
    `customer_name`  VARCHAR(50)  NOT NULL                COMMENT '客户姓名',
    `customer_sex`   INT          DEFAULT 0               COMMENT '性别:0=男,1=女',
    `customer_age`   INT          DEFAULT NULL            COMMENT '年龄',
    `id_card`        VARCHAR(18)  DEFAULT NULL            COMMENT '身份证号',
    `phone`          VARCHAR(20)  DEFAULT NULL            COMMENT '联系电话',
    `address`        VARCHAR(255) DEFAULT NULL            COMMENT '家庭住址',
    `checkin_date`   DATE         DEFAULT NULL            COMMENT '入住日期',
    `room_no`        VARCHAR(50)  DEFAULT NULL            COMMENT '房间号',
    `bed_id`         INT          DEFAULT NULL            COMMENT '床位ID',
    `nurse_level_id` INT          DEFAULT NULL            COMMENT '护理等级ID',
    `user_id`        INT          DEFAULT NULL            COMMENT '健康管家ID',
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted`     INT          DEFAULT 0               COMMENT '逻辑删除:0=显示,1=隐藏',
    PRIMARY KEY (`id`),
    KEY `idx_bed_id` (`bed_id`),
    KEY `idx_nurse_level_id` (`nurse_level_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_customer_bed` FOREIGN KEY (`bed_id`) REFERENCES `bed` (`id`),
    CONSTRAINT `fk_customer_nurselevel` FOREIGN KEY (`nurse_level_id`) REFERENCES `nurselevel` (`id`),
    CONSTRAINT `fk_customer_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户(老人)表';

-- ============================================================
-- 11. beddetails 床位详情表
-- ============================================================
CREATE TABLE `beddetails` (
    `id`            INT          NOT NULL AUTO_INCREMENT COMMENT '详情ID',
    `customer_id`   INT          DEFAULT NULL            COMMENT '客户ID',
    `customer_name` VARCHAR(50)  DEFAULT NULL            COMMENT '客户姓名',
    `customer_sex`  INT          DEFAULT NULL            COMMENT '客户性别',
    `bed_details`   VARCHAR(255) DEFAULT NULL            COMMENT '床位详情',
    `room_no`       VARCHAR(50)  DEFAULT NULL            COMMENT '房间号',
    `start_date`    DATE         DEFAULT NULL            COMMENT '开始日期',
    `end_date`      DATE         DEFAULT NULL            COMMENT '结束日期',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted`    INT          DEFAULT 0               COMMENT '逻辑删除:0=显示,1=隐藏',
    PRIMARY KEY (`id`),
    KEY `idx_customer_id` (`customer_id`),
    CONSTRAINT `fk_beddetails_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='床位详情表';

-- ============================================================
-- 12. nurserecord 护理记录表
-- ============================================================
CREATE TABLE `nurserecord` (
    `id`               INT          NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `customer_id`      INT          DEFAULT NULL            COMMENT '客户ID',
    `nurse_content_id` INT          DEFAULT NULL            COMMENT '护理内容ID',
    `record_date`      DATE         DEFAULT NULL            COMMENT '记录日期',
    `nurse_time`       VARCHAR(50)  DEFAULT NULL            COMMENT '护理时间',
    `staff_name`       VARCHAR(100) DEFAULT NULL            COMMENT '护理人员',
    `description`      VARCHAR(255) DEFAULT NULL            COMMENT '描述',
    `create_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted`       INT          DEFAULT 0               COMMENT '逻辑删除:0=显示,1=隐藏',
    PRIMARY KEY (`id`),
    KEY `idx_customer_id` (`customer_id`),
    KEY `idx_nurse_content_id` (`nurse_content_id`),
    CONSTRAINT `fk_nurserecord_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
    CONSTRAINT `fk_nurserecord_content` FOREIGN KEY (`nurse_content_id`) REFERENCES `nursecontent` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护理记录表';

-- ============================================================
-- 13. customernurseitem 客户护理项目表
-- ============================================================
CREATE TABLE `customernurseitem` (
    `id`            INT      NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `customer_id`   INT      DEFAULT NULL            COMMENT '客户ID',
    `nurse_item_id` INT      DEFAULT NULL            COMMENT '护理等级项目ID',
    `nurse_count`   INT      DEFAULT 0               COMMENT '护理次数',
    `nurse_date`    DATE     DEFAULT NULL            COMMENT '护理日期',
    `create_time`   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted`    INT      DEFAULT 0               COMMENT '逻辑删除:0=显示,1=隐藏',
    PRIMARY KEY (`id`),
    KEY `idx_customer_id` (`customer_id`),
    KEY `idx_nurse_item_id` (`nurse_item_id`),
    CONSTRAINT `fk_cni_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
    CONSTRAINT `fk_cni_nurseitem` FOREIGN KEY (`nurse_item_id`) REFERENCES `nurselevelitem` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户护理项目表';

-- ============================================================
-- 14. outward 外出登记表
-- ============================================================
CREATE TABLE `outward` (
    `id`               INT          NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `customer_id`      INT          DEFAULT NULL            COMMENT '客户ID',
    `customer_name`    VARCHAR(50)  DEFAULT NULL            COMMENT '客户姓名',
    `out_date`         DATE         DEFAULT NULL            COMMENT '外出日期',
    `reason`           VARCHAR(500) DEFAULT NULL            COMMENT '外出原因',
    `expect_back_date` DATE         DEFAULT NULL            COMMENT '预计归来日期',
    `create_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted`       INT          DEFAULT 0               COMMENT '逻辑删除:0=显示,1=隐藏',
    PRIMARY KEY (`id`),
    KEY `idx_customer_id` (`customer_id`),
    CONSTRAINT `fk_outward_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='外出登记表';

-- ============================================================
-- 15. backdown 归来登记表
-- ============================================================
CREATE TABLE `backdown` (
    `id`            INT         NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `customer_id`   INT         DEFAULT NULL            COMMENT '客户ID',
    `customer_name` VARCHAR(50) DEFAULT NULL            COMMENT '客户姓名',
    `back_date`     DATE        DEFAULT NULL            COMMENT '归来日期',
    `create_time`   DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted`    INT         DEFAULT 0               COMMENT '逻辑删除:0=显示,1=隐藏',
    PRIMARY KEY (`id`),
    KEY `idx_customer_id` (`customer_id`),
    CONSTRAINT `fk_backdown_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='归来登记表';

-- ============================================================
-- 16. food 菜品表
-- ============================================================
DROP TABLE IF EXISTS `customerpreference`;
DROP TABLE IF EXISTS `meal`;
DROP TABLE IF EXISTS `food`;
CREATE TABLE `food` (
    `id`          INT            NOT NULL AUTO_INCREMENT COMMENT '菜品ID',
    `food_name`   VARCHAR(100)   NOT NULL                COMMENT '菜品名称',
    `food_type`   VARCHAR(50)    DEFAULT NULL            COMMENT '菜品类型',
    `price`       DECIMAL(10,2)  DEFAULT 0.00            COMMENT '价格',
    `description` VARCHAR(255)   DEFAULT NULL            COMMENT '描述',
    `create_time` DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted`  INT            DEFAULT 0               COMMENT '逻辑删除:0=显示,1=隐藏',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜品表';

-- ============================================================
-- 17. customerpreference 客户饮食偏好表
-- ============================================================
CREATE TABLE `customerpreference` (
    `id`              INT         NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `customer_id`     INT         DEFAULT NULL            COMMENT '客户ID',
    `food_id`         INT         DEFAULT NULL            COMMENT '菜品ID',
    `preference_type` VARCHAR(20) DEFAULT NULL            COMMENT '偏好类型:like=喜欢,dislike=不喜欢',
    `create_time`     DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted`      INT         DEFAULT 0               COMMENT '逻辑删除:0=显示,1=隐藏',
    PRIMARY KEY (`id`),
    KEY `idx_customer_id` (`customer_id`),
    KEY `idx_food_id` (`food_id`),
    CONSTRAINT `fk_cp_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
    CONSTRAINT `fk_cp_food` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户饮食偏好表';

-- ============================================================
-- 18. meal 膳食记录表
-- ============================================================
CREATE TABLE `meal` (
    `id`          INT          NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `customer_id` INT          DEFAULT NULL            COMMENT '客户ID',
    `meal_date`   DATE         DEFAULT NULL            COMMENT '膳食日期',
    `meal_type`   VARCHAR(20)  DEFAULT NULL            COMMENT '餐别:早餐/午餐/晚餐',
    `food_ids`    VARCHAR(500) DEFAULT NULL            COMMENT '菜品ID列表',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted`  INT          DEFAULT 0               COMMENT '逻辑删除:0=显示,1=隐藏',
    PRIMARY KEY (`id`),
    KEY `idx_customer_id` (`customer_id`),
    KEY `idx_meal_date` (`meal_date`),
    CONSTRAINT `fk_meal_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='膳食记录表';


-- ============================================================
-- 插入测试数据
-- ============================================================

-- ---------- role ----------
INSERT INTO `role` (`id`, `role_name`, `description`) VALUES
(1, '超级管理员', '系统超级管理员，拥有所有权限'),
(2, '健康管家', '老人健康管家，负责老人的日常护理管理'),
(3, '普通用户', '普通用户，可查看基本信息');

-- ---------- user ----------
-- 密码为明文测试数据，实际项目中应使用加密存储
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `email`, `phone_number`, `sex`, `role_id`, `avatar`, `create_by`) VALUES
(1, 'admin', 'admin123', '系统管理员', 'admin@neuedu.cn', '13800000001', 0, 1, NULL, 'system'),
(2, 'guanjia', '123456', '健康管家小王', 'guanjia@neuedu.cn', '13800000002', 0, 2, NULL, 'admin');

-- ---------- menu ----------
-- 顶级菜单 (parent_id IS NULL)
INSERT INTO `menu` (`id`, `menus_index`, `title`, `icon`, `path`, `parent_id`) VALUES
(1,  'dashboard',      '首页',       'HomeOutlined',        '/dashboard',          NULL),
(2,  'system',         '系统管理',    'SettingOutlined',     '',                    NULL),
(3,  'customer',       '客户管理',    'TeamOutlined',        '',                    NULL),
(4,  'bed',            '床位管理',    'BankOutlined',        '',                    NULL),
(5,  'nursing',        '护理管理',    'HeartOutlined',       '',                    NULL),
(6,  'food',           '膳食管理',    'CoffeeOutlined',      '',                    NULL);

-- 子菜单 (parent_id 指向顶级菜单)
INSERT INTO `menu` (`id`, `menus_index`, `title`, `icon`, `path`, `parent_id`) VALUES
-- 系统管理
(7,  'system-user',    '用户管理',    'UserOutlined',        '/system/user',        2),
(8,  'system-role',    '角色管理',    'SafetyOutlined',      '/system/role',        2),
(9,  'system-menu',    '菜单管理',    'MenuOutlined',        '/system/menu',        2),
-- 客户管理
(10, 'customer-info',  '老人信息',    'IdcardOutlined',      '/customer',           3),
(11, 'customer-out',   '外出登记',    'LogoutOutlined',      '/outward',            3),
(12, 'customer-back',  '归来登记',    'LoginOutlined',       '/backdown',           3),
-- 床位管理
(13, 'bed-room',       '房间管理',    'HomeOutlined',        '/room',               4),
(14, 'bed-info',       '床位信息',    'TableOutlined',       '/bed',                4),
(15, 'bed-details',    '床位分配',    'AppstoreOutlined',    '/beddetails',         4),
-- 护理管理
(16, 'nurse-level',    '护理等级',    'TrophyOutlined',      '/nurselevel',         5),
(17, 'nurse-content',  '护理内容',    'FileTextOutlined',    '/nursecontent',       5),
(18, 'nurse-item',     '护理项目',    'ApartmentOutlined',   '/nurselevelitem',     5),
(19, 'nurse-record',   '护理记录',    'ScheduleOutlined',    '/nurserecord',        5),
-- 膳食管理
(20, 'food-item',      '菜品管理',    'ShopOutlined',        '/food',               6),
(21, 'food-preference','饮食偏好',    'LikeOutlined',        '/customerpreference', 6),
(22, 'food-meal',      '膳食计划',    'CalendarOutlined',    '/meal',               6);

-- ---------- rolemenu (超级管理员拥有所有菜单权限) ----------
INSERT INTO `rolemenu` (`id`, `role_id`, `menu_id`) VALUES
(1,  1, 1),  (2,  1, 2),  (3,  1, 3),  (4,  1, 4),  (5,  1, 5),  (6,  1, 6),
(7,  1, 7),  (8,  1, 8),  (9,  1, 9),  (10, 1, 10), (11, 1, 11), (12, 1, 12),
(13, 1, 13), (14, 1, 14), (15, 1, 15), (16, 1, 16), (17, 1, 17), (18, 1, 18),
(19, 1, 19), (20, 1, 20), (21, 1, 21), (22, 1, 22);

-- 健康管家拥有的菜单权限
INSERT INTO `rolemenu` (`id`, `role_id`, `menu_id`) VALUES
(23, 2, 1),
(24, 2, 3),  (25, 2, 10), (26, 2, 11), (27, 2, 12),
(28, 2, 5),  (29, 2, 16), (30, 2, 17), (31, 2, 18), (32, 2, 19),
(33, 2, 6),  (34, 2, 20), (35, 2, 21), (36, 2, 22);

-- ---------- nurselevel ----------
INSERT INTO `nurselevel` (`id`, `level_name`, `description`, `price`) VALUES
(1, '一级护理', '基础护理，适合生活自理能力较好的老人', 2000.00),
(2, '二级护理', '标准护理，适合需要部分协助的老人', 3500.00),
(3, '三级护理', '高级护理，适合需要全面照护的老人', 5000.00);

-- ---------- nursecontent ----------
INSERT INTO `nursecontent` (`id`, `content_name`, `description`) VALUES
(1, '血压测量', '定期为老人测量血压并记录'),
(2, '血糖监测', '定期为老人监测血糖指标'),
(3, '翻身护理', '帮助长期卧床老人定时翻身，预防压疮'),
(4, '口腔护理', '为老人进行口腔清洁护理'),
(5, '心理疏导', '与老人交流沟通，进行心理疏导'),
(6, '康复训练', '协助老人进行肢体康复训练'),
(7, '用药提醒', '按时提醒并协助老人服药'),
(8, '生活照料', '协助老人日常生活起居，如洗漱、更衣等');

-- ---------- nurselevelitem ----------
-- 一级护理: 血压测量、血糖监测
INSERT INTO `nurselevelitem` (`id`, `level_id`, `nurse_content_id`) VALUES
(1, 1, 1), (2, 1, 2);
-- 二级护理: 血压测量、血糖监测、翻身护理、口腔护理
INSERT INTO `nurselevelitem` (`id`, `level_id`, `nurse_content_id`) VALUES
(3, 2, 1), (4, 2, 2), (5, 2, 3), (6, 2, 4);
-- 三级护理: 所有8项
INSERT INTO `nurselevelitem` (`id`, `level_id`, `nurse_content_id`) VALUES
(7, 3, 1), (8, 3, 2), (9, 3, 3), (10, 3, 4),
(11, 3, 5), (12, 3, 6), (13, 3, 7), (14, 3, 8);

-- ---------- room ----------
INSERT INTO `room` (`id`, `room_no`, `building_no`, `floor`, `room_type`, `bed_count`, `empty_bed`) VALUES
(1, '101', '606栋', '1层', '双人间', 2, 2),
(2, '102', '606栋', '1层', '双人间', 2, 2),
(3, '103', '606栋', '1层', '单人间', 1, 1),
(4, '104', '606栋', '1层', '双人间', 2, 2),
(5, '105', '606栋', '1层', '单人间', 1, 1);

-- ---------- bed ----------
INSERT INTO `bed` (`id`, `bed_no`, `room_no`) VALUES
(1,  '101-1', '101'),
(2,  '101-2', '101'),
(3,  '102-1', '102'),
(4,  '102-2', '102'),
(5,  '103-1', '103'),
(6,  '104-1', '104'),
(7,  '104-2', '104'),
(8,  '105-1', '105');
