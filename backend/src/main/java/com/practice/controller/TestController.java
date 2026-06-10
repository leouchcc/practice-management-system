package com.practice.controller;

import com.practice.common.Result;
import com.practice.service.ActivityRegistrationService;
import com.practice.service.ActivityService;
import com.practice.entity.Activity;
import com.practice.entity.ActivityRegistration;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private ActivityRegistrationService activityRegistrationService;

    @Autowired
    private ActivityService activityService;

    @GetMapping("/create-activity")
    public Result<String> createActivity() {
        // 创建数学竞赛1活动
        Activity activity = new Activity();
        activity.setTitle("数学竞赛1");
        activity.setCategoryId(1L);
        activity.setOrganizerId(1L);
        activity.setContent("数学竞赛活动");
        activity.setStartTime(LocalDateTime.now().minusDays(1));
        activity.setEndTime(LocalDateTime.now().minusHours(2));
        activity.setLocation("教学楼A101");
        activity.setMaxParticipants(100);
        activity.setCurrentParticipants(1);
        activity.setCreditHours(java.math.BigDecimal.valueOf(4));
        activity.setCreditPoints(java.math.BigDecimal.valueOf(4));
        activity.setStatus("ENDED");
        activity.setIsContactActivity(0);
        
        activityService.save(activity);
        return Result.success("活动创建成功，ID: " + activity.getId());
    }

    @GetMapping("/list-registrations")
    public Result<List<ActivityRegistration>> listRegistrations() {
        QueryWrapper<ActivityRegistration> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "COMPLETED");
        List<ActivityRegistration> registrations = activityRegistrationService.list(queryWrapper);
        return Result.success(registrations);
    }

    @GetMapping("/list-activities")
    public Result<List<Activity>> listActivities() {
        List<Activity> activities = activityService.list();
        return Result.success(activities);
    }
}