package com.practice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.practice.common.Result;
import com.practice.dto.RegistrationDTO;
import com.practice.entity.ActivityRegistration;
import com.practice.service.ActivityRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/registration")
@CrossOrigin
public class ActivityRegistrationController {

    @Autowired
    private ActivityRegistrationService registrationService;

    @PostMapping("/register")
    public Result<Void> registerActivity(@RequestParam Long activityId) {
        try {
            // 从请求头获取用户ID
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            Long userId = Long.parseLong(request.getHeader("userId"));
            registrationService.registerActivity(activityId, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/approve")
    public Result<Void> approveRegistration(@PathVariable Long id) {
        try {
            registrationService.approveRegistration(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/reject")
    public Result<Void> rejectRegistration(@PathVariable Long id) {
        try {
            registrationService.rejectRegistration(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancelRegistration(@PathVariable Long id) {
        try {
            // 从请求头获取用户ID
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            Long userId = Long.parseLong(request.getHeader("userId"));
            registrationService.cancelRegistration(id, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/check-in")
    public Result<Void> checkIn(@PathVariable Long id) {
        try {
            registrationService.checkIn(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/check-out")
    public Result<Void> checkOut(@PathVariable Long id) {
        try {
            registrationService.checkOut(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<Page<RegistrationDTO>> getRegistrationList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long activityId,
            @RequestParam(required = false) String status) {
        Page<RegistrationDTO> registrationPage = registrationService.getRegistrationListWithDetails(page, size, studentId, activityId, status);
        return Result.success(registrationPage);
    }

    @PostMapping("/submit-proof")
    public Result<Void> submitProof(
            @RequestParam Long registrationId,
            @RequestParam MultipartFile file) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + fileExtension;

            // 确保上传目录存在
            String uploadDir = System.getProperty("user.dir") + "/uploads/proofs";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 保存文件
            String filePath = uploadDir + "/" + fileName;
            file.transferTo(new File(filePath));

            // 调用服务方法保存文件信息
            registrationService.submitProof(registrationId, "/uploads/proofs/" + fileName, originalFilename);

            return Result.success();
        } catch (Exception e) {
            return Result.error("证明提交失败: " + e.getMessage());
        }
    }

    @GetMapping("/proof/{id}")
    public void viewProof(@PathVariable Long id, jakarta.servlet.http.HttpServletResponse response) {
        try {
            // 获取报名记录
            ActivityRegistration registration = registrationService.getById(id);
            if (registration == null) {
                response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("报名记录不存在");
                return;
            }

            String filePath = registration.getProofFilePath();
            String fileName = registration.getProofFileName();

            if (filePath == null || filePath.isEmpty()) {
                response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("证明文件不存在");
                return;
            }

            // 构建完整文件路径
            String fullPath = System.getProperty("user.dir") + filePath;
            File file = new File(fullPath);

            if (!file.exists()) {
                response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("文件不存在");
                return;
            }

            // 设置响应头
            response.setContentType(getContentType(fileName));
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setContentLengthLong(file.length());

            // 读取文件并写入响应
            java.nio.file.Files.copy(file.toPath(), response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("下载文件失败: " + e.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @PostMapping("/evaluate")
    public Result<Void> evaluateRegistration(@RequestBody EvaluateRequest request) {
        try {
            // 由于数据库中没有点评相关字段，我们直接返回成功
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    private String getContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "mp4":
                return "video/mp4";
            case "avi":
                return "video/x-msvideo";
            case "mov":
                return "video/quicktime";
            case "txt":
                return "text/plain";
            case "pdf":
                return "application/pdf";
            default:
                return "application/octet-stream";
        }
    }

    // 点评请求DTO
    private static class EvaluateRequest {
        private Long registrationId;
        private String rating;

        public Long getRegistrationId() {
            return registrationId;
        }

        public void setRegistrationId(Long registrationId) {
            this.registrationId = registrationId;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }
    }

    @PostMapping("/verify-all-proofs")
    public Result<Void> verifyAllProofs() {
        try {
            registrationService.verifyAllProofs();
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
