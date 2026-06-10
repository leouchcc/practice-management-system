package com.practice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.practice.dto.CreditRecordDTO;
import com.practice.entity.Activity;
import com.practice.entity.ActivityRegistration;
import com.practice.entity.ContactRelation;
import com.practice.entity.CreditRecord;
import com.practice.entity.User;
import com.practice.mapper.CreditRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CreditRecordService extends ServiceImpl<CreditRecordMapper, CreditRecord> {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ContactRelationService contactRelationService;

    @Autowired
    private com.practice.mapper.UserMapper userMapper;

    public void createCreditRecord(ActivityRegistration registration, Activity activity) {
        CreditRecord record = new CreditRecord();
        record.setStudentId(registration.getStudentId());
        record.setActivityId(activity.getId());
        record.setRegistrationId(registration.getId());
        record.setCreditHours(activity.getCreditHours());
        record.setCreditPoints(activity.getCreditPoints());
        record.setAcademicYear(getCurrentAcademicYear());
        record.setSemester(getCurrentSemester());

        save(record);
    }

    public void createCreditRecordWithFinalPoints(ActivityRegistration registration, Activity activity, BigDecimal finalCreditPoints) {
        CreditRecord record = new CreditRecord();
        record.setStudentId(registration.getStudentId());
        record.setActivityId(activity.getId());
        record.setRegistrationId(registration.getId());
        record.setCreditHours(activity.getCreditHours());
        record.setCreditPoints(finalCreditPoints); // 使用点评后的最终学分
        record.setAcademicYear(getCurrentAcademicYear());
        record.setSemester(getCurrentSemester());

        save(record);
    }

    public Page<CreditRecordDTO> getCreditRecordList(Integer page, Integer size, Long studentId, Long userId, String academicYear, String semester, BigDecimal minCreditPoints, BigDecimal maxCreditPoints) {
        Page<CreditRecord> pageInfo = new Page<>(page, size);
        QueryWrapper<CreditRecord> queryWrapper = new QueryWrapper<>();

        if (studentId != null) {
            queryWrapper.eq("student_id", studentId);
        }

        if (userId != null) {
            // 检查userId是否是学生
            User user = userMapper.selectById(userId);
            if (user != null && "STUDENT".equals(user.getRole())) {
                // 如果是学生，查询该学生的记录
                queryWrapper.eq("student_id", userId);
            } else if (user != null && "TEACHER".equals(user.getRole())) {
                // 如果是教师，查询该教师联系人的记录
                List<User> contacts = contactRelationService.getContactList(userId);
                if (!contacts.isEmpty()) {
                    Set<Long> contactIds = contacts.stream().map(User::getId).collect(java.util.stream.Collectors.toSet());
                    queryWrapper.in("student_id", contactIds);
                } else {
                    // 如果教师没有联系人，返回空结果
                    queryWrapper.eq("id", -1);
                }
            }
            // 管理员角色不设置条件，查询所有记录
        }

        if (academicYear != null && !academicYear.isEmpty()) {
            queryWrapper.eq("academic_year", academicYear);
        }

        if (semester != null && !semester.isEmpty()) {
            queryWrapper.eq("semester", semester);
        }

        // 添加学分范围查询
        if (minCreditPoints != null) {
            queryWrapper.ge("credit_points", minCreditPoints);
        }
        if (maxCreditPoints != null) {
            queryWrapper.le("credit_points", maxCreditPoints);
        }

        queryWrapper.orderByDesc("create_time");
        Page<CreditRecord> recordPage = page(pageInfo, queryWrapper);

        // 转换为包含活动名称的DTO
        List<CreditRecordDTO> dtoList = recordPage.getRecords().stream().map(record -> {
            CreditRecordDTO dto = new CreditRecordDTO();
            dto.setId(record.getId());
            dto.setStudentId(record.getStudentId());
            dto.setActivityId(record.getActivityId());
            dto.setRegistrationId(record.getRegistrationId());
            dto.setCreditHours(record.getCreditHours());
            dto.setCreditPoints(record.getCreditPoints());
            dto.setAcademicYear(record.getAcademicYear());
            dto.setSemester(record.getSemester());
            dto.setCreateTime(record.getCreateTime());

            // 获取活动名称
            Activity activity = activityService.getById(record.getActivityId());
            if (activity != null) {
                dto.setActivityTitle(activity.getTitle());
                dto.setActivityStartTime(activity.getStartTime());
                dto.setActivityEndTime(activity.getEndTime());
            }

            // 获取学生信息
            User student = userMapper.selectById(record.getStudentId());
            if (student != null) {
                dto.setStudentNumber(student.getStudentId());
                dto.setStudentName(student.getRealName());
            }

            return dto;
        }).collect(java.util.stream.Collectors.toList());

        Page<CreditRecordDTO> dtoPage = new Page<>(recordPage.getCurrent(), recordPage.getSize());
        dtoPage.setTotal(recordPage.getTotal());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    public BigDecimal getTotalCreditHours(Long studentId) {
        QueryWrapper<CreditRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        List<CreditRecord> records = list(queryWrapper);

        return records.stream()
                .map(CreditRecord::getCreditHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalCreditPoints(Long studentId) {
        QueryWrapper<CreditRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        List<CreditRecord> records = list(queryWrapper);

        return records.stream()
                .map(CreditRecord::getCreditPoints)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String getCurrentAcademicYear() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        if (month >= 9) {
            return year + "-" + (year + 1);
        } else {
            return (year - 1) + "-" + year;
        }
    }

    private String getCurrentSemester() {
        int month = LocalDate.now().getMonthValue();
        if (month >= 2 && month <= 7) {
            return "春季学期";
        } else if (month >= 8 && month <= 12) {
            return "秋季学期";
        } else {
            return "冬季学期";
        }
    }

    public List<CreditRecordDTO> getAllCreditRecordsByStudentId(Long studentId) {
        QueryWrapper<CreditRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        queryWrapper.orderByDesc("create_time");
        List<CreditRecord> records = list(queryWrapper);

        return records.stream().map(record -> {
            CreditRecordDTO dto = new CreditRecordDTO();
            dto.setId(record.getId());
            dto.setStudentId(record.getStudentId());
            dto.setActivityId(record.getActivityId());
            dto.setRegistrationId(record.getRegistrationId());
            dto.setCreditHours(record.getCreditHours());
            dto.setCreditPoints(record.getCreditPoints());
            dto.setAcademicYear(record.getAcademicYear());
            dto.setSemester(record.getSemester());
            dto.setCreateTime(record.getCreateTime());

            Activity activity = activityService.getById(record.getActivityId());
            if (activity != null) {
                dto.setActivityTitle(activity.getTitle());
                dto.setActivityStartTime(activity.getStartTime());
                dto.setActivityEndTime(activity.getEndTime());
            }

            User student = userMapper.selectById(record.getStudentId());
            if (student != null) {
                dto.setStudentNumber(student.getStudentId());
                dto.setStudentName(student.getRealName());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    public List<CreditRecordDTO> getAllCreditRecordsByTeacherId(Long teacherId) {
        List<User> contacts = contactRelationService.getContactList(teacherId);
        Set<Long> contactIds = contacts.stream().map(User::getId).collect(Collectors.toSet());

        QueryWrapper<CreditRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("student_id", contactIds);
        queryWrapper.orderByDesc("create_time");
        List<CreditRecord> records = list(queryWrapper);

        return records.stream().map(record -> {
            CreditRecordDTO dto = new CreditRecordDTO();
            dto.setId(record.getId());
            dto.setStudentId(record.getStudentId());
            dto.setActivityId(record.getActivityId());
            dto.setRegistrationId(record.getRegistrationId());
            dto.setCreditHours(record.getCreditHours());
            dto.setCreditPoints(record.getCreditPoints());
            dto.setAcademicYear(record.getAcademicYear());
            dto.setSemester(record.getSemester());
            dto.setCreateTime(record.getCreateTime());

            Activity activity = activityService.getById(record.getActivityId());
            if (activity != null) {
                dto.setActivityTitle(activity.getTitle());
                dto.setActivityStartTime(activity.getStartTime());
                dto.setActivityEndTime(activity.getEndTime());
            }

            User student = userMapper.selectById(record.getStudentId());
            if (student != null) {
                dto.setStudentNumber(student.getStudentId());
                dto.setStudentName(student.getRealName());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    public List<CreditRecordDTO> getAllCreditRecords() {
        QueryWrapper<CreditRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        List<CreditRecord> records = list(queryWrapper);

        return records.stream().map(record -> {
            CreditRecordDTO dto = new CreditRecordDTO();
            dto.setId(record.getId());
            dto.setStudentId(record.getStudentId());
            dto.setActivityId(record.getActivityId());
            dto.setRegistrationId(record.getRegistrationId());
            dto.setCreditHours(record.getCreditHours());
            dto.setCreditPoints(record.getCreditPoints());
            dto.setAcademicYear(record.getAcademicYear());
            dto.setSemester(record.getSemester());
            dto.setCreateTime(record.getCreateTime());

            Activity activity = activityService.getById(record.getActivityId());
            if (activity != null) {
                dto.setActivityTitle(activity.getTitle());
                dto.setActivityStartTime(activity.getStartTime());
                dto.setActivityEndTime(activity.getEndTime());
            }

            User student = userMapper.selectById(record.getStudentId());
            if (student != null) {
                dto.setStudentNumber(student.getStudentId());
                dto.setStudentName(student.getRealName());
            }

            return dto;
        }).collect(Collectors.toList());
    }
}
