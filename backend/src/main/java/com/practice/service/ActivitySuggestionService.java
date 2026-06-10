package com.practice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.practice.dto.SuggestionDTO;
import com.practice.entity.ActivitySuggestion;
import com.practice.mapper.ActivitySuggestionMapper;
import com.practice.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivitySuggestionService extends ServiceImpl<ActivitySuggestionMapper, ActivitySuggestion> {

    @Autowired
    private SystemNotificationService notificationService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    private static final String UNREAD_COUNT_CACHE_KEY = "suggestion:unread:";

    public void createSuggestion(Long activityId, Long senderId, Long receiverId, String content) {
        ActivitySuggestion suggestion = new ActivitySuggestion();
        suggestion.setActivityId(activityId);
        suggestion.setSenderId(senderId);
        suggestion.setReceiverId(receiverId);
        suggestion.setContent(content);
        suggestion.setParentId(null);
        suggestion.setIsRead(0);

        save(suggestion);
        incrementUnreadCount(receiverId);

        String title = "活动修改建议";
        String notificationContent = "您收到了新的活动修改建议，请查看。";
        notificationService.createNotification(
                receiverId,
                title,
                notificationContent,
                "INFO",
                suggestion.getId(),
                "ACTIVITY_SUGGESTION"
        );
    }

    public void replySuggestion(Long parentId, Long senderId, Long receiverId, String content) {
        ActivitySuggestion reply = new ActivitySuggestion();
        reply.setActivityId(null);
        reply.setSenderId(senderId);
        reply.setReceiverId(receiverId);
        reply.setContent(content);
        reply.setParentId(parentId);
        reply.setIsRead(0);

        save(reply);
        incrementUnreadCount(receiverId);

        String title = "活动建议回复";
        String notificationContent = "您收到了活动建议的回复，请查看。";
        notificationService.createNotification(
                receiverId,
                title,
                notificationContent,
                "INFO",
                reply.getId(),
                "SUGGESTION_REPLY"
        );
    }

    public Page<SuggestionDTO> getSuggestionList(Integer page, Integer size, Long activityId, Long userId) {
        Page<ActivitySuggestion> pageInfo = new Page<>(page, size);
        QueryWrapper<ActivitySuggestion> queryWrapper = new QueryWrapper<>();

        if (activityId != null) {
            queryWrapper.eq("activity_id", activityId);
        }

        queryWrapper.and(wrapper -> {
            wrapper.eq("sender_id", userId).or().eq("receiver_id", userId);
        });

        queryWrapper.orderByDesc("create_time");
        Page<ActivitySuggestion> suggestionPage = page(pageInfo, queryWrapper);

        // 转换为包含活动和用户信息的DTO
        Page<SuggestionDTO> dtoPage = new Page<>(page, size);
        dtoPage.setTotal(suggestionPage.getTotal());
        dtoPage.setRecords(suggestionPage.getRecords().stream().map(suggestion -> {
            SuggestionDTO dto = new SuggestionDTO();
            dto.setId(suggestion.getId());
            dto.setActivityId(suggestion.getActivityId());
            dto.setSenderId(suggestion.getSenderId());
            dto.setReceiverId(suggestion.getReceiverId());
            dto.setContent(suggestion.getContent());
            dto.setParentId(suggestion.getParentId());
            dto.setIsRead(suggestion.getIsRead());
            dto.setDeleted(suggestion.getDeleted());
            dto.setCreateTime(suggestion.getCreateTime());
            dto.setUpdateTime(suggestion.getUpdateTime());

            // 填充活动标题
            if (suggestion.getActivityId() != null) {
                try {
                    dto.setActivityTitle(activityService.getById(suggestion.getActivityId()).getTitle());
                } catch (Exception e) {
                    dto.setActivityTitle("未知活动");
                }
            } else {
                // 对于回复，尝试从父建议中获取活动标题
                if (suggestion.getParentId() != null) {
                    ActivitySuggestion parent = getById(suggestion.getParentId());
                    if (parent != null && parent.getActivityId() != null) {
                        try {
                            dto.setActivityTitle(activityService.getById(parent.getActivityId()).getTitle());
                        } catch (Exception e) {
                            dto.setActivityTitle("未知活动");
                        }
                    } else {
                        dto.setActivityTitle("未知活动");
                    }
                } else {
                    dto.setActivityTitle("未知活动");
                }
            }

            // 填充发送者名称
            try {
                dto.setSenderName(userService.getById(suggestion.getSenderId()).getRealName());
            } catch (Exception e) {
                dto.setSenderName("未知用户");
            }

            return dto;
        }).collect(java.util.stream.Collectors.toList()));

        return dtoPage;
    }

    public void markAsRead(Long id) {
        ActivitySuggestion suggestion = getById(id);
        if (suggestion != null && suggestion.getIsRead() == 0) {
            suggestion.setIsRead(1);
            updateById(suggestion);
            decrementUnreadCount(suggestion.getReceiverId());
        }
    }

    public void markAllAsRead(Long userId) {
        QueryWrapper<ActivitySuggestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiver_id", userId);
        queryWrapper.eq("is_read", 0);
        ActivitySuggestion updateEntity = new ActivitySuggestion();
        updateEntity.setIsRead(1);
        update(updateEntity, queryWrapper);
        clearUnreadCountCache(userId);
    }

    public Long getUnreadCount(Long userId) {
        String cacheKey = UNREAD_COUNT_CACHE_KEY + userId;
        Object cachedCount = redisUtil.get(cacheKey);
        if (cachedCount != null) {
            return Long.parseLong(cachedCount.toString());
        }

        QueryWrapper<ActivitySuggestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiver_id", userId).eq("is_read", 0);
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