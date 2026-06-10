package com.practice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreditRecordDTO {
    private Long id;
    private Long studentId;
    private String studentNumber;
    private String studentName;
    private Long activityId;
    private String activityTitle;
    private LocalDateTime activityStartTime;
    private LocalDateTime activityEndTime;
    private Long registrationId;
    private BigDecimal creditHours;
    private BigDecimal creditPoints;
    private String academicYear;
    private String semester;
    private LocalDateTime createTime;
}
