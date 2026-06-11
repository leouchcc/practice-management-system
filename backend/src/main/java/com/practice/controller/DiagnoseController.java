package com.practice.controller;

import com.practice.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/diagnose")
public class DiagnoseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db-test")
    public Result<String> testDatabase() {
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT 1 as test");
            if (!result.isEmpty()) {
                return Result.success("数据库连接成功");
            }
            return Result.error("数据库查询结果为空");
        } catch (Exception e) {
            return Result.error("数据库连接失败: " + e.getMessage());
        }
    }

    @GetMapping("/users-count")
    public Result<Integer> getUsersCount() {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sys_user", Integer.class);
            return Result.success("用户数量: " + count, count);
        } catch (Exception e) {
            return Result.error("查询用户数量失败: " + e.getMessage());
        }
    }

    @GetMapping("/env")
    public Result<Map<String, String>> getEnv() {
        Map<String, String> env = Map.of(
            "JDBC_DATABASE_URL", System.getenv("JDBC_DATABASE_URL"),
            "MYSQLUSER", System.getenv("MYSQLUSER"),
            "MYSQLPASSWORD", System.getenv("MYSQLPASSWORD"),
            "REDIS_URL", System.getenv("REDIS_URL")
        );
        return Result.success(env);
    }
}