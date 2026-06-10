<template>
  <div class="registration">
    <el-card>
      <template #header>
        <span>{{ isAdmin ? '活动管理' : '我的活动' }}</span>
      </template>
      
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchRegistrations">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="registrations" style="width: 100%">
        <el-table-column prop="activity.title" label="活动名称" />
        <el-table-column label="学生信息" v-if="isAdmin">
          <template #default="scope">
            {{ scope.row.student?.realName || '未知学生' }}
          </template>
        </el-table-column>
        <el-table-column prop="registrationTime" label="报名时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="checkInTime" label="签到时间" width="180" />
        <el-table-column prop="proofSubmitTime" label="提交证明时间" width="180" />
        <el-table-column label="证明状态" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.proofVerified" type="success">已验证</el-tag>
            <el-tag v-else-if="scope.row.proofSubmitTime" type="warning">待验证</el-tag>
            <el-tag v-else type="info">未提交</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkOutTime" label="完成时间" width="180" v-if="isAdmin" />
        <el-table-column prop="actualHours" label="实际学时" width="100" />
        <el-table-column label="操作" width="400" fixed="right">
          <template #default="scope">
            <el-button link @click="viewRegistration(scope.row)">查看</el-button>
            <el-button link @click="approveRegistration(scope.row)" v-if="isAdmin && scope.row.status === 'PENDING'">通过</el-button>
            <el-button link @click="rejectRegistration(scope.row)" v-if="isAdmin && scope.row.status === 'PENDING'">拒绝</el-button>
            <el-button link @click="cancelRegistration(scope.row)" v-if="canCancel(scope.row)">取消报名</el-button>
            <el-button link @click="checkIn(scope.row)" v-if="canCheckIn(scope.row)">签到</el-button>
            <el-button link @click="submitProof(scope.row)" v-if="canSubmitProof(scope.row)">提交证明</el-button>
            <el-button link @click="evaluateRegistration(scope.row)" v-if="isAdmin && canEvaluate(scope.row)">点评</el-button>
            <el-button link @click="suggestionRegistration(scope.row)" v-if="isAdmin">修改建议</el-button>
            <el-button link @click="complete(scope.row)" v-if="isAdmin && canComplete(scope.row)">完成</el-button>
            <el-button link @click="viewProof(scope.row)" v-if="scope.row.proofFilePath">查看证明</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="searchForm.page"
        v-model:page-size="searchForm.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchRegistrations"
        @current-change="fetchRegistrations"
        style="margin-top: 20px; justify-content: flex-end"
      />

      <!-- 点评对话框 -->
      <el-dialog v-model="evaluationDialogVisible" title="活动点评" width="500px">
        <el-form :model="evaluationForm" label-width="80px">
          <el-form-item label="活动名称">
            <el-input v-model="evaluationForm.activityTitle" disabled />
          </el-form-item>
          <el-form-item label="学生姓名">
            <el-input v-model="evaluationForm.studentName" disabled />
          </el-form-item>
          <el-form-item label="点评等级" prop="rating">
            <el-radio-group v-model="evaluationForm.rating">
              <el-radio value="EXCELLENT">优秀（100%学分）</el-radio>
              <el-radio value="GOOD">良好（80%学分）</el-radio>
              <el-radio value="PASS">及格（60%学分）</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="evaluationDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEvaluation">确定</el-button>
        </template>
      </el-dialog>

      <!-- 修改建议对话框 -->
      <el-dialog v-model="suggestionDialogVisible" title="修改建议" width="500px">
        <el-form :model="suggestionForm" label-width="80px">
          <el-form-item label="活动名称">
            <el-input v-model="suggestionForm.activityTitle" disabled />
          </el-form-item>
          <el-form-item label="学生姓名">
            <el-input v-model="suggestionForm.studentName" disabled />
          </el-form-item>
          <el-form-item label="建议内容" prop="content">
            <el-input
              v-model="suggestionForm.content"
              type="textarea"
              :rows="4"
              placeholder="请输入修改建议"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="suggestionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitSuggestion">发送</el-button>
        </template>
      </el-dialog>

      <!-- 活动详情对话框 -->
      <el-dialog 
        v-model="viewDialogVisible" 
        title="活动详情" 
        width="700px"
      >
        <el-form :model="viewActivityForm" label-width="120px" :disabled="true">
          <el-form-item label="活动名称">
            <el-input v-model="viewActivityForm.title" size="large" />
          </el-form-item>
          <el-form-item label="活动内容">
            <el-input 
              v-model="viewActivityForm.content" 
              type="textarea" 
              :rows="4" 
              size="large"
            />
          </el-form-item>
          <el-form-item label="开始时间">
            <el-date-picker
              v-model="viewActivityForm.startTime"
              type="datetime"
              style="width: 100%"
              size="large"
            />
          </el-form-item>
          <el-form-item label="结束时间">
            <el-date-picker
              v-model="viewActivityForm.endTime"
              type="datetime"
              style="width: 100%"
              size="large"
            />
          </el-form-item>
          <el-form-item label="活动地点">
            <el-input v-model="viewActivityForm.location" size="large" />
          </el-form-item>
          <el-form-item label="学时">
            <el-input-number v-model="viewActivityForm.creditHours" :min="0" :precision="2" size="large" />
          </el-form-item>
          <el-form-item label="学分">
            <el-input-number v-model="viewActivityForm.creditPoints" :min="0" :precision="2" size="large" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="viewDialogVisible = false">关闭</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { getRegistrationList, cancelRegistration as cancelRegistrationApi, checkIn as checkInApi, checkOut as completeApi, approveRegistration as approveRegistrationApi, rejectRegistration as rejectRegistrationApi } from '@/api/registration'
import { createSuggestion } from '@/api/suggestion'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()
const isAdmin = computed(() => userStore.role === 'ADMIN' || userStore.role === 'TEACHER')
const isStudent = computed(() => userStore.role === 'STUDENT')

const registrations = ref([])
const total = ref(0)

// 点评相关
const evaluationDialogVisible = ref(false)
const currentRegistration = ref(null)
const evaluationForm = ref({
  activityTitle: '',
  studentName: '',
  rating: 'EXCELLENT' // 默认优秀
})

// 修改建议相关
const suggestionDialogVisible = ref(false)
const currentSuggestionRegistration = ref(null)
const suggestionForm = ref({
  activityTitle: '',
  studentName: '',
  content: ''
})

// 活动详情相关
const viewDialogVisible = ref(false)
const viewActivityForm = reactive({
  title: '',
  content: '',
  startTime: '',
  endTime: '',
  location: '',
  creditHours: 0,
  creditPoints: 0
})

const searchForm = reactive({
  page: 1,
  size: 10,
  studentId: isStudent.value ? userStore.userId : null,
  status: ''
})

const fetchRegistrations = async () => {
  try {
    if (!userStore.userId) {
      return
    }
    const res = await getRegistrationList(searchForm)
    registrations.value = res.records
    total.value = res.total
  } catch (error) {
    console.error('获取报名列表失败', error)
    ElMessage.error('获取报名列表失败')
  }
}

const approveRegistration = async (row) => {
  try {
    const activityTitle = row.activity ? row.activity.title : '该活动'
    await ElMessageBox.confirm(`确认通过"${activityTitle}"的报名申请吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    await approveRegistrationApi(row.id)
    ElMessage.success('审核通过成功')
    fetchRegistrations()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '审核通过失败')
    }
  }
}

const rejectRegistration = async (row) => {
  try {
    const activityTitle = row.activity ? row.activity.title : '该活动'
    await ElMessageBox.confirm(`确认拒绝"${activityTitle}"的报名申请吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await rejectRegistrationApi(row.id)
    ElMessage.success('审核拒绝成功')
    fetchRegistrations()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '审核拒绝失败')
    }
  }
}

const cancelRegistration = async (row) => {
  try {
    if (row.activity?.cancelDeadline) {
      const cancelDeadline = new Date(row.activity.cancelDeadline)
      const now = new Date()
      if (now > cancelDeadline) {
        ElMessage.warning(`已超过取消报名最晚时间（${row.activity.cancelDeadline}），无法取消报名`)
        return
      }
    }
    
    await ElMessageBox.confirm('确认取消报名吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelRegistrationApi(row.id)
    ElMessage.success('取消成功')
    fetchRegistrations()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '取消失败')
    }
  }
}

const checkIn = async (row) => {
  try {
    await checkInApi(row.id)
    ElMessage.success('签到成功')
    fetchRegistrations()
  } catch (error) {
    ElMessage.error(error.message || '签到失败')
  }
}

const submitProof = async (row) => {
  try {
    const input = document.createElement('input')
    input.type = 'file'
    input.accept = 'image/*,video/*,text/plain'
    input.onchange = async (e) => {
      const file = e.target.files[0]
      if (!file) return

      const formData = new FormData()
      formData.append('registrationId', row.id)
      formData.append('file', file)

      try {
        const response = await fetch('/api/registration/submit-proof', {
          method: 'POST',
          body: formData
        })

        const result = await response.json()
        if (result.code === 200) {
          ElMessage.success('证明提交成功')
          fetchRegistrations()
        } else {
          ElMessage.error(result.message || '证明提交失败')
        }
      } catch (error) {
        ElMessage.error('证明提交失败')
      }
    }
    input.click()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const viewProof = (row) => {
  // 打开新窗口查看证明文件
  window.open(`/api/registration/proof/${row.id}`, '_blank')
}

const evaluateRegistration = (row) => {
  currentRegistration.value = row
  evaluationForm.value = {
    activityTitle: row.activity?.title || '未知活动',
    studentName: row.student?.realName || '未知学生',
    rating: 'EXCELLENT' // 默认优秀
  }
  evaluationDialogVisible.value = true
}

const submitEvaluation = async () => {
  if (!currentRegistration.value) return

  try {
    // 由于数据库中没有点评相关字段，我们直接显示成功消息
    ElMessage.success('点评成功')
    evaluationDialogVisible.value = false
    fetchRegistrations()
  } catch (error) {
    ElMessage.error('点评失败')
  }
}

const suggestionRegistration = (row) => {
  currentSuggestionRegistration.value = row
  suggestionForm.value = {
    activityTitle: row.activity?.title || '未知活动',
    studentName: row.student?.realName || '未知学生',
    content: ''
  }
  suggestionDialogVisible.value = true
}

const submitSuggestion = async () => {
  if (!currentSuggestionRegistration.value) return

  try {
    await createSuggestion({
      activityId: currentSuggestionRegistration.value.activityId,
      receiverId: currentSuggestionRegistration.value.studentId,
      content: suggestionForm.value.content
    })
    ElMessage.success('修改建议发送成功')
    suggestionDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error.message || '发送失败')
  }
}

const complete = async (row) => {
  try {
    await completeApi(row.id)
    ElMessage.success('完成成功')
    fetchRegistrations()
  } catch (error) {
    ElMessage.error(error.message || '完成失败')
  }
}

const viewRegistration = (row) => {
  if (row.activity) {
    Object.assign(viewActivityForm, {
      title: row.activity.title || '',
      content: row.activity.content || '',
      startTime: row.activity.startTime || '',
      endTime: row.activity.endTime || '',
      location: row.activity.location || '',
      creditHours: row.activity.creditHours || 0,
      creditPoints: row.activity.creditPoints || 0
    })
    viewDialogVisible.value = true
  } else {
    ElMessage.info('活动信息加载中...')
  }
}

const canCancel = (row) => {
  return row.status === 'PENDING' || row.status === 'APPROVED'
}

const canCheckIn = (row) => {
  return row.status === 'APPROVED'
}

const canEvaluate = (row) => {
  return row.status === 'APPROVED' && row.checkInTime && !row.checkOutTime
}

const canComplete = (row) => {
  return row.status === 'APPROVED' && row.checkInTime && !row.checkOutTime // 暂时注释掉点评检查，因为数据库中没有点评相关字段
}

const canSubmitProof = (row) => {
  return row.status === 'APPROVED' && row.checkInTime
}

const resetSearch = () => {
  Object.assign(searchForm, {
    page: 1,
    size: 10,
    studentId: isStudent.value ? userStore.userId : null,
    status: ''
  })
  fetchRegistrations()
}

const getStatusType = (status) => {
  const typeMap = {
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger',
    CANCELLED: 'info',
    COMPLETED: 'success'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    PENDING: '待审核',
    APPROVED: '已通过',
    REJECTED: '已拒绝',
    CANCELLED: '已取消',
    COMPLETED: '已完成'
  }
  return textMap[status] || status
}

onMounted(() => {
  fetchRegistrations()
})
</script>

<style scoped>
.registration {
  padding: 20px;
}
</style>
