package com.practice.dto;

import com.practice.entity.ActivitySuggestion;
import lombok.Data;

@Data
public class SuggestionDTO extends ActivitySuggestion {
    private String activityTitle;
    private String senderName;
    private String receiverName;
}
