package com.practice.controller;

import com.practice.common.Result;
import com.practice.entity.User;
import com.practice.service.ContactRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin
public class ContactRelationController {

    @Autowired
    private ContactRelationService contactRelationService;

    /**
     * 发送联系人申请
     * @param requestData 请求数据，包含realName和studentId
     * @param userId 请求者ID
     * @return 操作结果
     */
    @PostMapping("/request")
    public Result<Void> sendContactRequest(@RequestBody Map<String, String> requestData, @RequestHeader("userId") Long userId) {
        try {
            String realName = requestData.get("realName");
            String studentId = requestData.get("studentId");

            if (realName == null || studentId == null) {
                return Result.error("请填写真实姓名和学号/工号");
            }

            contactRelationService.sendContactRequest(userId, realName, studentId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 接受联系人申请
     * @param relationId 关系ID
     * @return 操作结果
     */
    @PostMapping("/accept/{relationId}")
    public Result<Void> acceptContactRequest(@PathVariable Long relationId) {
        try {
            contactRelationService.acceptContactRequest(relationId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 拒绝联系人申请
     * @param relationId 关系ID
     * @return 操作结果
     */
    @PostMapping("/reject/{relationId}")
    public Result<Void> rejectContactRequest(@PathVariable Long relationId) {
        try {
            contactRelationService.rejectContactRequest(relationId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取联系人列表
     * @param userId 用户ID
     * @return 联系人列表
     */
    @GetMapping("/list")
    public Result<List<User>> getContactList(@RequestHeader("userId") Long userId) {
        try {
            List<User> contacts = contactRelationService.getContactList(userId);
            return Result.success(contacts);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除联系人
     * @param contactId 联系人ID
     * @return 操作结果
     */
    @DeleteMapping("/{contactId}")
    public Result<Void> deleteContact(@PathVariable Long contactId, @RequestHeader("userId") Long userId) {
        try {
            contactRelationService.deleteContact(userId, contactId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
