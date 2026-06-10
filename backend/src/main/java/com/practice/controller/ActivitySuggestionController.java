package com.practice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.practice.common.Result;
import com.practice.dto.SuggestionDTO;
import com.practice.dto.SuggestionRequest;
import com.practice.service.ActivitySuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suggestion")
@CrossOrigin
public class ActivitySuggestionController {

    @Autowired
    private ActivitySuggestionService suggestionService;

    @PostMapping
    public Result<Void> createSuggestion(@RequestBody SuggestionRequest request, @RequestHeader("userId") Long userId) {
        try {
            suggestionService.createSuggestion(
                    request.getActivityId(),
                    userId,
                    request.getReceiverId(),
                    request.getContent()
            );
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/reply")
    public Result<Void> replySuggestion(@RequestBody SuggestionRequest request, @RequestHeader("userId") Long userId) {
        try {
            suggestionService.replySuggestion(
                    request.getParentId(),
                    userId,
                    request.getReceiverId(),
                    request.getContent()
            );
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<Page<SuggestionDTO>> getSuggestionList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long activityId,
            @RequestHeader("userId") Long userId) {
        Page<SuggestionDTO> suggestionPage = suggestionService.getSuggestionList(page, size, activityId, userId);
        return Result.success(suggestionPage);
    }

    @PostMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        try {
            suggestionService.markAsRead(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/read-all")
    public Result<Void> markAllAsRead(@RequestHeader("userId") Long userId) {
        try {
            suggestionService.markAllAsRead(userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(@RequestHeader("userId") Long userId) {
        try {
            Long count = suggestionService.getUnreadCount(userId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}