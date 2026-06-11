package com.practice.controller;

import com.practice.common.Result;
import com.practice.dto.LoginRequest;
import com.practice.dto.RegisterRequest;
import com.practice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            System.out.println("Received login request - username: [" + request.getUsername() + "], password length: " + (request.getPassword() != null ? request.getPassword().length() : 0));
            Map<String, Object> result = userService.login(request);
            return Result.success(result);
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/login-test")
    public Result<Map<String, Object>> loginTest(@RequestParam String username, @RequestParam String password) {
        try {
            System.out.println("GET login test - username: [" + username + "]");
            LoginRequest request = new LoginRequest();
            request.setUsername(username);
            request.setPassword(password);
            Map<String, Object> result = userService.login(request);
            return Result.success(result);
        } catch (Exception e) {
            System.out.println("Login test error: " + e.getMessage());
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterRequest request) {
        try {
            userService.register(request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
