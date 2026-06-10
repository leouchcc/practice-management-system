package com.practice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RegistrationDTO {
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
    private String evaluationStatus; // 点评状态：PENDING-待点评 COMPLETED-已点评
    private String rating; // 点评等级：EXCELLENT-优秀 GOOD-良好 PASS-及格
    private BigDecimal finalCreditPoints; // 最终获得的学分
    
    private ActivityInfo activity;
    private StudentInfo student;
    
    @Data
    public static class ActivityInfo {
        private Long id;
        private String title;
        private String startTime;
        private String endTime;
        private String cancelDeadline;
        private String location;
        private Integer maxParticipants;
        private Integer currentParticipants;
        private BigDecimal creditHours;
        private BigDecimal creditPoints;
        private String status;
    }
    
    @Data
    public static class StudentInfo {
        private Long id;
        private String username;
        private String realName;
        private String studentId;
        private String phone;
        private String email;
    }
}
