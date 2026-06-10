package com.practice.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.practice.entity.ActivityCategory;
import com.practice.mapper.ActivityCategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityCategoryService extends ServiceImpl<ActivityCategoryMapper, ActivityCategory> {

    public List<ActivityCategory> getAllCategories() {
        return list();
    }

    public ActivityCategory getCategoryById(Long id) {
        return getById(id);
    }

    public void createCategory(ActivityCategory category) {
        save(category);
    }

    public void updateCategory(ActivityCategory category) {
        updateById(category);
    }

    public void deleteCategory(Long id) {
        removeById(id);
    }
}
