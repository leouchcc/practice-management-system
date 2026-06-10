package com.practice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.practice.entity.ContactRelation;
import com.practice.entity.User;
import com.practice.mapper.ContactRelationMapper;
import com.practice.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ContactRelationService extends ServiceImpl<ContactRelationMapper, ContactRelation> {

    @Autowired
    private ContactRelationMapper contactRelationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SystemNotificationService notificationService;

    /**
     * 添加联系人
     * @param requesterId 请求者ID
     * @param realName 被添加者真实姓名
     * @param studentId 被添加者学号/工号
     */
    public void sendContactRequest(Long requesterId, String realName, String studentId) {
        // 查找被添加者
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("real_name", realName)
                .eq("student_id", studentId);
        User addressee = userMapper.selectOne(userQueryWrapper);

        if (addressee == null) {
            throw new RuntimeException("未找到匹配的用户");
        }

        // 检查是否已经存在关系
        QueryWrapper<ContactRelation> relationQueryWrapper = new QueryWrapper<>();
        relationQueryWrapper.eq("requester_id", requesterId)
                .eq("addressee_id", addressee.getId());
        ContactRelation existingRelation = contactRelationMapper.selectOne(relationQueryWrapper);

        if (existingRelation != null) {
            throw new RuntimeException("已经是联系人");
        }

        // 创建联系人关系，直接设置为已接受状态
        ContactRelation relation = new ContactRelation();
        relation.setRequesterId(requesterId);
        relation.setAddresseeId(addressee.getId());
        relation.setStatus("ACCEPTED");
        contactRelationMapper.insert(relation);

        // 发送通知给被添加者
        User requester = userMapper.selectById(requesterId);
        String title = "联系人添加通知";
        String content = requester.getRealName() + "（" + (requester.getStudentId() != null ? "教师" : requester.getStudentId()) + "）已将您添加为联系人。";
        notificationService.createNotification(
                addressee.getId(),
                title,
                content,
                "SUCCESS",
                relation.getId(),
                "CONTACT_ADDED"
        );
    }

    /**
     * 接受联系人申请
     * @param relationId 关系ID
     */
    public void acceptContactRequest(Long relationId) {
        ContactRelation relation = contactRelationMapper.selectById(relationId);
        if (relation == null) {
            throw new RuntimeException("申请不存在");
        }

        if (!"PENDING".equals(relation.getStatus())) {
            throw new RuntimeException("申请状态已变更");
        }

        // 更新状态为已接受
        relation.setStatus("ACCEPTED");
        contactRelationMapper.updateById(relation);

        // 发送通知给请求者
        User requester = userMapper.selectById(relation.getRequesterId());
        User addressee = userMapper.selectById(relation.getAddresseeId());
        String title = "联系人申请已接受";
        String content = addressee.getRealName() + "（" + addressee.getStudentId() + "）已接受您的联系人申请，现在可以开始联系了。";
        notificationService.createNotification(
                requester.getId(),
                title,
                content,
                "SUCCESS",
                relation.getId(),
                "CONTACT_ACCEPTED"
        );
    }

    /**
     * 拒绝联系人申请
     * @param relationId 关系ID
     */
    public void rejectContactRequest(Long relationId) {
        ContactRelation relation = contactRelationMapper.selectById(relationId);
        if (relation == null) {
            throw new RuntimeException("申请不存在");
        }

        if (!"PENDING".equals(relation.getStatus())) {
            throw new RuntimeException("申请状态已变更");
        }

        // 更新状态为已拒绝
        relation.setStatus("REJECTED");
        contactRelationMapper.updateById(relation);

        // 发送通知给请求者
        User requester = userMapper.selectById(relation.getRequesterId());
        User addressee = userMapper.selectById(relation.getAddresseeId());
        String title = "联系人申请已拒绝";
        String content = addressee.getRealName() + "（" + addressee.getStudentId() + "）已拒绝您的联系人申请。";
        notificationService.createNotification(
                requester.getId(),
                title,
                content,
                "INFO",
                relation.getId(),
                "CONTACT_REJECTED"
        );
    }

    /**
     * 获取用户的联系人列表
     * @param userId 用户ID
     * @return 联系人列表
     */
    public List<User> getContactList(Long userId) {
        // 查找用户作为请求者且状态为ACCEPTED的联系人
        QueryWrapper<ContactRelation> requesterQuery = new QueryWrapper<>();
        requesterQuery.eq("requester_id", userId)
                .eq("status", "ACCEPTED");
        List<ContactRelation> requesterRelations = contactRelationMapper.selectList(requesterQuery);

        // 查找用户作为被添加者且状态为ACCEPTED的联系人
        QueryWrapper<ContactRelation> addresseeQuery = new QueryWrapper<>();
        addresseeQuery.eq("addressee_id", userId)
                .eq("status", "ACCEPTED");
        List<ContactRelation> addresseeRelations = contactRelationMapper.selectList(addresseeQuery);

        // 合并联系人ID
        Set<Long> contactIds = new HashSet<>();
        for (ContactRelation relation : requesterRelations) {
            contactIds.add(relation.getAddresseeId());
        }
        for (ContactRelation relation : addresseeRelations) {
            contactIds.add(relation.getRequesterId());
        }

        // 查询联系人信息
        List<User> contacts = new ArrayList<>();
        for (Long contactId : contactIds) {
            User contact = userMapper.selectById(contactId);
            if (contact != null) {
                contacts.add(contact);
            }
        }

        return contacts;
    }

    /**
     * 删除联系人
     * @param userId 当前用户ID
     * @param contactId 联系人ID
     */
    public void deleteContact(Long userId, Long contactId) {
        // 查找联系人关系（双向查找）
        QueryWrapper<ContactRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
                .eq("requester_id", userId)
                .eq("addressee_id", contactId)
                .or()
                .eq("requester_id", contactId)
                .eq("addressee_id", userId)
        );
        ContactRelation relation = contactRelationMapper.selectOne(queryWrapper);

        if (relation == null) {
            throw new RuntimeException("联系人关系不存在");
        }

        // 删除联系人关系
        contactRelationMapper.deleteById(relation.getId());

        // 发送通知给被删除的联系人
        User currentUser = userMapper.selectById(userId);
        User contactUser = userMapper.selectById(contactId);
        String title = "联系人删除通知";
        String content = currentUser.getRealName() + "（" + (currentUser.getStudentId() != null ? "教师" : currentUser.getStudentId()) + "）已将您从联系人列表中删除。";
        notificationService.createNotification(
                contactId,
                title,
                content,
                "INFO",
                relation.getId(),
                "CONTACT_DELETED"
        );
    }
}
