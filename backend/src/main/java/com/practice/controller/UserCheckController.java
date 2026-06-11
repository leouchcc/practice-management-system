package com.practice.controller;

import com.practice.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/check")
public class UserCheckController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/users")
    public Result<List<Map<String, Object>>> checkUsers() {
        try {
            List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT id, username, real_name, role FROM sys_user");
            return Result.success(users);
        } catch (Exception e) {
            return Result.error("查询用户失败: " + e.getMessage());
        }
    }

    @GetMapping("/user-by-username")
    public Result<Map<String, Object>> checkUserByUsername(String username) {
        try {
            String sql = "SELECT id, username, real_name, role FROM sys_user WHERE username = ?";
            List<Map<String, Object>> users = jdbcTemplate.queryForList(sql, username);
            if (users.isEmpty()) {
                return Result.error("用户不存在: " + username);
            }
            return Result.success(users.get(0));
        } catch (Exception e) {
            return Result.error("查询用户失败: " + e.getMessage());
        }
    }
}