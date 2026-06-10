package com.practice.controller;

import com.practice.common.Result;
import com.practice.entity.ActivityCategory;
import com.practice.service.ActivityCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@CrossOrigin
public class ActivityCategoryController {

    @Autowired
    private ActivityCategoryService categoryService;

    @GetMapping("/list")
    public Result<List<ActivityCategory>> getAllCategories() {
        List<ActivityCategory> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    @GetMapping("/{id}")
    public Result<ActivityCategory> getCategoryById(@PathVariable Long id) {
        ActivityCategory category = categoryService.getCategoryById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        return Result.success(category);
    }

    @PostMapping
    public Result<Void> createCategory(@RequestBody ActivityCategory category) {
        try {
            categoryService.createCategory(category);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping
    public Result<Void> updateCategory(@RequestBody ActivityCategory category) {
        try {
            categoryService.updateCategory(category);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
