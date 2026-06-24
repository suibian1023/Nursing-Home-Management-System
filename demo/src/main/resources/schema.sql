-- ============================================================================
-- MySQL DDL Script for YYZX (养老中心) System
-- Generated from entity classes in com.neuedu.yyzx.pojo
-- ============================================================================

CREATE DATABASE IF NOT EXISTS yyzx DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE yyzx;

-- 1. role (角色表)
CREATE TABLE role (
    id          INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    role_name   VARCHAR(255) NOT NULL                COMMENT '角色名称',
    description VARCHAR(500) DEFAULT NULL            COMMENT '描述',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色';

-- 2. user (用户表)
CREATE TABLE user (
    id           INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    username     VARCHAR(255) NOT NULL                COMMENT '用户名',
    password     VARCHAR(255) NOT NULL                COMMENT '密码',
    nickname     VARCHAR(255) DEFAULT NULL            COMMENT '昵称',
    email        VARCHAR(255) DEFAULT NULL            COMMENT '邮箱',
    phone_number VARCHAR(20)  DEFAULT NULL            COMMENT '手机号',
    sex          INT          DEFAULT NULL            COMMENT '性别 0=男 1=女',
    role_id      INT          DEFAULT NULL            COMMENT '角色ID',
    avatar       VARCHAR(500) DEFAULT NULL            COMMENT '头像',
    create_by    VARCHAR(255) DEFAULT NULL            COMMENT '创建人',
    create_time  DATETIME     DEFAULT NULL            COMMENT '创建时间',
    is_deleted   INT          NOT NULL DEFAULT 0      COMMENT '逻辑删除标记',
    PRIMARY KEY (id),
    INDEX idx_user_username (username),
    INDEX idx_user_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户';

-- 3. customer (客户表)
CREATE TABLE customer (
    id             INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    customer_name  VARCHAR(255) NOT NULL                COMMENT '客户姓名',
    customer_sex   INT          DEFAULT NULL            COMMENT '客户性别',
    customer_age   INT          DEFAULT NULL            COMMENT '客户年龄',
    id_card        VARCHAR(18)  DEFAULT NULL            COMMENT '身份证号',
    phone          VARCHAR(20)  DEFAULT NULL            COMMENT '电话',
    address        VARCHAR(500) DEFAULT NULL            COMMENT '地址',
    checkin_date   DATETIME     DEFAULT NULL            COMMENT '入住日期',
    room_no        VARCHAR(255) DEFAULT NULL            COMMENT '房间号',
    bed_id         INT          DEFAULT NULL            COMMENT '床位ID',
    nurse_level_id INT          DEFAULT NULL            COMMENT '护理等级ID',
    user_id        INT          DEFAULT NULL            COMMENT '健康管家用户ID',
    is_deleted     INT          NOT NULL DEFAULT 0      COMMENT '逻辑删除标记',
    create_time    DATETIME     DEFAULT NULL            COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_customer_name (customer_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户';

-- 4. room (房间表)
CREATE TABLE room (
    id          INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    room_no     VARCHAR(255) NOT NULL                COMMENT '房间编号',
    building_no VARCHAR(255) DEFAULT NULL            COMMENT '楼号',
    floor       VARCHAR(255) DEFAULT NULL            COMMENT '楼层',
    room_type   VARCHAR(255) DEFAULT NULL            COMMENT '房间类型',
    bed_count   INT          DEFAULT NULL            COMMENT '床位总数',
    empty_bed   INT          DEFAULT NULL            COMMENT '空床数',
    is_deleted  INT          NOT NULL DEFAULT 0      COMMENT '逻辑删除标记',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房间';

-- 5. bed (床位表)
CREATE TABLE bed (
    id         INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    bed_no     VARCHAR(255) NOT NULL                COMMENT '床位编号',
    room_no    VARCHAR(255) DEFAULT NULL            COMMENT '房间编号',
    is_used    INT          DEFAULT 0               COMMENT '是否占用',
    is_deleted INT          NOT NULL DEFAULT 0      COMMENT '逻辑删除标记',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='床位';

-- 6. beddetails (床位详情表)
CREATE TABLE beddetails (
    id            INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    customer_id   INT          DEFAULT NULL            COMMENT '客户ID',
    customer_name VARCHAR(255) DEFAULT NULL            COMMENT '客户姓名',
    customer_sex  INT          DEFAULT NULL            COMMENT '客户性别',
    bed_details   VARCHAR(255) DEFAULT NULL            COMMENT '床位详情',
    room_no       VARCHAR(255) DEFAULT NULL            COMMENT '房间编号',
    start_date    DATETIME     DEFAULT NULL            COMMENT '开始日期',
    end_date      DATETIME     DEFAULT NULL            COMMENT '结束日期',
    is_deleted    INT          NOT NULL DEFAULT 0      COMMENT '逻辑删除标记',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='床位详情';

-- 7. nurselevel (护理等级表)
CREATE TABLE nurselevel (
    id          INT            NOT NULL AUTO_INCREMENT COMMENT '主键',
    level_name  VARCHAR(255)   NOT NULL                COMMENT '等级名称',
    description VARCHAR(500)   DEFAULT NULL            COMMENT '描述',
    price       DECIMAL(10,2)  DEFAULT NULL            COMMENT '价格',
    is_deleted  INT            NOT NULL DEFAULT 0      COMMENT '逻辑删除标记',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护理等级';

-- 8. nursecontent (护理内容表)
CREATE TABLE nursecontent (
    id           INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    content_name VARCHAR(255) NOT NULL                COMMENT '内容名称',
    description  VARCHAR(500) DEFAULT NULL            COMMENT '描述',
    is_deleted   INT          NOT NULL DEFAULT 0      COMMENT '逻辑删除标记',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护理内容';

-- 9. nurselevelitem (护理等级项目关联表)
CREATE TABLE nurselevelitem (
    id               INT  NOT NULL AUTO_INCREMENT COMMENT '主键',
    level_id         INT  DEFAULT NULL            COMMENT '等级ID',
    nurse_content_id INT  DEFAULT NULL            COMMENT '护理内容ID',
    is_deleted       INT  NOT NULL DEFAULT 0      COMMENT '逻辑删除标记',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护理等级项目关联';

-- 10. customernurseitem (客户护理项目表)
CREATE TABLE customernurseitem (
    id             INT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    customer_id    INT      DEFAULT NULL            COMMENT '客户ID',
    nurse_item_id  INT      DEFAULT NULL            COMMENT '护理项目ID',
    nurse_count    INT      DEFAULT NULL            COMMENT '护理次数',
    nurse_date     DATETIME DEFAULT NULL            COMMENT '护理日期',
    is_deleted     INT      NOT NULL DEFAULT 0      COMMENT '逻辑删除标记',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户护理项目';

-- 11. nurserecord (护理记录表)
CREATE TABLE nurserecord (
    id               INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    customer_id      INT          DEFAULT NULL            COMMENT '客户ID',
    nurse_content_id INT          DEFAULT NULL            COMMENT '护理内容ID',
    record_date      DATETIME     DEFAULT NULL            COMMENT '记录日期',
    nurse_time       VARCHAR(255) DEFAULT NULL            COMMENT '护理时间',
    staff_name       VARCHAR(100) DEFAULT NULL            COMMENT '护理人员',
    description      VARCHAR(500) DEFAULT NULL            COMMENT '描述',
    is_deleted       INT          NOT NULL DEFAULT 0      COMMENT '逻辑删除标记',
    create_time      DATETIME     DEFAULT NULL            COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护理记录';

-- 12. outward (外出记录表)
CREATE TABLE outward (
    id               INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    customer_id      INT          DEFAULT NULL            COMMENT '客户ID',
    customer_name    VARCHAR(255) DEFAULT NULL            COMMENT '客户姓名',
    out_date         DATETIME     DEFAULT NULL            COMMENT '外出日期',
    reason           VARCHAR(500) DEFAULT NULL            COMMENT '外出原因',
    expect_back_date DATETIME     DEFAULT NULL            COMMENT '预计返回日期',
    is_deleted       INT          NOT NULL DEFAULT 0      COMMENT '逻辑删除标记',
    create_time      DATETIME     DEFAULT NULL            COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='外出记录';

-- 13. backdown (返院记录表)
CREATE TABLE backdown (
    id            INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    customer_id   INT          DEFAULT NULL            COMMENT '客户ID',
    customer_name VARCHAR(255) DEFAULT NULL            COMMENT '客户姓名',
    back_date     DATETIME     DEFAULT NULL            COMMENT '返回日期',
    is_deleted    INT          NOT NULL DEFAULT 0      COMMENT '逻辑删除标记',
    create_time   DATETIME     DEFAULT NULL            COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='返院记录';

-- 14. food (食物表)
CREATE TABLE food (
    id          INT            NOT NULL AUTO_INCREMENT COMMENT '主键',
    food_name   VARCHAR(255)   NOT NULL                COMMENT '食物名称',
    food_type   VARCHAR(255)   DEFAULT NULL            COMMENT '食物类型',
    price       DECIMAL(10,2)  DEFAULT NULL            COMMENT '价格',
    description VARCHAR(500)   DEFAULT NULL            COMMENT '描述',
    is_deleted  INT            NOT NULL DEFAULT 0      COMMENT '逻辑删除标记',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='食物';

-- 15. customerpreference (客户饮食偏好表)
CREATE TABLE customerpreference (
    id              INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    customer_id     INT          DEFAULT NULL            COMMENT '客户ID',
    food_id         INT          DEFAULT NULL            COMMENT '食物ID',
    preference_type VARCHAR(255) DEFAULT NULL            COMMENT '偏好类型',
    is_deleted      INT          NOT NULL DEFAULT 0      COMMENT '逻辑删除标记',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户饮食偏好';

-- 16. meal (膳食记录表)
CREATE TABLE meal (
    id          INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    customer_id INT          DEFAULT NULL            COMMENT '客户ID',
    meal_date   DATETIME     DEFAULT NULL            COMMENT '膳食日期',
    meal_type   VARCHAR(255) DEFAULT NULL            COMMENT '膳食类型',
    food_ids    VARCHAR(500) DEFAULT NULL            COMMENT '食物ID列表',
    is_deleted  INT          NOT NULL DEFAULT 0      COMMENT '逻辑删除标记',
    create_time DATETIME     DEFAULT NULL            COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='膳食记录';

-- 17. menu (菜单表)
CREATE TABLE menu (
    id          INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    menus_index VARCHAR(255) DEFAULT NULL            COMMENT '一级菜单索引',
    title       VARCHAR(255) DEFAULT NULL            COMMENT '菜单标题',
    icon        VARCHAR(255) DEFAULT NULL            COMMENT '菜单图标',
    path        VARCHAR(255) DEFAULT NULL            COMMENT '菜单路径',
    parent_id   INT          DEFAULT NULL            COMMENT '父级ID',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单';

-- 18. rolemenu (角色菜单关联表)
CREATE TABLE rolemenu (
    id      INT NOT NULL AUTO_INCREMENT COMMENT '主键',
    role_id INT DEFAULT NULL            COMMENT '角色ID',
    menu_id INT DEFAULT NULL            COMMENT '菜单ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_menu (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联';

-- ============================================================================
-- Default Data
-- ============================================================================

-- Insert default admin role
INSERT INTO role (id, role_name, description) VALUES (1, '超级管理员', '系统超级管理员，拥有所有权限');

-- Insert default admin user (password: admin123, MD5 hashed)
-- Note: login service uses plain text comparison (user.getPassword().equals(password))
INSERT INTO user (id, username, password, nickname, role_id, create_time, is_deleted) 
VALUES (1, 'admin', 'admin123', '系统管理员', 1, NOW(), 0);
