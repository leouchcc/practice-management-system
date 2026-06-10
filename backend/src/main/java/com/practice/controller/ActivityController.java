package com.practice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.practice.common.Result;
import com.practice.dto.ActivityRequest;
import com.practice.entity.Activity;
import com.practice.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity")
@CrossOrigin
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping("/list")
    public Result<Page<Activity>> getActivityList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long creatorId) {
        Page<Activity> activityPage = activityService.getActivityList(page, size, keyword, categoryId, status, creatorId);
        return Result.success(activityPage);
    }

    @GetMapping("/{id}")
    public Result<Activity> getActivityById(@PathVariable Long id) {
        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        return Result.success(activity);
    }

    @PostMapping
    public Result<Void> createActivity(@RequestBody ActivityRequest request, @RequestHeader("userId") Long userId) {
        try {
            activityService.createActivity(request, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping
    public Result<Void> updateActivity(@RequestBody ActivityRequest request) {
        try {
            activityService.updateActivity(request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/publish")
    public Result<Void> publishActivity(@PathVariable Long id) {
        try {
            activityService.publishActivity(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancelActivity(@PathVariable Long id) {
        try {
            activityService.cancelActivity(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteActivity(@PathVariable Long id) {
        try {
            activityService.deleteActivity(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
