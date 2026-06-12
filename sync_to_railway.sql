-- ================================================
-- 数据同步脚本：从本地数据库同步到 Railway
-- ================================================

-- 清空现有数据（保留表结构）
DELETE FROM `activity_registration`;
DELETE FROM `activity`;
DELETE FROM `activity_category`;
DELETE FROM `announcement`;
DELETE FROM `activity_suggestion`;
DELETE FROM `system_notification`;
DELETE FROM `credit_record`;
DELETE FROM `sys_user`;

-- 重置自增ID
ALTER TABLE `sys_user` AUTO_INCREMENT = 1;
ALTER TABLE `activity_category` AUTO_INCREMENT = 1;
ALTER TABLE `activity` AUTO_INCREMENT = 1;
ALTER TABLE `activity_registration` AUTO_INCREMENT = 1;
ALTER TABLE `announcement` AUTO_INCREMENT = 1;
ALTER TABLE `activity_suggestion` AUTO_INCREMENT = 1;
ALTER TABLE `system_notification` AUTO_INCREMENT = 1;
ALTER TABLE `credit_record` AUTO_INCREMENT = 1;

-- 插入用户数据
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `student_id`, `phone`, `email`, `role`, `status`, `deleted`, `create_time`, `update_time`) VALUES
(1, 'root', '$2a$10$E1QywinleOjTMic1tm7pEu9.7SCxlJUfCTpZMjbpqTP4xYv1tmL2i', '系统管理员', NULL, '13800138000', 'root@example.com', 'ADMIN', 1, 0, '2026-02-15 02:00:00', '2026-02-15 02:00:00'),
(2, 'teacher1', '$2a$10$E1QywinleOjTMic1tm7pEu9.7SCxlJUfCTpZMjbpqTP4xYv1tmL2i', '张老师', NULL, '13800138001', 'teacher1@example.com', 'TEACHER', 1, 0, '2026-02-15 02:00:00', '2026-02-15 02:00:00'),
(3, 'teacher2', '$2a$10$E1QywinleOjTMic1tm7pEu9.7SCxlJUfCTpZMjbpqTP4xYv1tmL2i', '李老师', NULL, '13800138002', 'teacher2@example.com', 'TEACHER', 1, 0, '2026-02-15 02:00:00', '2026-02-15 02:00:00'),
(4, 'student1', '$2a$10$E1QywinleOjTMic1tm7pEu9.7SCxlJUfCTpZMjbpqTP4xYv1tmL2i', '王小明', '2024001001', '13800138003', 'student1@example.com', 'STUDENT', 1, 0, '2026-02-15 02:00:00', '2026-02-15 02:00:00'),
(5, 'student2', '$2a$10$E1QywinleOjTMic1tm7pEu9.7SCxlJUfCTpZMjbpqTP4xYv1tmL2i', '李小红', '2024001002', '13800138004', 'student2@example.com', 'STUDENT', 1, 0, '2026-02-15 02:00:00', '2026-02-15 02:00:00'),
(6, 'student3', '$2a$10$E1QywinleOjTMic1tm7pEu9.7SCxlJUfCTpZMjbpqTP4xYv1tmL2i', '张小刚', '2024001003', '13800138005', 'student3@example.com', 'STUDENT', 1, 0, '2026-02-15 02:00:00', '2026-02-15 02:00:00');

-- 插入活动分类数据
INSERT INTO `activity_category` (`id`, `name`, `description`, `deleted`, `create_time`, `update_time`) VALUES
(1, '志愿服务', '各类志愿服务活动', 0, '2026-02-15 02:02:04', '2026-02-15 02:02:04'),
(2, '学术讲座', '学术讲座和报告', 0, '2026-02-15 02:02:04', '2026-02-15 02:02:04'),
(3, '文体活动', '文艺体育活动', 0, '2026-02-15 02:02:04', '2026-02-15 02:02:04'),
(4, '社会实践', '社会实践和调研', 0, '2026-02-15 02:02:04', '2026-02-15 02:02:04'),
(5, '其他活动', '其他类型的实践活动', 0, '2026-02-15 02:02:04', '2026-02-15 02:02:04');

-- 插入公告数据
INSERT INTO `announcement` (`id`, `title`, `content`, `publisher_id`, `target_audience`, `priority`, `status`, `publish_time`, `deleted`, `create_time`, `update_time`) VALUES
(1, '欢迎使用实践活动管理系统', '欢迎使用高校学生实践活动管理系统，请各位同学和老师积极使用。', 1, 'ALL', 'HIGH', 'PUBLISHED', '2026-02-15 02:02:04', 0, '2026-02-15 02:02:04', '2026-02-15 02:02:04'),
(2, '系统使用说明', '本系统支持活动发布、报名、学时管理等功能，详细说明请查看帮助文档。', 1, 'ALL', 'NORMAL', 'PUBLISHED', '2026-02-15 02:02:04', 0, '2026-02-15 02:02:04', '2026-02-15 02:02:04');

-- 插入活动数据
INSERT INTO `activity` (`id`, `title`, `category_id`, `organizer_id`, `content`, `start_time`, `end_time`, `location`, `max_participants`, `current_participants`, `credit_hours`, `credit_points`, `status`, `deleted`, `create_time`, `update_time`) VALUES
(1, '社区志愿服务', 1, 2, '走进社区，为老人提供帮助和服务', '2026-02-20 08:00:00', '2026-02-20 17:00:00', '市中心社区', 50, 0, 8.00, 2.00, 'PUBLISHED', 0, '2026-02-15 02:00:00', '2026-02-15 02:00:00'),
(2, '学术讲座：人工智能前沿', 2, 2, '邀请专家讲解人工智能最新发展', '2026-02-22 14:00:00', '2026-02-22 16:00:00', '学术报告厅', 200, 0, 2.00, 1.00, 'PUBLISHED', 0, '2026-02-15 02:00:00', '2026-02-15 02:00:00'),
(3, '校园运动会志愿者', 3, 3, '协助组织校园运动会', '2026-02-25 07:00:00', '2026-02-25 18:00:00', '操场', 100, 0, 10.00, 3.00, 'PUBLISHED', 0, '2026-02-15 02:00:00', '2026-02-15 02:00:00');

-- 插入报名数据
INSERT INTO `activity_registration` (`id`, `activity_id`, `student_id`, `registration_time`, `status`, `check_in_time`, `check_in_location`, `check_out_time`, `actual_hours`, `deleted`, `create_time`, `update_time`, `proof_file_path`, `proof_file_name`, `proof_submit_time`, `proof_verified`) VALUES
(1, 1, 4, '2026-02-16 10:00:00', 'APPROVED', '2026-02-20 08:00:00', '市中心社区', '2026-02-20 17:00:00', 8.00, 0, '2026-02-16 10:00:00', '2026-02-16 10:00:00', NULL, NULL, NULL, 0),
(2, 2, 4, '2026-02-18 14:00:00', 'APPROVED', '2026-02-22 14:00:00', '学术报告厅', '2026-02-22 16:00:00', 2.00, 0, '2026-02-18 14:00:00', '2026-02-18 14:00:00', NULL, NULL, NULL, 0),
(3, 3, 5, '2026-02-20 09:00:00', 'APPROVED', '2026-02-25 07:00:00', '操场', '2026-02-25 18:00:00', 10.00, 0, '2026-02-20 09:00:00', '2026-02-20 09:00:00', NULL, NULL, NULL, 0);