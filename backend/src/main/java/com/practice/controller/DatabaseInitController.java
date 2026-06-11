package com.practice.controller;

import com.practice.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/init")
public class DatabaseInitController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/database")
    public Result<String> initDatabase() {
        try {
            String dropTablesSql = """
                DROP TABLE IF EXISTS activity_suggestion;
                DROP TABLE IF EXISTS contact_relation;
                DROP TABLE IF EXISTS system_notification;
                DROP TABLE IF EXISTS announcement;
                DROP TABLE IF EXISTS certificate;
                DROP TABLE IF EXISTS credit_record;
                DROP TABLE IF EXISTS activity_registration;
                DROP TABLE IF EXISTS activity;
                DROP TABLE IF EXISTS activity_category;
                DROP TABLE IF EXISTS sys_user;
            """;
            
            for (String sql : dropTablesSql.split(";")) {
                sql = sql.trim();
                if (!sql.isEmpty()) {
                    jdbcTemplate.execute(sql);
                }
            }

            String createUserTable = """
                CREATE TABLE `sys_user` (
                    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
                    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
                    `password` VARCHAR(100) NOT NULL COMMENT '密码',
                    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
                    `student_id` VARCHAR(20) COMMENT '学号',
                    `phone` VARCHAR(20) COMMENT '手机号',
                    `email` VARCHAR(100) COMMENT '邮箱',
                    `role` VARCHAR(20) NOT NULL COMMENT '角色：STUDENT/TEACHER/ADMIN',
                    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
                    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否 1-是',
                    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    INDEX idx_username (`username`),
                    INDEX idx_student_id (`student_id`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表'
            """;
            jdbcTemplate.execute(createUserTable);

            String createCategoryTable = """
                CREATE TABLE `activity_category` (
                    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
                    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
                    `description` VARCHAR(200) COMMENT '分类描述',
                    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否 1-是',
                    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动分类表'
            """;
            jdbcTemplate.execute(createCategoryTable);

            String createActivityTable = """
                CREATE TABLE `activity` (
                    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '活动ID',
                    `title` VARCHAR(200) NOT NULL COMMENT '活动标题',
                    `category_id` BIGINT NOT NULL COMMENT '分类ID',
                    `organizer_id` BIGINT NOT NULL COMMENT '组织者ID',
                    `content` TEXT COMMENT '活动内容',
                    `start_time` DATETIME NOT NULL COMMENT '开始时间',
                    `end_time` DATETIME NOT NULL COMMENT '结束时间',
                    `location` VARCHAR(200) COMMENT '活动地点',
                    `max_participants` INT COMMENT '最大参与人数',
                    `current_participants` INT DEFAULT 0 COMMENT '当前参与人数',
                    `credit_hours` DECIMAL(5,2) DEFAULT 0 COMMENT '学时',
                    `credit_points` DECIMAL(5,2) DEFAULT 0 COMMENT '学分',
                    `status` VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿 PUBLISHED-已发布 STARTED-进行中 ENDED-已结束 CANCELLED-已取消',
                    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否 1-是',
                    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    INDEX idx_category (`category_id`),
                    INDEX idx_organizer (`organizer_id`),
                    INDEX idx_status (`status`),
                    INDEX idx_start_time (`start_time`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表'
            """;
            jdbcTemplate.execute(createActivityTable);

            String createRegistrationTable = """
                CREATE TABLE `activity_registration` (
                    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '报名ID',
                    `activity_id` BIGINT NOT NULL COMMENT '活动ID',
                    `student_id` BIGINT NOT NULL COMMENT '学生ID',
                    `registration_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
                    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核 APPROVED-已通过 REJECTED-已拒绝 CANCELLED-已取消 COMPLETED-已完成',
                    `check_in_time` DATETIME COMMENT '签到时间',
                    `check_in_location` VARCHAR(200) COMMENT '签到位置',
                    `proof_file_path` VARCHAR(255) COMMENT '证明文件路径',
                    `proof_file_name` VARCHAR(255) COMMENT '证明文件名称',
                    `proof_submit_time` DATETIME COMMENT '提交证明时间',
                    `check_out_time` DATETIME COMMENT '签退时间',
                    `actual_hours` DECIMAL(5,2) COMMENT '实际参与学时',
                    `evaluation_status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '点评状态：PENDING-待点评 COMPLETED-已点评',
                    `rating` VARCHAR(20) COMMENT '点评等级：EXCELLENT-优秀 GOOD-良好 PASS-及格',
                    `final_credit_points` DECIMAL(5,2) COMMENT '最终获得的学分',
                    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否 1-是',
                    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    UNIQUE KEY uk_activity_student (`activity_id`, `student_id`),
                    INDEX idx_student (`student_id`),
                    INDEX idx_status (`status`),
                    INDEX idx_evaluation_status (`evaluation_status`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动报名表'
            """;
            jdbcTemplate.execute(createRegistrationTable);

            String createCreditRecordTable = """
                CREATE TABLE `credit_record` (
                    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
                    `student_id` BIGINT NOT NULL COMMENT '学生ID',
                    `activity_id` BIGINT NOT NULL COMMENT '活动ID',
                    `registration_id` BIGINT NOT NULL COMMENT '报名ID',
                    `credit_hours` DECIMAL(5,2) DEFAULT 0 COMMENT '学时',
                    `credit_points` DECIMAL(5,2) DEFAULT 0 COMMENT '学分',
                    `academic_year` VARCHAR(20) COMMENT '学年',
                    `semester` VARCHAR(20) COMMENT '学期',
                    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否 1-是',
                    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    INDEX idx_student (`student_id`),
                    INDEX idx_academic_year (`academic_year`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学分记录表'
            """;
            jdbcTemplate.execute(createCreditRecordTable);

            String createCertificateTable = """
                CREATE TABLE `certificate` (
                    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '证书ID',
                    `student_id` BIGINT NOT NULL COMMENT '学生ID',
                    `activity_id` BIGINT NOT NULL COMMENT '活动ID',
                    `certificate_no` VARCHAR(50) UNIQUE COMMENT '证书编号',
                    `issue_date` DATE COMMENT '颁发日期',
                    `file_path` VARCHAR(255) COMMENT '证书文件路径',
                    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否 1-是',
                    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    INDEX idx_student (`student_id`),
                    INDEX idx_certificate_no (`certificate_no`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='证书表'
            """;
            jdbcTemplate.execute(createCertificateTable);

            String createAnnouncementTable = """
                CREATE TABLE `announcement` (
                    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '公告ID',
                    `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
                    `content` TEXT NOT NULL COMMENT '公告内容',
                    `publisher_id` BIGINT NOT NULL COMMENT '发布人ID',
                    `target_audience` VARCHAR(20) DEFAULT 'ALL' COMMENT '目标受众：ALL-全部 STUDENT-学生 TEACHER-教师',
                    `priority` VARCHAR(20) DEFAULT 'NORMAL' COMMENT '优先级：LOW-NORMAL-HIGH',
                    `status` VARCHAR(20) DEFAULT 'PUBLISHED' COMMENT '状态：DRAFT-草稿 PUBLISHED-已发布 ARCHIVED-已归档',
                    `publish_time` DATETIME COMMENT '发布时间',
                    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否 1-是',
                    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    INDEX idx_status (`status`),
                    INDEX idx_publish_time (`publish_time`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表'
            """;
            jdbcTemplate.execute(createAnnouncementTable);

            String createNotificationTable = """
                CREATE TABLE `system_notification` (
                    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '通知ID',
                    `user_id` BIGINT NOT NULL COMMENT '用户ID',
                    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
                    `content` TEXT COMMENT '通知内容',
                    `type` VARCHAR(20) DEFAULT 'INFO' COMMENT '类型：INFO-WARNING-SUCCESS-ERROR',
                    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读：0-否 1-是',
                    `related_id` BIGINT COMMENT '关联ID',
                    `related_type` VARCHAR(50) COMMENT '关联类型',
                    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否 1-是',
                    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    INDEX idx_user (`user_id`),
                    INDEX idx_is_read (`is_read`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表'
            """;
            jdbcTemplate.execute(createNotificationTable);

            String createContactRelationTable = """
                CREATE TABLE `contact_relation` (
                    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关系ID',
                    `requester_id` BIGINT NOT NULL COMMENT '请求者ID',
                    `addressee_id` BIGINT NOT NULL COMMENT '被添加者ID',
                    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待确认 ACCEPTED-已接受 REJECTED-已拒绝',
                    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    UNIQUE KEY uk_requester_addressee (`requester_id`, `addressee_id`),
                    INDEX idx_requester (`requester_id`),
                    INDEX idx_addressee (`addressee_id`),
                    INDEX idx_status (`status`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系人关系表'
            """;
            jdbcTemplate.execute(createContactRelationTable);

            String createSuggestionTable = """
                CREATE TABLE `activity_suggestion` (
                    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '建议ID',
                    `activity_id` BIGINT COMMENT '活动ID',
                    `sender_id` BIGINT NOT NULL COMMENT '发送者ID',
                    `receiver_id` BIGINT NOT NULL COMMENT '接收者ID',
                    `content` TEXT NOT NULL COMMENT '建议内容',
                    `parent_id` BIGINT COMMENT '父建议ID，用于回复',
                    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读：0-否 1-是',
                    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否 1-是',
                    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    INDEX idx_activity (`activity_id`),
                    INDEX idx_sender (`sender_id`),
                    INDEX idx_receiver (`receiver_id`),
                    INDEX idx_parent (`parent_id`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动建议表'
            """;
            jdbcTemplate.execute(createSuggestionTable);

            String encodedPassword = passwordEncoder.encode("123520");
            
            String insertCategories = """
                INSERT INTO `activity_category` (`name`, `description`) VALUES
                ('志愿服务', '各类志愿服务活动'),
                ('社会实践', '社会实践活动'),
                ('创新创业', '创新创业相关活动'),
                ('学术讲座', '学术讲座和报告'),
                ('文体活动', '文艺体育活动')
            """;
            jdbcTemplate.execute(insertCategories);

            String insertUsers = String.format("""
                INSERT INTO `sys_user` (`username`, `password`, `real_name`, `student_id`, `phone`, `email`, `role`, `status`) VALUES
                ('root', '%s', '系统管理员', NULL, '13800138000', 'root@example.com', 'ADMIN', 1),
                ('teacherliu', '%s', '刘海鸿', NULL, '13800138001', 'teacherliu@example.com', 'TEACHER', 1),
                ('刘海鸿', '%s', '刘海鸿', '2024001', '13800138002', 'student@example.com', 'STUDENT', 1)
            """, encodedPassword, encodedPassword, encodedPassword);
            jdbcTemplate.execute(insertUsers);

            String insertAnnouncements = """
                INSERT INTO `announcement` (`title`, `content`, `publisher_id`, `target_audience`, `priority`, `status`, `publish_time`) VALUES
                ('欢迎使用实践活动管理系统', '欢迎使用高校学生实践活动管理系统，请各位同学和老师积极使用。', 1, 'ALL', 'HIGH', 'PUBLISHED', NOW()),
                ('系统使用说明', '本系统支持活动发布、报名、学时管理等功能，详细说明请查看帮助文档。', 1, 'ALL', 'NORMAL', 'PUBLISHED', NOW())
            """;
            jdbcTemplate.execute(insertAnnouncements);

            return Result.success("数据库初始化成功");
        } catch (Exception e) {
            return Result.error("数据库初始化失败: " + e.getMessage());
        }
    }

    @GetMapping("/check-tables")
    public Result<List<Map<String, Object>>> checkTables() {
        try {
            List<Map<String, Object>> tables = jdbcTemplate.queryForList("SHOW TABLES");
            return Result.success(tables);
        } catch (Exception e) {
            return Result.error("查询表失败: " + e.getMessage());
        }
    }
}