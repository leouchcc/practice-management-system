package com.practice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("credit_record")
public class CreditRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long activityId;

    private Long registrationId;

    private BigDecimal creditHours;

    private BigDecimal creditPoints;

    private String academicYear;

    private String semester;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
