package com.practice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.practice.common.Result;
import com.practice.dto.ChangePasswordRequest;
import com.practice.entity.User;
import com.practice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<Page<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        Page<User> userPage = userService.getUserList(page, size, keyword);
        return Result.success(userPage);
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    @PutMapping
    public Result<Void> updateUser(@RequestBody User user) {
        try {
            userService.updateUser(user);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/changePassword")
    public Result<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(request.getOldPassword(), request.getNewPassword());
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}