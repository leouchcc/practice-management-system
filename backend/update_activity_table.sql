USE practice_management;

-- 为activity表添加证书相关字段
ALTER TABLE `activity`
ADD COLUMN `is_certificate` TINYINT(1) DEFAULT 0 COMMENT '是否颁发证书：0-否 1-是',
ADD COLUMN `certificate_name` VARCHAR(255) COMMENT '证书名称';

-- 为activity表添加其他缺失字段
ALTER TABLE `activity`
ADD COLUMN `cancel_deadline` DATETIME COMMENT '取消截止时间',
ADD COLUMN `is_contact_activity` TINYINT DEFAULT 0 COMMENT '是否联系活动：0-否 1-是';

-- 为activity_registration表添加缺失字段
ALTER TABLE `activity_registration`
ADD COLUMN `proof_file_path` VARCHAR(500) COMMENT '证明文件路径',
ADD COLUMN `proof_file_name` VARCHAR(255) COMMENT '证明文件名称',
ADD COLUMN `proof_submit_time` DATETIME COMMENT '证明提交时间',
ADD COLUMN `proof_verified` TINYINT(1) DEFAULT 0 COMMENT '证明是否验证：0-否 1-是';

-- 为user表添加real_name字段
ALTER TABLE `user`
ADD COLUMN `real_name` VARCHAR(50) COMMENT '真实姓名';

-- 插入测试数据
-- 确保有活动分类
INSERT IGNORE INTO `activity_category` (`id`, `name`, `description`) VALUES
(1, '学术活动', '学术相关的活动'),
(2, '文体活动', '文化体育活动'),
(3, '志愿服务', '志愿者服务活动'),
(4, '社会实践', '社会实践活动');

-- 确保有测试用户
INSERT IGNORE INTO `user` (`id`, `username`, `password`, `role`, `real_name`) VALUES
(7, '刘海鸿', '123456', 'STUDENT', '刘海鸿'),
(1, 'admin', '123456', 'ADMIN', '管理员');

-- 确保有测试活动
INSERT IGNORE INTO `activity` (`id`, `title`, `category_id`, `organizer_id`, `content`, `start_time`, `end_time`, `location`, `max_participants`, `current_participants`, `credit_hours`, `credit_points`, `status`, `is_certificate`, `certificate_name`, `is_contact_activity`) VALUES
(1, '数学竞赛1', 1, 1, '数学竞赛活动', '2026-02-01 10:00:00', '2026-02-01 12:00:00', '教学楼A101', 100, 1, 2.0, 1.0, 'ENDED', 1, '数学竞赛优胜证书', 0);

-- 确保有测试报名记录
INSERT IGNORE INTO `activity_registration` (`id`, `activity_id`, `student_id`, `status`, `check_in_time`, `check_out_time`, `proof_verified`) VALUES
(1, 1, 7, 'COMPLETED', '2026-02-01 10:00:00', '2026-02-01 12:00:00', 1);

-- 确保有测试证书记录
INSERT IGNORE INTO `certificate` (`id`, `certificate_number`, `activity_id`, `student_id`, `certificate_name`, `issue_date`, `create_time`) VALUES
(1, 'CERT-TEST000001', 1, 7, '测试证书', '2026-02-26', NOW());

-- 为activity表添加索引
CREATE INDEX idx_is_certificate ON `activity` (`is_certificate`);
