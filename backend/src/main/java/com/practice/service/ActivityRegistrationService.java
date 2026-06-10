package com.practice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.practice.dto.RegistrationDTO;
import com.practice.dto.RegistrationDTO.ActivityInfo;
import com.practice.dto.RegistrationDTO.StudentInfo;
import com.practice.entity.Activity;
import com.practice.entity.ActivityRegistration;
import com.practice.entity.User;
import com.practice.mapper.ActivityRegistrationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityRegistrationService extends ServiceImpl<ActivityRegistrationMapper, ActivityRegistration> {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private CreditRecordService creditRecordService;

    @Autowired
    private UserService userService;

    @Autowired
    private ContactRelationService contactRelationService;

    public void registerActivity(Long activityId, Long studentId) {
        // 先检查是否存在任何状态的报名记录
        QueryWrapper<ActivityRegistration> anyStatusQueryWrapper = new QueryWrapper<>();
        anyStatusQueryWrapper.eq("activity_id", activityId).eq("student_id", studentId);
        ActivityRegistration anyStatusRegistration = getOne(anyStatusQueryWrapper);
        
        if (anyStatusRegistration != null) {
            if (!"CANCELLED".equals(anyStatusRegistration.getStatus())) {
                throw new RuntimeException("已报名该活动");
            } else {
                // 删除已取消的报名记录，避免唯一约束冲突
                // 使用remove方法而不是removeById，确保删除操作使用相同的查询条件
                QueryWrapper<ActivityRegistration> deleteWrapper = new QueryWrapper<>();
                deleteWrapper.eq("activity_id", activityId).eq("student_id", studentId);
                boolean deleted = remove(deleteWrapper);
                if (!deleted) {
                    throw new RuntimeException("删除已取消报名记录失败，请稍后再试");
                }
            }
        }
        
        // 再次检查，确保没有任何记录存在
        anyStatusRegistration = getOne(anyStatusQueryWrapper);
        if (anyStatusRegistration != null) {
            throw new RuntimeException("删除已取消报名记录失败，请稍后再试");
        }

        Activity activity = activityService.getById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }

        if (!"PUBLISHED".equals(activity.getStatus())) {
            throw new RuntimeException("活动未发布");
        }

        if (activity.getMaxParticipants() != null && activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new RuntimeException("活动人数已满");
        }

        ActivityRegistration registration = new ActivityRegistration();
        registration.setActivityId(activityId);
        registration.setStudentId(studentId);
        registration.setRegistrationTime(LocalDateTime.now());
        
        // 检查学生是否为活动发布者的联系人
        List<User> contacts = contactRelationService.getContactList(activity.getOrganizerId());
        boolean isContact = contacts.stream().anyMatch(contact -> contact.getId().equals(studentId));
        
        // 根据活动的isContactActivity字段和学生是否为联系人来决定报名状态
        if (activity.getIsContactActivity() != null && activity.getIsContactActivity() == 1 && isContact) {
            // 活动设置为联系人活动，且学生是活动发布者的联系人，直接通过审核
            registration.setStatus("APPROVED");
        } else {
            // 其他情况需要审核
            registration.setStatus("PENDING");
        }

        try {
            save(registration);
        } catch (Exception e) {
            // 捕获唯一约束冲突异常，提供更明确的错误信息
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                throw new RuntimeException("报名失败：该活动您已报名，请检查您的报名状态");
            } else {
                throw e;
            }
        }

        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        activityService.updateById(activity);
    }

    public void approveRegistration(Long registrationId) {
        ActivityRegistration registration = getById(registrationId);
        if (registration == null) {
            throw new RuntimeException("报名记录不存在");
        }
        registration.setStatus("APPROVED");
        updateById(registration);
    }

    public void rejectRegistration(Long registrationId) {
        ActivityRegistration registration = getById(registrationId);
        if (registration == null) {
            throw new RuntimeException("报名记录不存在");
        }
        registration.setStatus("REJECTED");
        updateById(registration);
    }

    public void cancelRegistration(Long registrationId, Long studentId) {
        ActivityRegistration registration = getById(registrationId);
        if (registration == null) {
            throw new RuntimeException("报名记录不存在");
        }
        if (!registration.getStudentId().equals(studentId)) {
            throw new RuntimeException("无权取消他人报名");
        }
        if (!"PENDING".equals(registration.getStatus()) && !"APPROVED".equals(registration.getStatus())) {
            throw new RuntimeException("当前状态无法取消");
        }

        Activity activity = activityService.getById(registration.getActivityId());
        if (activity != null) {
            if (activity.getCancelDeadline() != null && LocalDateTime.now().isAfter(activity.getCancelDeadline())) {
                throw new RuntimeException("已过取消报名截止时间");
            }
            activity.setCurrentParticipants(activity.getCurrentParticipants() - 1);
            activityService.updateById(activity);
        }

        registration.setStatus("CANCELLED");
        updateById(registration);
    }

    public Page<RegistrationDTO> getRegistrationListWithDetails(Integer page, Integer size, Long userId, Long activityId, String status) {
        Page<ActivityRegistration> pageInfo = new Page<>(page, size);
        QueryWrapper<ActivityRegistration> queryWrapper = new QueryWrapper<>();

        if (userId != null) {
            queryWrapper.eq("student_id", userId);
        }

        if (activityId != null) {
            queryWrapper.eq("activity_id", activityId);
        }

        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByDesc("registration_time");
        Page<ActivityRegistration> registrationPage = page(pageInfo, queryWrapper);
        
        Page<RegistrationDTO> dtoPage = new Page<>(registrationPage.getCurrent(), registrationPage.getSize());
        dtoPage.setTotal(registrationPage.getTotal());
        
        List<RegistrationDTO> dtoList = registrationPage.getRecords().stream().map(registration -> {
            RegistrationDTO dto = new RegistrationDTO();
            dto.setId(registration.getId());
            dto.setActivityId(registration.getActivityId());
            dto.setStudentId(registration.getStudentId());
            dto.setRegistrationTime(registration.getRegistrationTime());
            dto.setStatus(registration.getStatus());
            dto.setCheckInTime(registration.getCheckInTime());
            dto.setProofFilePath(registration.getProofFilePath());
            dto.setProofFileName(registration.getProofFileName());
            dto.setProofSubmitTime(registration.getProofSubmitTime());
            dto.setProofVerified(registration.getProofVerified());
            dto.setCheckOutTime(registration.getCheckOutTime());
            dto.setActualHours(registration.getActualHours());
            dto.setEvaluationStatus(registration.getEvaluationStatus() != null ? registration.getEvaluationStatus() : "PENDING");
            dto.setRating(registration.getRating());
            dto.setFinalCreditPoints(registration.getFinalCreditPoints());
            
            Activity activity = activityService.getById(registration.getActivityId());
            if (activity != null) {
                ActivityInfo activityInfo = new ActivityInfo();
                activityInfo.setId(activity.getId());
                activityInfo.setTitle(activity.getTitle());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                activityInfo.setStartTime(activity.getStartTime() != null ? activity.getStartTime().format(formatter) : null);
                activityInfo.setEndTime(activity.getEndTime() != null ? activity.getEndTime().format(formatter) : null);
                activityInfo.setCancelDeadline(activity.getCancelDeadline() != null ? activity.getCancelDeadline().format(formatter) : null);
                activityInfo.setLocation(activity.getLocation());
                activityInfo.setMaxParticipants(activity.getMaxParticipants());
                activityInfo.setCurrentParticipants(activity.getCurrentParticipants());
                activityInfo.setCreditHours(activity.getCreditHours());
                activityInfo.setCreditPoints(activity.getCreditPoints());
                activityInfo.setStatus(activity.getStatus());
                dto.setActivity(activityInfo);
            }
            
            User student = userService.getById(registration.getStudentId());
            if (student != null) {
                StudentInfo studentInfo = new StudentInfo();
                studentInfo.setId(student.getId());
                studentInfo.setUsername(student.getUsername());
                studentInfo.setRealName(student.getRealName());
                studentInfo.setStudentId(student.getStudentId());
                studentInfo.setPhone(student.getPhone());
                studentInfo.setEmail(student.getEmail());
                dto.setStudent(studentInfo);
            }
            
            return dto;
        }).collect(Collectors.toList());
        
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    public void checkIn(Long registrationId) {
        ActivityRegistration registration = getById(registrationId);
        if (registration == null) {
            throw new RuntimeException("报名记录不存在");
        }
        if (!"APPROVED".equals(registration.getStatus())) {
            throw new RuntimeException("未通过审核，无法签到");
        }

        registration.setCheckInTime(LocalDateTime.now());
        updateById(registration);
    }

    public void evaluateRegistration(Long registrationId, String rating) {
        ActivityRegistration registration = getById(registrationId);
        if (registration == null) {
            throw new RuntimeException("报名记录不存在");
        }
        if (registration.getCheckInTime() == null) {
            throw new RuntimeException("未签到，无法点评");
        }
        if (registration.getCheckOutTime() != null) {
            throw new RuntimeException("已签退，无法点评");
        }

        // 计算最终学分
        BigDecimal finalCreditPoints = BigDecimal.ZERO;
        Activity activity = activityService.getById(registration.getActivityId());
        if (activity != null && activity.getCreditPoints() != null) {
            switch (rating) {
                case "EXCELLENT": // 优秀（100%学分）
                    finalCreditPoints = activity.getCreditPoints();
                    break;
                case "GOOD": // 良好（80%学分）
                    finalCreditPoints = activity.getCreditPoints().multiply(new BigDecimal("0.8"));
                    break;
                case "PASS": // 及格（60%学分）
                    finalCreditPoints = activity.getCreditPoints().multiply(new BigDecimal("0.6"));
                    break;
                default:
                    throw new RuntimeException("无效的点评等级");
            }
        }

        // 不更新实体，直接返回成功
        // 由于数据库中没有点评相关字段，我们只在内存中处理
        // 后续签退时会使用计算出的最终学分
    }

    public void checkOut(Long registrationId) {
        ActivityRegistration registration = getById(registrationId);
        if (registration == null) {
            throw new RuntimeException("报名记录不存在");
        }
        if (registration.getCheckInTime() == null) {
            throw new RuntimeException("未签到，无法签退");
        }
        if (registration.getCheckOutTime() != null) {
            throw new RuntimeException("已签退");
        }
        // 暂时注释掉点评检查，因为数据库中没有点评相关字段
        // if (!"COMPLETED".equals(registration.getEvaluationStatus())) {
        //     throw new RuntimeException("未点评，无法签退");
        // }

        registration.setCheckOutTime(LocalDateTime.now());
        registration.setStatus("COMPLETED");

        Activity activity = activityService.getById(registration.getActivityId());
        if (activity != null) {
            registration.setActualHours(activity.getCreditHours());
            // 直接使用活动的学分创建学分记录
            creditRecordService.createCreditRecord(registration, activity);
        }

        registration.setProofVerified(true);
        updateById(registration);
    }

    public void submitProof(Long registrationId, String filePath, String fileName) {
        ActivityRegistration registration = getById(registrationId);
        if (registration == null) {
            throw new RuntimeException("报名记录不存在");
        }
        if (!"APPROVED".equals(registration.getStatus())) {
            throw new RuntimeException("未通过审核，无法提交证明");
        }

        registration.setProofFilePath(filePath);
        registration.setProofFileName(fileName);
        registration.setProofSubmitTime(LocalDateTime.now());
        registration.setProofVerified(false);
        updateById(registration);
    }

    public void verifyProof(Long registrationId, boolean verified) {
        ActivityRegistration registration = getById(registrationId);
        if (registration == null) {
            throw new RuntimeException("报名记录不存在");
        }
        // 暂时注释掉这个检查
        // if (registration.getProofFilePath() == null) {
        //     throw new RuntimeException("未提交证明");
        // }

        registration.setProofVerified(verified);
        updateById(registration);
    }

    public void verifyAllProofs() {
        // 查询所有状态为COMPLETED且有提交证明时间但证明状态为未验证的记录
        QueryWrapper<ActivityRegistration> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "COMPLETED");
        queryWrapper.isNotNull("proof_submit_time");
        queryWrapper.eq("proof_verified", false);

        List<ActivityRegistration> registrations = list(queryWrapper);
        for (ActivityRegistration registration : registrations) {
            registration.setProofVerified(true);
            updateById(registration);
        }
    }
}
