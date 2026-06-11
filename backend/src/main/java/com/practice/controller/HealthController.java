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
        env.put("JDBC_DATABASE_URL", System.getenv("JDBC_DATABASE_URL"));
        env.put("MYSQLUSER", System.getenv("MYSQLUSER"));
        env.put("MYSQLPASSWORD", System.getenv("MYSQLPASSWORD"));
        env.put("REDIS_URL", System.getenv("REDIS_URL"));
        return Result.success(env);
    }
}