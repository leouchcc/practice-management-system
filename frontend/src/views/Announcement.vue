<template>
  <div class="announcement">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>公告通知</span>
          <el-button type="primary" @click="showCreateDialog" v-if="isAdmin">发布公告</el-button>
        </div>
      </template>
      
      <el-tabs type="border-card">
        <el-tab-pane label="系统公告">
          <el-table :data="announcements" style="width: 100%">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="content" label="内容" show-overflow-tooltip />
            <el-table-column prop="priority" label="优先级" width="100">
              <template #default="scope">
                <el-tag :type="getPriorityType(scope.row.priority)">{{ getPriorityText(scope.row.priority) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="publishTime" label="发布时间" width="180" />
            <el-table-column label="操作" width="150" v-if="isAdmin">
              <template #default="scope">
                <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            @current-change="fetchAnnouncements"
            @size-change="fetchAnnouncements"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 20px"
          />
        </el-tab-pane>
        
        <el-tab-pane label="通知中心">
          <el-table :data="notifications" style="width: 100%">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="content" label="内容" show-overflow-tooltip />
            <el-table-column prop="type" label="类型" width="100">
              <template #default="scope">
                <el-tag :type="getNotificationType(scope.row.type)">{{ getNotificationText(scope.row.type) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="isRead" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.isRead ? 'success' : 'warning'">
                  {{ scope.row.isRead ? '已读' : '未读' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column label="操作" width="250">
              <template #default="scope">
                <el-button size="small" @click="markAsRead(scope.row.id)" v-if="!scope.row.isRead">标记已读</el-button>
                <el-button size="small" type="primary" @click="handleContactRequest(scope.row)" v-if="scope.row.relatedType === 'CONTACT_REQUEST' && !scope.row.isRead">处理申请</el-button>
                <el-button size="small" @click="handleSuggestion(scope.row)" v-if="scope.row.relatedType === 'ACTIVITY_SUGGESTION'">查看建议</el-button>
                <el-button size="small" @click="handleSuggestionReply(scope.row)" v-if="scope.row.relatedType === 'SUGGESTION_REPLY'">查看回复</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <el-pagination
            v-model:current-page="notificationPagination.page"
            v-model:page-size="notificationPagination.size"
            :total="notificationPagination.total"
            @current-change="fetchNotifications"
            @size-change="fetchNotifications"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 20px"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题"></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入内容"></el-input>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="form.priority" placeholder="请选择优先级">
            <el-option label="低" value="LOW" />
            <el-option label="普通" value="NORMAL" />
            <el-option label="高" value="HIGH" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标受众" prop="targetAudience">
          <el-select v-model="form.targetAudience" placeholder="请选择目标受众">
            <el-option label="全部" value="ALL" />
            <el-option label="学生" value="STUDENT" />
            <el-option label="教师" value="TEACHER" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 联系人申请处理对话框 -->
    <el-dialog v-model="contactDialogVisible" title="处理联系人申请" width="500px">
      <div class="contact-request-content">
        <p>{{ currentNotification?.content }}</p>
      </div>
      <template #footer>
        <el-button @click="contactDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="acceptContactRequest">接受</el-button>
        <el-button type="danger" @click="rejectContactRequest">拒绝</el-button>
      </template>
    </el-dialog>

    <!-- 建议详情对话框 -->
    <el-dialog v-model="suggestionDialogVisible" title="建议详情" width="800px">
      <el-timeline>
        <el-timeline-item
          v-for="(suggestion, index) in suggestions"
          :key="suggestion.id"
          :timestamp="suggestion.createTime"
          :type="suggestion.senderId === userStore.userId ? 'success' : 'info'"
        >
          <div>
            <h4>{{ suggestion.senderId === userStore.userId ? '我' : '对方' }}</h4>
            <p>{{ suggestion.content }}</p>
            <el-button size="small" @click="showReplyDialog(suggestion)" style="margin-top: 10px">
              回复
            </el-button>
          </div>
        </el-timeline-item>
      </el-timeline>
      <template #footer>
        <el-button @click="suggestionDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 回复对话框 -->
    <el-dialog v-model="replyDialogVisible" title="回复建议" width="600px">
      <el-form :model="replyForm" label-width="100px">
        <el-form-item label="回复内容">
          <el-input v-model="replyForm.content" type="textarea" :rows="4" placeholder="请输入回复内容"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getAnnouncementList, createAnnouncement, updateAnnouncement, deleteAnnouncement } from '@/api/announcement'
import { getNotificationList, markAsRead as markNotificationAsRead } from '@/api/notification'
import { getSuggestionList, replySuggestion } from '@/api/suggestion'
import { ElMessage, ElMessageBox, ElDialog, ElForm, ElFormItem, ElInput, ElButton, ElTimeline, ElTimelineItem } from 'element-plus'
import axios from 'axios'

const userStore = useUserStore()
const announcements = ref([])
const notifications = ref([])
const dialogVisible = ref(false)
const contactDialogVisible = ref(false)
const suggestionDialogVisible = ref(false)
const replyDialogVisible = ref(false)
const dialogTitle = ref('发布公告')
const formRef = ref(null)
const currentNotification = ref(null)
const currentSuggestion = ref(null)
const suggestions = ref([])
const replyForm = ref({
  receiverId: '',
  content: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const notificationPagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  title: '',
  content: '',
  priority: 'NORMAL',
  targetAudience: 'ALL'
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
  targetAudience: [{ required: true, message: '请选择目标受众', trigger: 'change' }]
}

const isAdmin = computed(() => userStore.role === 'ADMIN' || userStore.role === 'TEACHER')

const fetchAnnouncements = async () => {
  try {
    const res = await getAnnouncementList({
      page: pagination.page,
      size: pagination.size
    })
    announcements.value = res.records
    pagination.total = res.total
  } catch (error) {
    ElMessage.error('获取公告列表失败')
  }
}

const fetchNotifications = async () => {
  try {
    const res = await getNotificationList({
      page: notificationPagination.page,
      size: notificationPagination.size
    })
    notifications.value = res.records
    notificationPagination.total = res.total
  } catch (error) {
    ElMessage.error('获取通知列表失败')
  }
}

const showCreateDialog = () => {
  dialogTitle.value = '发布公告'
  Object.assign(form, {
    id: null,
    title: '',
    content: '',
    priority: 'NORMAL',
    targetAudience: 'ALL'
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑公告'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await updateAnnouncement(form.id, form)
          ElMessage.success('更新成功')
        } else {
          await createAnnouncement(form)
          ElMessage.success('发布成功')
        }
        dialogVisible.value = false
        fetchAnnouncements()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条公告吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAnnouncement(id)
    ElMessage.success('删除成功')
    fetchAnnouncements()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const markAsRead = async (id) => {
  try {
    await markNotificationAsRead(id)
    ElMessage.success('标记已读成功')
    fetchNotifications()
  } catch (error) {
    ElMessage.error('标记已读失败')
  }
}

const handleContactRequest = (notification) => {
  currentNotification.value = notification
  contactDialogVisible.value = true
}

const acceptContactRequest = async () => {
  try {
    const token = localStorage.getItem('token')
    await axios.post(`/api/contact/accept/${currentNotification.value.relatedId}`, {}, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    ElMessage.success('接受申请成功')
    contactDialogVisible.value = false
    fetchNotifications()
  } catch (error) {
    ElMessage.error('接受申请失败')
  }
}

const rejectContactRequest = async () => {
  try {
    const token = localStorage.getItem('token')
    await axios.post(`/api/contact/reject/${currentNotification.value.relatedId}`, {}, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    ElMessage.success('拒绝申请成功')
    contactDialogVisible.value = false
    fetchNotifications()
  } catch (error) {
    ElMessage.error('拒绝申请失败')
  }
}

const handleSuggestion = async (notification) => {
  currentNotification.value = notification
  try {
    const response = await getSuggestionList({ activityId: null })
    if (response && response.records) {
      suggestions.value = response.records.filter(s => 
        s.id === notification.relatedId || s.parentId === notification.relatedId
      )
      if (suggestions.value.length > 0) {
        currentSuggestion.value = suggestions.value.find(s => s.id === notification.relatedId)
        suggestionDialogVisible.value = true
      }
    }
  } catch (error) {
    ElMessage.error('获取建议详情失败')
  }
}

const handleSuggestionReply = async (notification) => {
  currentNotification.value = notification
  try {
    const response = await getSuggestionList({ activityId: null })
    if (response && response.records) {
      suggestions.value = response.records.filter(s => 
        s.id === notification.relatedId || s.parentId === notification.relatedId
      )
      if (suggestions.value.length > 0) {
        currentSuggestion.value = suggestions.value.find(s => s.id === notification.relatedId)
        suggestionDialogVisible.value = true
      }
    }
  } catch (error) {
    ElMessage.error('获取建议详情失败')
  }
}

const showReplyDialog = (suggestion) => {
  currentSuggestion.value = suggestion
  replyForm.value = {
    receiverId: suggestion.senderId.toString(),
    content: ''
  }
  replyDialogVisible.value = true
}

const submitReply = async () => {
  if (!replyForm.value.content) {
    ElMessage.warning('请输入回复内容')
    return
  }

  try {
    await replySuggestion({
      parentId: currentSuggestion.value.id,
      receiverId: parseInt(replyForm.value.receiverId),
      content: replyForm.value.content
    })
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    handleSuggestion(currentNotification.value)
  } catch (error) {
    ElMessage.error('回复失败')
  }
}

const getPriorityType = (priority) => {
  const map = {
    'LOW': 'info',
    'NORMAL': 'success',
    'HIGH': 'danger'
  }
  return map[priority] || 'info'
}

const getPriorityText = (priority) => {
  const map = {
    'LOW': '低',
    'NORMAL': '普通',
    'HIGH': '高'
  }
  return map[priority] || priority
}

const getStatusType = (status) => {
  const map = {
    'DRAFT': 'info',
    'PUBLISHED': 'success',
    'ARCHIVED': 'warning'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'DRAFT': '草稿',
    'PUBLISHED': '已发布',
    'ARCHIVED': '已归档'
  }
  return map[status] || status
}

const getNotificationType = (type) => {
  const map = {
    'INFO': 'info',
    'WARNING': 'warning',
    'SUCCESS': 'success',
    'ERROR': 'danger'
  }
  return map[type] || 'info'
}

const getNotificationText = (type) => {
  const map = {
    'INFO': '信息',
    'WARNING': '警告',
    'SUCCESS': '成功',
    'ERROR': '错误'
  }
  return map[type] || type
}

onMounted(() => {
  fetchAnnouncements()
  fetchNotifications()
})
</script>

<style scoped>
.announcement {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
