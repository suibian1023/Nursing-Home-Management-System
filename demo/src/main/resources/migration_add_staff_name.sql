-- 护理记录表添加 staff_name 列
-- 如果表已存在，请执行此ALTER语句
ALTER TABLE nurserecord ADD COLUMN staff_name VARCHAR(100) DEFAULT NULL COMMENT '护理人员' AFTER nurse_time;
