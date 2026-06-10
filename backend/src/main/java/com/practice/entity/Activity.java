package com.practice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("activity")
public class Activity {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private Long categoryId;

    private Long organizerId;

    private String content;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime cancelDeadline;

    private String location;

    private Integer maxParticipants;

    private Integer currentParticipants;

    private BigDecimal creditHours;

    private BigDecimal creditPoints;

    private String status;

    private Integer isContactActivity;
    
    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
