package com.practice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("activity_registration")
public class ActivityRegistration {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long activityId;

    private Long studentId;

    private LocalDateTime registrationTime;

    private String status;

    private LocalDateTime checkInTime;

    private String proofFilePath;
    private String proofFileName;
    private LocalDateTime proofSubmitTime;
    private Boolean proofVerified;

    private LocalDateTime checkOutTime;

    private BigDecimal actualHours;

    @TableField(exist = false)
    private String evaluationStatus; // 点评状态：PENDING-待点评 COMPLETED-已点评
    @TableField(exist = false)
    private String rating; // 点评等级：EXCELLENT-优秀 GOOD-良好 PASS-及格
    @TableField(exist = false)
    private BigDecimal finalCreditPoints; // 最终获得的学分

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
