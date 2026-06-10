package com.practice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.practice.entity.SystemNotification;
import com.practice.mapper.SystemNotificationMapper;
import com.practice.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemNotificationService extends ServiceImpl<SystemNotificationMapper, SystemNotification> {

    @Autowired
    private RedisUtil redisUtil;

    private static final String UNREAD_COUNT_CACHE_KEY = "notification:unread:";

    public void createNotification(Long userId, String title, String content, String type, Long relatedId, String relatedType) {
        SystemNotification notification = new SystemNotification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setIsRead(0);
        notification.setRelatedId(relatedId);
        notification.setRelatedType(relatedType);

        save(notification);
        incrementUnreadCount(userId);
    }

    public Page<SystemNotification> getNotificationList(Integer page, Integer size, Long userId) {
        Page<SystemNotification> pageInfo = new Page<>(page, size);
        QueryWrapper<SystemNotification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time");
        return page(pageInfo, queryWrapper);
    }

    public void markAsRead(Long notificationId) {
        SystemNotification notification = getById(notificationId);
        if (notification != null && notification.getIsRead() == 0) {
            notification.setIsRead(1);
            updateById(notification);
            decrementUnreadCount(notification.getUserId());
        }
    }

    public void markAllAsRead(Long userId) {
        QueryWrapper<SystemNotification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("is_read", 0);
        List<SystemNotification> notifications = list(queryWrapper);
        notifications.forEach(n -> n.setIsRead(1));
        updateBatchById(notifications);
        clearUnreadCountCache(userId);
    }

    public Long getUnreadCount(Long userId) {
        String cacheKey = UNREAD_COUNT_CACHE_KEY + userId;
        Object cachedCount = redisUtil.get(cacheKey);
        if (cachedCount != null) {
            return Long.parseLong(cachedCount.toString());
        }

        QueryWrapper<SystemNotification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("is_read", 0);
        Long count = count(queryWrapper);
        
        redisUtil.set(cacheKey, count, 5, java.util.concurrent.TimeUnit.MINUTES);
        
        return count;
    }

    private void incrementUnreadCount(Long userId) {
        String cacheKey = UNREAD_COUNT_CACHE_KEY + userId;
        redisUtil.increment(cacheKey, 1);
        redisUtil.expire(cacheKey, 5, java.util.concurrent.TimeUnit.MINUTES);
    }

    private void decrementUnreadCount(Long userId) {
        String cacheKey = UNREAD_COUNT_CACHE_KEY + userId;
        redisUtil.decrement(cacheKey, 1);
    }

    private void clearUnreadCountCache(Long userId) {
        String cacheKey = UNREAD_COUNT_CACHE_KEY + userId;
        redisUtil.delete(cacheKey);
    }
}
