package com.practice.controller;

import com.practice.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping("/check")
    public Result<Map<String, Object>> healthCheck() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "practice-management-system");
        result.put("timestamp", System.currentTimeMillis());
        return Result.success(result);
    }

    @GetMapping("/env")
    public Result<Map<String, String>> getEnv() {
        Map<String, String> env = new HashMap<>();
        env.put("PORT", System.getenv("PORT"));
        env.put("MYSQLHOST", System.getenv("MYSQLHOST"));
        env.put("MYSQL_PORT", System.getenv("MYSQL_PORT"));
        env.put("MYSQLPORT", System.getenv("MYSQLPORT"));
        env.put("MYSQLDATABASE", System.getenv("MYSQLDATABASE"));
        env.put("MYSQL_DATABASE", System.getenv("MYSQL_DATABASE"));
        env.put("MYSQLUSER", System.getenv("MYSQLUSER"));
        env.put("MYSQL_USER", System.getenv("MYSQL_USER"));
        env.put("MYSQLPASSWORD", System.getenv("MYSQLPASSWORD"));
        env.put("MYSQL_PASSWORD", System.getenv("MYSQL_PASSWORD"));
        env.put("REDISHOST", System.getenv("REDISHOST"));
        env.put("REDIS_HOST", System.getenv("REDIS_HOST"));
        env.put("REDISPORT", System.getenv("REDISPORT"));
        env.put("REDIS_PORT", System.getenv("REDIS_PORT"));
        env.put("REDISPASSWORD", System.getenv("REDISPASSWORD"));
        env.put("REDIS_PASSWORD", System.getenv("REDIS_PASSWORD"));
        return Result.success(env);
    }
}