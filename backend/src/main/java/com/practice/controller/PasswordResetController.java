package com.practice.controller;

import com.practice.common.Result;
import com.practice.entity.User;
import com.practice.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/password")
@CrossOrigin
public class PasswordResetController {

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/reset-all")
    public Result<Integer> resetAllPasswords(@RequestParam String newPassword) {
        List<User> users = userMapper.selectList(null);
        int count = 0;
        for (User user : users) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userMapper.updateById(user);
            count++;
        }
        return Result.success("成功重置 " + count + " 个用户的密码", count);
    }

    @PostMapping("/reset")
    public Result<Void> resetPassword(@RequestParam String username, @RequestParam String newPassword) {
        User user = userMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>().eq("username", username));
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
        return Result.success("密码重置成功");
    }

    @GetMapping("/list-users")
    public Result<List<User>> listUsers() {
        List<User> users = userMapper.selectList(null);
        return Result.success(users);
    }
}