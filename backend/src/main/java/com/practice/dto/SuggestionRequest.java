package com.practice.dto;

import lombok.Data;

@Data
public class SuggestionRequest {
    private Long activityId;
    private Long receiverId;
    private String content;
    private Long parentId;
}