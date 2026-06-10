<template>
  <div class="notification-center">
    <el-card>
      <template #header>
        <span>通知中心</span>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="系统通知" name="system">
          <el-table :data="systemNotifications" style="width: 100%">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="content" label="内容" show-overflow-tooltip />
            <el-table-column prop="createTime" label="时间" width="180" />
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button link @click="markSystemNotificationAsRead(scope.row)" v-if="!scope.row.isRead">标记已读</el-button>
                <el-tag v-else type="success" size="small">已读</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="活动建议" name="suggestion">
          <el-table :data="suggestions" style="width: 100%">
            <el-table-column prop="activityTitle" label="活动名称" />
            <el-table-column prop="senderName" label="发送者" />
            <el-table-column prop="content" label="内容" show-overflow-tooltip />
            <el-table-column prop="createTime" label="时间" width="180" />
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button link @click="replySuggestion(scope.row)" v-if="scope.row.receiverId === userStore.userId">回复</el-button>
                <el-button link @click="markSuggestionAsRead(scope.row)" v-if="!scope.row.isRead">标记已读</el-button>
                <el-tag v-else type="success" size="small">已读</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <!-- 回复建议对话框 -->
      <el-dialog v-model="replyDialogVisible" title="回复建议" width="500px">
        <el-form :model="replyForm" label-width="80px">
          <el-form-item label="活动名称">
            <el-input v-model="replyForm.activityTitle" disabled />
          </el-form-item>
          <el-form-item label="发送者">
            <el-input v-model="replyForm.senderName" disabled />
          </el-form-item>
          <el-form-item label="原建议">
            <el-input v-model="replyForm.originalContent" type="textarea" :rows="3" disabled />
          </el-form-item>
          <el-form-item label="回复内容" prop="content">
            <el-input
              v-model="replyForm.content"
              type="textarea"
              :rows="4"
              placeholder="请输入回复内容"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReply">发送</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getSuggestionList, replySuggestion as replySuggestionApi, markAsRead as markSuggestionAsReadApi } from '@/api/suggestion'
import { getUnreadCount, getSuggestionUnreadCount, markAsRead as markNotificationAsReadApi, getNotificationList } from '@/api/notification'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const activeTab = ref('system')

// 通知数据
const systemNotifications = ref([])
const suggestions = ref([])

// 回复对话框
const replyDialogVisible = ref(false)
const currentSuggestion = ref(null)
const replyForm = ref({
  activityTitle: '',
  senderName: '',
  originalContent: '',
  content: ''
})

// 获取通知数据
const fetchNotifications = async () => {
  try {
    // 获取系统通知
    const systemRes = await getNotificationList()
    systemNotifications.value = systemRes.records
    
    // 获取活动建议
    const suggestionRes = await getSuggestionList()
    suggestions.value = suggestionRes.records
  } catch (error) {
    console.error('获取通知失败', error)
    ElMessage.error('获取通知失败')
  }
}

// 更新未读消息数
const updateUnreadCount = async () => {
  try {
    const [notificationCount, suggestionCount] = await Promise.all([
      getUnreadCount(),
      getSuggestionUnreadCount()
    ])
    const totalCount = (notificationCount || 0) + (suggestionCount || 0)
    // 更新本地存储的未读消息数，供其他组件使用
    localStorage.setItem('unreadCount', totalCount.toString())
    // 触发全局事件通知 MainLayout 更新
    window.dispatchEvent(new CustomEvent('unreadCountUpdated', { detail: totalCount }))
  } catch (error) {
    console.error('更新未读消息数失败', error)
  }
}

// 标记系统通知为已读
const markSystemNotificationAsRead = async (row) => {
  try {
    await markNotificationAsReadApi(row.id)
    row.isRead = 1
    ElMessage.success('标记成功')
    updateUnreadCount()
  } catch (error) {
    ElMessage.error('标记失败')
  }
}

// 标记建议为已读
const markSuggestionAsRead = async (row) => {
  try {
    await markSuggestionAsReadApi(row.id)
    row.isRead = 1
    ElMessage.success('标记成功')
    updateUnreadCount()
  } catch (error) {
    ElMessage.error('标记失败')
  }
}

// 回复建议
const replySuggestion = (row) => {
  currentSuggestion.value = row
  replyForm.value = {
    activityTitle: row.activityTitle,
    senderName: row.senderName,
    originalContent: row.content,
    content: ''
  }
  replyDialogVisible.value = true
}

// 提交回复
const submitReply = async () => {
  if (!currentSuggestion.value) return

  try {
    await replySuggestionApi({
      parentId: currentSuggestion.value.id,
      receiverId: currentSuggestion.value.senderId,
      content: replyForm.value.content
    })
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    fetchNotifications()
  } catch (error) {
    ElMessage.error(error.message || '回复失败')
  }
}

onMounted(() => {
  fetchNotifications()
  // 页面加载时更新未读数
  updateUnreadCount()
})
</script>

<style scoped>
.notification-center {
  padding: 20px;
}
</style>