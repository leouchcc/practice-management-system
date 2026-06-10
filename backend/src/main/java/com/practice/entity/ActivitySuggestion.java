package com.practice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("activity_suggestion")
public class ActivitySuggestion {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long activityId;

    private Long senderId;

    private Long receiverId;

    private String content;

    private Long parentId;

    private Integer isRead;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}