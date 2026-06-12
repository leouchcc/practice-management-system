package com.practice.controller;

import com.practice.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
public class DataSyncController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/clear")
    public Result<String> clearData() {
        try {
            // 清空现有数据
            jdbcTemplate.execute("DELETE FROM activity_registration");
            jdbcTemplate.execute("DELETE FROM activity");
            jdbcTemplate.execute("DELETE FROM activity_category");
            jdbcTemplate.execute("DELETE FROM announcement");
            jdbcTemplate.execute("DELETE FROM activity_suggestion");
            jdbcTemplate.execute("DELETE FROM system_notification");
            jdbcTemplate.execute("DELETE FROM credit_record");
            jdbcTemplate.execute("DELETE FROM sys_user");

            // 重置自增ID
            jdbcTemplate.execute("ALTER TABLE sys_user AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE activity_category AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE activity AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE activity_registration AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE announcement AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE activity_suggestion AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE system_notification AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE credit_record AUTO_INCREMENT = 1");

            return Result.success("数据库清空成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("清空失败: " + e.getMessage());
        }
    }

    @GetMapping("/export")
    public Result<Map<String, List<Map<String, Object>>>> exportData() {
        try {
            Map<String, List<Map<String, Object>>> data = new HashMap<>();

            // 导出用户数据 - 使用 DATE_FORMAT 将日期转换为字符串
            data.put("users", jdbcTemplate.queryForList(
                "SELECT id, username, password, real_name, student_id, phone, email, role, status, deleted, " +
                "DATE_FORMAT(create_time, '%Y-%m-%d %H:%i:%s') as create_time, " +
                "DATE_FORMAT(update_time, '%Y-%m-%d %H:%i:%s') as update_time FROM sys_user"
            ));

            // 导出分类数据
            data.put("categories", jdbcTemplate.queryForList(
                "SELECT id, name, description, deleted, " +
                "DATE_FORMAT(create_time, '%Y-%m-%d %H:%i:%s') as create_time, " +
                "DATE_FORMAT(update_time, '%Y-%m-%d %H:%i:%s') as update_time FROM activity_category"
            ));

            // 导出活动数据
            data.put("activities", jdbcTemplate.queryForList(
                "SELECT id, title, category_id, organizer_id, content, " +
                "DATE_FORMAT(start_time, '%Y-%m-%d %H:%i:%s') as start_time, " +
                "DATE_FORMAT(end_time, '%Y-%m-%d %H:%i:%s') as end_time, " +
                "DATE_FORMAT(cancel_deadline, '%Y-%m-%d %H:%i:%s') as cancel_deadline, " +
                "location, max_participants, current_participants, credit_hours, credit_points, " +
                "is_contact_activity, issue_certificate, certificate_name, status, deleted, " +
                "DATE_FORMAT(create_time, '%Y-%m-%d %H:%i:%s') as create_time, " +
                "DATE_FORMAT(update_time, '%Y-%m-%d %H:%i:%s') as update_time FROM activity"
            ));

            // 导出报名数据
            data.put("registrations", jdbcTemplate.queryForList(
                "SELECT id, activity_id, student_id, " +
                "DATE_FORMAT(registration_time, '%Y-%m-%d %H:%i:%s') as registration_time, " +
                "status, " +
                "DATE_FORMAT(check_in_time, '%Y-%m-%d %H:%i:%s') as check_in_time, " +
                "check_in_location, " +
                "DATE_FORMAT(check_out_time, '%Y-%m-%d %H:%i:%s') as check_out_time, " +
                "actual_hours, deleted, " +
                "DATE_FORMAT(create_time, '%Y-%m-%d %H:%i:%s') as create_time, " +
                "DATE_FORMAT(update_time, '%Y-%m-%d %H:%i:%s') as update_time, " +
                "proof_file_path, proof_file_name, " +
                "DATE_FORMAT(proof_submit_time, '%Y-%m-%d %H:%i:%s') as proof_submit_time, " +
                "proof_verified FROM activity_registration"
            ));

            // 导出公告数据
            data.put("announcements", jdbcTemplate.queryForList(
                "SELECT id, title, content, publisher_id, target_audience, priority, status, " +
                "DATE_FORMAT(publish_time, '%Y-%m-%d %H:%i:%s') as publish_time, " +
                "deleted, " +
                "DATE_FORMAT(create_time, '%Y-%m-%d %H:%i:%s') as create_time, " +
                "DATE_FORMAT(update_time, '%Y-%m-%d %H:%i:%s') as update_time FROM announcement"
            ));

            // 导入学分记录
            data.put("creditRecords", jdbcTemplate.queryForList(
                "SELECT id, student_id, activity_id, registration_id, credit_hours, credit_points, " +
                "academic_year, semester, deleted, " +
                "DATE_FORMAT(create_time, '%Y-%m-%d %H:%i:%s') as create_time, " +
                "DATE_FORMAT(update_time, '%Y-%m-%d %H:%i:%s') as update_time FROM credit_record"
            ));

            // 导出通知数据
            data.put("notifications", jdbcTemplate.queryForList(
                "SELECT id, user_id, title, content, type, is_read, related_id, related_type, deleted, " +
                "DATE_FORMAT(create_time, '%Y-%m-%d %H:%i:%s') as create_time FROM system_notification"
            ));

            // 导出入校建议数据
            data.put("suggestions", jdbcTemplate.queryForList(
                "SELECT id, activity_id, sender_id, receiver_id, content, parent_id, is_read, deleted, " +
                "DATE_FORMAT(create_time, '%Y-%m-%d %H:%i:%s') as create_time, " +
                "DATE_FORMAT(update_time, '%Y-%m-%d %H:%i:%s') as update_time FROM activity_suggestion"
            ));

            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("数据导出失败: " + e.getMessage());
        }
    }

    @PostMapping("/sync")
    public Result<String> syncData(@RequestBody Map<String, List<Map<String, Object>>> data) {
        try {
            // 清空现有数据
            jdbcTemplate.execute("DELETE FROM activity_registration");
            jdbcTemplate.execute("DELETE FROM activity");
            jdbcTemplate.execute("DELETE FROM activity_category");
            jdbcTemplate.execute("DELETE FROM announcement");
            jdbcTemplate.execute("DELETE FROM activity_suggestion");
            jdbcTemplate.execute("DELETE FROM system_notification");
            jdbcTemplate.execute("DELETE FROM credit_record");
            jdbcTemplate.execute("DELETE FROM sys_user");

            // 重置自增ID
            jdbcTemplate.execute("ALTER TABLE sys_user AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE activity_category AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE activity AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE activity_registration AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE announcement AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE activity_suggestion AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE system_notification AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE credit_record AUTO_INCREMENT = 1");

            // 导入用户数据
            List<Map<String, Object>> users = data.get("users");
            if (users != null) {
                for (Map<String, Object> user : users) {
                    jdbcTemplate.update(
                        "INSERT INTO sys_user (username, password, real_name, role, email, phone, student_id, create_time, update_time, deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        user.get("username"), user.get("password"), user.get("real_name"),
                        user.get("role"), user.get("email"), user.get("phone"),
                        user.get("student_id"), user.get("create_time"), user.get("update_time"),
                        user.get("deleted")
                    );
                }
            }

            // 导入分类数据
            List<Map<String, Object>> categories = data.get("categories");
            if (categories != null) {
                for (Map<String, Object> category : categories) {
                    jdbcTemplate.update(
                        "INSERT INTO activity_category (name, description, create_time, update_time, deleted) VALUES (?, ?, ?, ?, ?)",
                        category.get("name"), category.get("description"),
                        category.get("create_time"), category.get("update_time"), category.get("deleted")
                    );
                }
            }

            // 导入活动数据
            List<Map<String, Object>> activities = data.get("activities");
            if (activities != null) {
                for (Map<String, Object> activity : activities) {
                    jdbcTemplate.update(
                        "INSERT INTO activity (title, category_id, organizer_id, content, start_time, end_time, cancel_deadline, location, max_participants, current_participants, credit_hours, credit_points, is_contact_activity, issue_certificate, certificate_name, status, deleted, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        activity.get("title"), activity.get("category_id"), activity.get("organizer_id"),
                        activity.get("content"), activity.get("start_time"), activity.get("end_time"),
                        activity.get("cancel_deadline"), activity.get("location"),
                        activity.get("max_participants"), activity.get("current_participants"),
                        activity.get("credit_hours"), activity.get("credit_points"),
                        activity.get("is_contact_activity"), activity.get("issue_certificate"),
                        activity.get("certificate_name"), activity.get("status"),
                        activity.get("deleted"), activity.get("create_time"), activity.get("update_time")
                    );
                }
            }

            // 导入报名数据
            List<Map<String, Object>> registrations = data.get("registrations");
            if (registrations != null) {
                for (Map<String, Object> registration : registrations) {
                    jdbcTemplate.update(
                        "INSERT INTO activity_registration (activity_id, student_id, registration_time, status, check_in_time, check_in_location, check_out_time, actual_hours, deleted, create_time, update_time, proof_file_path, proof_file_name, proof_submit_time, proof_verified) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        registration.get("activity_id"), registration.get("student_id"), registration.get("registration_time"),
                        registration.get("status"), registration.get("check_in_time"), registration.get("check_in_location"),
                        registration.get("check_out_time"), registration.get("actual_hours"),
                        registration.get("deleted"), registration.get("create_time"), registration.get("update_time"),
                        registration.get("proof_file_path"), registration.get("proof_file_name"),
                        registration.get("proof_submit_time"), registration.get("proof_verified")
                    );
                }
            }

            // 导入公告数据
            List<Map<String, Object>> announcements = data.get("announcements");
            if (announcements != null) {
                for (Map<String, Object> announcement : announcements) {
                    jdbcTemplate.update(
                        "INSERT INTO announcement (title, content, create_time, update_time, deleted) VALUES (?, ?, ?, ?, ?)",
                        announcement.get("title"), announcement.get("content"),
                        announcement.get("create_time"), announcement.get("update_time"), announcement.get("deleted")
                    );
                }
            }

            // 导入学分记录
            List<Map<String, Object>> creditRecords = data.get("creditRecords");
            if (creditRecords != null) {
                for (Map<String, Object> record : creditRecords) {
                    jdbcTemplate.update(
                        "INSERT INTO credit_record (user_id, activity_id, hours, points, year, semester, create_time, update_time, deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        record.get("user_id"), record.get("activity_id"), record.get("hours"),
                        record.get("points"), record.get("year"), record.get("semester"),
                        record.get("create_time"), record.get("update_time"), record.get("deleted")
                    );
                }
            }

            // 导入通知数据
            List<Map<String, Object>> notifications = data.get("notifications");
            if (notifications != null) {
                for (Map<String, Object> notification : notifications) {
                    jdbcTemplate.update(
                        "INSERT INTO system_notification (user_id, title, content, type, is_read, related_id, related_type, deleted, create_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        notification.get("user_id"), notification.get("title"), notification.get("content"),
                        notification.get("type"), notification.get("is_read"), notification.get("related_id"),
                        notification.get("related_type"), notification.get("deleted"), notification.get("create_time")
                    );
                }
            }

            // 导出入校建议数据
            List<Map<String, Object>> suggestions = data.get("suggestions");
            if (suggestions != null) {
                for (Map<String, Object> suggestion : suggestions) {
                    jdbcTemplate.update(
                        "INSERT INTO activity_suggestion (activity_id, sender_id, receiver_id, content, parent_id, is_read, deleted, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        suggestion.get("activity_id"), suggestion.get("sender_id"), suggestion.get("receiver_id"),
                        suggestion.get("content"), suggestion.get("parent_id"), suggestion.get("is_read"),
                        suggestion.get("deleted"), suggestion.get("create_time"), suggestion.get("update_time")
                    );
                }
            }

            return Result.success("数据同步成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("数据同步失败: " + e.getMessage());
        }
    }

    @PostMapping("/sync-table")
    public Result<String> syncTable(@RequestBody Map<String, List<Map<String, Object>>> data) {
        try {
            String tableName = data.keySet().iterator().next();
            List<Map<String, Object>> records = data.get(tableName);
            
            if (records == null || records.isEmpty()) {
                return Result.success("表 " + tableName + " 没有数据需要同步");
            }

            // 根据表名执行不同的插入逻辑
            switch (tableName) {
                case "users":
                    for (Map<String, Object> user : records) {
                        jdbcTemplate.update(
                            "INSERT INTO sys_user (username, password, real_name, role, email, phone, student_id, create_time, update_time, deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            user.get("username"), user.get("password"), user.get("real_name"),
                            user.get("role"), user.get("email"), user.get("phone"),
                            user.get("student_id"), user.get("create_time"), user.get("update_time"),
                            user.get("deleted")
                        );
                    }
                    break;
                case "categories":
                    for (Map<String, Object> category : records) {
                        jdbcTemplate.update(
                            "INSERT INTO activity_category (name, description, create_time, update_time, deleted) VALUES (?, ?, ?, ?, ?)",
                            category.get("name"), category.get("description"),
                            category.get("create_time"), category.get("update_time"), category.get("deleted")
                        );
                    }
                    break;
                case "activities":
                    for (Map<String, Object> activity : records) {
                        jdbcTemplate.update(
                            "INSERT INTO activity (title, category_id, organizer_id, content, start_time, end_time, cancel_deadline, location, max_participants, current_participants, credit_hours, credit_points, is_contact_activity, issue_certificate, certificate_name, status, deleted, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            activity.get("title"), activity.get("category_id"), activity.get("organizer_id"),
                            activity.get("content"), activity.get("start_time"), activity.get("end_time"),
                            activity.get("cancel_deadline"), activity.get("location"),
                            activity.get("max_participants"), activity.get("current_participants"),
                            activity.get("credit_hours"), activity.get("credit_points"),
                            activity.get("is_contact_activity"), activity.get("issue_certificate"),
                            activity.get("certificate_name"), activity.get("status"),
                            activity.get("deleted"), activity.get("create_time"), activity.get("update_time")
                        );
                    }
                    break;
                case "registrations":
                    for (Map<String, Object> registration : records) {
                        jdbcTemplate.update(
                            "INSERT INTO activity_registration (activity_id, student_id, registration_time, status, check_in_time, check_in_location, check_out_time, actual_hours, deleted, create_time, update_time, proof_file_path, proof_file_name, proof_submit_time, proof_verified) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            registration.get("activity_id"), registration.get("student_id"), registration.get("registration_time"),
                            registration.get("status"), registration.get("check_in_time"), registration.get("check_in_location"),
                            registration.get("check_out_time"), registration.get("actual_hours"),
                            registration.get("deleted"), registration.get("create_time"), registration.get("update_time"),
                            registration.get("proof_file_path"), registration.get("proof_file_name"),
                            registration.get("proof_submit_time"), registration.get("proof_verified")
                        );
                    }
                    break;
                case "announcements":
                    for (Map<String, Object> announcement : records) {
                        jdbcTemplate.update(
                            "INSERT INTO announcement (title, content, create_time, update_time, deleted) VALUES (?, ?, ?, ?, ?)",
                            announcement.get("title"), announcement.get("content"),
                            announcement.get("create_time"), announcement.get("update_time"), announcement.get("deleted")
                        );
                    }
                    break;
                case "creditRecords":
                    for (Map<String, Object> record : records) {
                        jdbcTemplate.update(
                            "INSERT INTO credit_record (user_id, activity_id, hours, points, year, semester, create_time, update_time, deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            record.get("user_id"), record.get("activity_id"), record.get("hours"),
                            record.get("points"), record.get("year"), record.get("semester"),
                            record.get("create_time"), record.get("update_time"), record.get("deleted")
                        );
                    }
                    break;
                case "notifications":
                    for (Map<String, Object> notification : records) {
                        jdbcTemplate.update(
                            "INSERT INTO system_notification (user_id, title, content, type, is_read, related_id, related_type, deleted, create_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            notification.get("user_id"), notification.get("title"), notification.get("content"),
                            notification.get("type"), notification.get("is_read"), notification.get("related_id"),
                            notification.get("related_type"), notification.get("deleted"), notification.get("create_time")
                        );
                    }
                    break;
                case "suggestions":
                    for (Map<String, Object> suggestion : records) {
                        jdbcTemplate.update(
                            "INSERT INTO activity_suggestion (activity_id, sender_id, receiver_id, content, parent_id, is_read, deleted, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            suggestion.get("activity_id"), suggestion.get("sender_id"), suggestion.get("receiver_id"),
                            suggestion.get("content"), suggestion.get("parent_id"), suggestion.get("is_read"),
                            suggestion.get("deleted"), suggestion.get("create_time"), suggestion.get("update_time")
                        );
                    }
                    break;
                default:
                    return Result.error("未知的表名: " + tableName);
            }

            return Result.success("表 " + tableName + " 同步成功，共 " + records.size() + " 条记录");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("同步失败: " + e.getMessage());
        }
    }
}