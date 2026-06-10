package com.practice.dto;

import lombok.Data;

@Data
public class AnnouncementRequest {
    private Long id;
    private String title;
    private String content;
    private String targetAudience;
    private String priority;
}
