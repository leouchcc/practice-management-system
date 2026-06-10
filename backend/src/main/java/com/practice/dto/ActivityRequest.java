package com.practice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ActivityRequest {
    private Long id;
    private String title;
    private Long categoryId;
    private String content;
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime cancelDeadline;

    private String location;
    private Integer maxParticipants;
    private BigDecimal creditHours;
    private BigDecimal creditPoints;
    private Integer isContactActivity;
}
