package com.practice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.practice.dto.ActivityRequest;
import com.practice.entity.Activity;
import com.practice.mapper.ActivityMapper;
import com.practice.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ActivityService extends ServiceImpl<ActivityMapper, Activity> {

    @Autowired
    private RedisUtil redisUtil;

    private static final String ACTIVITY_LIST_CACHE_KEY = "activity:list";

    public Page<Activity> getActivityList(Integer page, Integer size, String keyword, Long categoryId, String status, Long creatorId) {
        String cacheKey = ACTIVITY_LIST_CACHE_KEY + ":" + page + ":" + size + ":" + keyword + ":" + categoryId + ":" + status + ":" + creatorId;
        
        Page<Activity> cachedData = (Page<Activity>) redisUtil.get(cacheKey);
        if (cachedData != null) {
            return cachedData;
        }

        Page<Activity> pageInfo = new Page<>(page, size);
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like("title", keyword);
        }

        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }

        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }

        if (creatorId != null) {
            queryWrapper.eq("organizer_id", creatorId);
        }

        queryWrapper.orderByDesc("create_time");
        Page<Activity> result = page(pageInfo, queryWrapper);
        
        redisUtil.set(cacheKey, result, 5, TimeUnit.MINUTES);
        
        return result;
    }

    public Activity getActivityById(Long id) {
        return getById(id);
    }

    public void createActivity(ActivityRequest request, Long organizerId) {
        Activity activity = new Activity();
        activity.setTitle(request.getTitle());
        activity.setCategoryId(request.getCategoryId());
        activity.setOrganizerId(organizerId);
        activity.setContent(request.getContent());
        activity.setStartTime(request.getStartTime());
        activity.setEndTime(request.getEndTime());
        activity.setLocation(request.getLocation());
        activity.setMaxParticipants(request.getMaxParticipants());
        activity.setCreditHours(request.getCreditHours());
        activity.setCreditPoints(request.getCreditPoints());
        activity.setCurrentParticipants(0);
        activity.setStatus("DRAFT");
        activity.setIsContactActivity(request.getIsContactActivity() != null ? request.getIsContactActivity() : 0);

        save(activity);
        clearActivityListCache();
    }

    public void updateActivity(ActivityRequest request) {
        Activity activity = getById(request.getId());
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }

        activity.setTitle(request.getTitle());
        activity.setCategoryId(request.getCategoryId());
        activity.setContent(request.getContent());
        activity.setStartTime(request.getStartTime());
        activity.setEndTime(request.getEndTime());
        activity.setLocation(request.getLocation());
        activity.setMaxParticipants(request.getMaxParticipants());
        activity.setCreditHours(request.getCreditHours());
        activity.setCreditPoints(request.getCreditPoints());
        activity.setIsContactActivity(request.getIsContactActivity() != null ? request.getIsContactActivity() : 0);

        updateById(activity);
        clearActivityListCache();
    }

    public void publishActivity(Long id) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        activity.setStatus("PUBLISHED");
        updateById(activity);
        clearActivityListCache();
    }

    public void cancelActivity(Long id) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        activity.setStatus("CANCELLED");
        updateById(activity);
        clearActivityListCache();
    }

    public void deleteActivity(Long id) {
        removeById(id);
        clearActivityListCache();
    }

    private void clearActivityListCache() {
        String pattern = ACTIVITY_LIST_CACHE_KEY + ":*";
        redisUtil.deleteByPattern(pattern);
    }
}
