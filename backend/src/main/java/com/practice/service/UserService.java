package com.practice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.practice.config.JwtUtil;
import com.practice.dto.LoginRequest;
import com.practice.dto.RegisterRequest;
import com.practice.entity.User;
import com.practice.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Map<String, Object> login(LoginRequest request) {
        logger.info("Login attempt for username: {}", request.getUsername());
        
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", request.getUsername());
        User user = getOne(queryWrapper);

        if (user == null) {
            logger.warn("User not found: {}", request.getUsername());
            throw new RuntimeException("用户不存在");
        }

        if (user.getStatus() == 0) {
            logger.warn("User disabled: {}", request.getUsername());
            throw new RuntimeException("账号已被禁用");
        }

        logger.info("User found: {}, checking password...", user.getUsername());
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Invalid password for user: {}", request.getUsername());
            throw new RuntimeException("密码错误");
        }

        logger.info("Password matched, generating token for user: {}", user.getUsername());
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", getUserInfo(user));
        return result;
    }

    public void register(RegisterRequest request) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", request.getUsername());
        if (getOne(queryWrapper) != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setStudentId(request.getStudentId());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole() != null ? request.getRole() : "STUDENT");
        user.setStatus(1);

        save(user);
    }

    public Page<User> getUserList(Integer page, Integer size, String keyword) {
        Page<User> pageInfo = new Page<>(page, size);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like("username", keyword)
                    .or()
                    .like("real_name", keyword)
                    .or()
                    .like("student_id", keyword));
        }

        return page(pageInfo, queryWrapper);
    }

    public void updateUser(User user) {
        updateById(user);
    }

    public void deleteUser(Long id) {
        removeById(id);
    }

    public void changePassword(String oldPassword, String newPassword) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userIdStr = request.getHeader("userId");
        if (userIdStr == null) {
            throw new RuntimeException("未获取到用户信息");
        }

        Long userId = Long.parseLong(userIdStr);
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
    }

    private Map<String, Object> getUserInfo(User user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("studentId", user.getStudentId());
        userInfo.put("phone", user.getPhone());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole());
        return userInfo;
    }
}