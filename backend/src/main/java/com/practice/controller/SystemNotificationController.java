package com.practice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.practice.common.Result;
import com.practice.entity.SystemNotification;
import com.practice.service.SystemNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@CrossOrigin
public class SystemNotificationController {

    @Autowired
    private SystemNotificationService notificationService;

    @GetMapping("/list")
    public Result<Page<SystemNotification>> getNotificationList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestHeader("userId") Long userId) {
        Page<SystemNotification> notificationPage = notificationService.getNotificationList(page, size, userId);
        return Result.success(notificationPage);
    }

    @PostMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        try {
            notificationService.markAsRead(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/read-all")
    public Result<Void> markAllAsRead(@RequestHeader("userId") Long userId) {
        try {
            notificationService.markAllAsRead(userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(@RequestHeader("userId") Long userId) {
        Long count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }
}
