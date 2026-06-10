package com.practice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.practice.dto.AnnouncementRequest;
import com.practice.entity.Announcement;
import com.practice.mapper.AnnouncementMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AnnouncementService extends ServiceImpl<AnnouncementMapper, Announcement> {

    public Page<Announcement> getAnnouncementList(Integer page, Integer size, String targetAudience, String status) {
        Page<Announcement> pageInfo = new Page<>(page, size);
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();

        if (targetAudience != null && !targetAudience.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .eq("target_audience", targetAudience)
                    .or()
                    .eq("target_audience", "ALL"));
        }

        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByDesc("publish_time");
        return page(pageInfo, queryWrapper);
    }

    public Announcement getAnnouncementById(Long id) {
        return getById(id);
    }

    public void createAnnouncement(AnnouncementRequest request, Long publisherId) {
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setPublisherId(publisherId);
        announcement.setTargetAudience(request.getTargetAudience());
        announcement.setPriority(request.getPriority());
        announcement.setStatus("PUBLISHED");
        announcement.setPublishTime(LocalDateTime.now());

        save(announcement);
    }

    public void updateAnnouncement(AnnouncementRequest request) {
        Announcement announcement = getById(request.getId());
        if (announcement == null) {
            throw new RuntimeException("公告不存在");
        }

        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setTargetAudience(request.getTargetAudience());
        announcement.setPriority(request.getPriority());

        updateById(announcement);
    }

    public void deleteAnnouncement(Long id) {
        removeById(id);
    }
}
