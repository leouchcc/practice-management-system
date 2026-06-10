<template>
  <div class="teacher-activities">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ pageTitle }}</span>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>
      
      <el-table :data="activityList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="活动ID" width="80" />
        <el-table-column prop="title" label="活动名称" width="200" />
        <el-table-column prop="categoryName" label="活动类别" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="location" label="地点" width="150" />
        <el-table-column prop="maxParticipants" label="最大人数" width="100" />
        <el-table-column prop="currentParticipants" label="当前人数" width="100" />
        <el-table-column prop="creditHours" label="学时" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusName(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewDetail(scope.row)">
              查看详情
            </el-button>
            <el-button size="small" @click="addSuggestion(scope.row)">
              修改建议
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="loadActivities"
          @current-change="loadActivities"
        />
      </div>
    </el-card>

    <el-dialog v-model="suggestionDialogVisible" title="添加修改建议" width="600px">
      <el-form :model="suggestionForm" label-width="100px">
        <el-form-item label="选择学生">
          <el-select v-model="suggestionForm.receiverId" placeholder="请选择学生" style="width: 100%">
            <el-option
              v-for="user in users"
              :key="user.id"
              :label="`${user.realName} (${user.studentId || '无学号'})`"
              :value="user.id.toString()"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="建议内容">
          <el-input v-model="suggestionForm.content" type="textarea" :rows="4" placeholder="请输入修改建议"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="suggestionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSuggestion">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getActivityList } from '@/api/activity'
import { createSuggestion } from '@/api/suggestion'
import { getUserList } from '@/api/user'
import { ElMessage, ElDialog, ElForm, ElFormItem, ElInput, ElButton, ElSelect, ElOption } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activityList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const suggestionDialogVisible = ref(false)
const currentActivity = ref(null)
const suggestionForm = ref({
  receiverId: '',
  content: ''
})
const users = ref([])

const statusFilter = computed(() => route.query.status)
const statusNameFilter = computed(() => route.query.statusName)

const pageTitle = computed(() => {
  if (statusFilter.value) {
    return `我的活动 - ${statusNameFilter.value}`
  }
  return '我发布的活动'
})

const loadActivities = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      creatorId: userStore.userId
    }
    
    if (statusFilter.value) {
      params.status = statusFilter.value
    }
    
    const response = await getActivityList(params)
    
    if (response) {
      activityList.value = response.records || []
      total.value = response.total || 0
    }
  } catch (error) {
    ElMessage.error('获取活动列表失败')
    console.error('获取活动列表失败:', error)
  } finally {
    loading.value = false
  }
}

const loadUsers = async () => {
  try {
    const response = await getUserList({ page: 1, size: 100 })
    if (response && response.records) {
      users.value = response.records.filter(user => user.role === 'STUDENT')
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  }
}

const addSuggestion = async (activity) => {
  currentActivity.value = activity
  suggestionForm.value = {
    receiverId: '',
    content: ''
  }
  await loadUsers()
  suggestionDialogVisible.value = true
}

const submitSuggestion = async () => {
  if (!suggestionForm.value.receiverId || !suggestionForm.value.content) {
    ElMessage.warning('请填写完整信息')
    return
  }

  try {
    await createSuggestion({
      activityId: currentActivity.value.id,
      receiverId: parseInt(suggestionForm.value.receiverId),
      content: suggestionForm.value.content
    })
    ElMessage.success('添加修改建议成功')
    suggestionDialogVisible.value = false
  } catch (error) {
    ElMessage.error('添加修改建议失败')
  }
}

const viewDetail = (activity) => {
  ElMessage.info(`活动详情：${activity.title}`)
}

const goBack = () => {
  router.back()
}

const getStatusName = (status) => {
  const statusMap = {
    'DRAFT': '草稿',
    'PUBLISHED': '已发布',
    'STARTED': '进行中',
    'ENDED': '已结束',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

const getStatusType = (status) => {
  const typeMap = {
    'DRAFT': 'info',
    'PUBLISHED': 'success',
    'STARTED': 'warning',
    'ENDED': 'info',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'default'
}

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.teacher-activities {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
