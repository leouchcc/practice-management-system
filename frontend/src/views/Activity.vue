<template>
  <div class="activity">
    <el-card class="activity-card">
      <template #header>
        <div class="card-header">
          <h2 class="card-title">{{ isAdmin ? '活动发布' : '活动详情' }}</h2>
          <el-button 
            type="primary" 
            @click="showCreateDialog" 
            v-if="isAdmin"
            class="create-btn"
          >
            <el-icon><Plus /></el-icon>
            <span>创建活动</span>
          </el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="请输入活动名称"
            clearable 
            size="large"
            class="search-input"
          />
        </el-form-item>
        <el-form-item>
          <el-select 
            v-model="searchForm.categoryId" 
            placeholder="请选择分类" 
            clearable
            size="large"
            class="search-select"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select 
            v-model="searchForm.status" 
            placeholder="请选择状态" 
            clearable
            size="large"
            class="search-select"
          >
            <el-option label="已发布" value="PUBLISHED" />
            <el-option label="进行中" value="STARTED" />
            <el-option label="已结束" value="ENDED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            @click="fetchActivities"
            size="large"
            class="search-btn"
          >
            <el-icon><Search /></el-icon>
            <span>搜索</span>
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button 
            @click="resetSearch"
            size="large"
            class="reset-btn"
          >
            <el-icon><Refresh /></el-icon>
            <span>重置</span>
          </el-button>
        </el-form-item>
      </el-form>

      <el-table :data="activities" style="width: 100%" class="activity-table">
        <el-table-column prop="title" label="活动名称" min-width="200">
          <template #default="scope">
            <div class="activity-title">
              <span class="title-text">{{ scope.row.title }}</span>
              <el-tag 
                v-if="scope.row.isContactActivity" 
                size="small" 
                class="contact-tag"
              >
                联系人活动
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="location" label="地点" min-width="150" />
        <el-table-column prop="currentParticipants" label="参与人数" width="120">
          <template #default="scope">
            <div class="participants-info">
              <span class="participants-count">{{ scope.row.currentParticipants }}</span>
              <span class="participants-max">/ {{ scope.row.maxParticipants || '不限' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" class="status-tag">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button link @click="viewActivity(scope.row)" class="action-btn">
                <el-icon><View /></el-icon>
                <span>查看</span>
              </el-button>
              <el-button 
                link 
                @click="registerForActivity(scope.row)" 
                v-if="isStudent && canRegister(scope.row)"
                class="action-btn primary"
              >
                <el-icon><Check /></el-icon>
                <span>报名</span>
              </el-button>
              <el-button 
                link 
                @click="editActivity(scope.row)" 
                v-if="isAdmin"
                class="action-btn"
              >
                <el-icon><Edit /></el-icon>
                <span>编辑</span>
              </el-button>
              <el-button 
                link 
                @click="publishActivity(scope.row)" 
                v-if="isAdmin && scope.row.status === 'DRAFT'"
                class="action-btn primary"
              >
                <el-icon><Top /></el-icon>
                <span>发布</span>
              </el-button>
              <el-button 
                link 
                @click="deleteActivity(scope.row)" 
                v-if="isAdmin"
                class="action-btn danger"
              >
                <el-icon><Delete /></el-icon>
                <span>删除</span>
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="searchForm.page"
        v-model:page-size="searchForm.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchActivities"
        @current-change="fetchActivities"
        class="activity-pagination"
        background
      />
    </el-card>

    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="700px"
      class="activity-dialog"
    >
      <el-form :model="activityForm" :rules="rules" ref="activityFormRef" label-width="120px" class="dialog-form">
        <el-form-item label="活动名称" prop="title">
          <el-input v-model="activityForm.title" placeholder="请输入活动名称" size="large" />
        </el-form-item>
        <el-form-item label="活动分类" prop="categoryId">
          <el-select v-model="activityForm.categoryId" placeholder="请选择分类" size="large">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="活动内容" prop="content">
          <el-input 
            v-model="activityForm.content" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入活动内容" 
            size="large"
          />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="activityForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
            size="large"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="activityForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%"
            size="large"
          />
        </el-form-item>
        <el-form-item label="取消报名最晚时间" prop="cancelDeadline">
          <el-date-picker
            v-model="activityForm.cancelDeadline"
            type="datetime"
            placeholder="选择取消报名最晚时间（可选）"
            style="width: 100%"
            size="large"
          />
        </el-form-item>
        <el-form-item label="活动地点" prop="location">
          <el-input v-model="activityForm.location" placeholder="请输入活动地点" size="large" />
        </el-form-item>
        <el-form-item label="最大人数" prop="maxParticipants">
          <el-input-number v-model="activityForm.maxParticipants" :min="1" placeholder="请输入最大人数" size="large" />
        </el-form-item>
        <el-form-item label="学时" prop="creditHours">
          <el-input-number v-model="activityForm.creditHours" :min="0" :precision="2" placeholder="请输入学时" size="large" />
        </el-form-item>
        <el-form-item label="学分" prop="creditPoints">
          <el-input-number v-model="activityForm.creditPoints" :min="0" :precision="2" placeholder="请输入学分" size="large" />
        </el-form-item>
        <el-form-item label="联系人活动" prop="isContactActivity">
          <el-radio-group v-model="activityForm.isContactActivity" class="radio-group">
            <el-radio-button value="1">是</el-radio-button>
            <el-radio-button value="0">否</el-radio-button>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" class="footer-btn">取消</el-button>
          <el-button type="primary" @click="saveActivity" class="footer-btn primary">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog 
      v-model="viewDialogVisible" 
      title="活动详情" 
      width="700px"
      class="activity-dialog"
    >
      <el-form :model="viewActivityForm" label-width="120px" class="dialog-form" :disabled="true">
        <el-form-item label="活动名称">
          <el-input v-model="viewActivityForm.title" size="large" />
        </el-form-item>
        <el-form-item label="活动分类">
          <el-select v-model="viewActivityForm.categoryId" size="large">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
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
        <el-form-item label="取消报名最晚时间">
          <el-date-picker
            v-model="viewActivityForm.cancelDeadline"
            type="datetime"
            style="width: 100%"
            size="large"
          />
        </el-form-item>
        <el-form-item label="活动地点">
          <el-input v-model="viewActivityForm.location" size="large" />
        </el-form-item>
        <el-form-item label="最大人数">
          <el-input-number v-model="viewActivityForm.maxParticipants" :min="1" size="large" />
        </el-form-item>
        <el-form-item label="学时">
          <el-input-number v-model="viewActivityForm.creditHours" :min="0" :precision="2" size="large" />
        </el-form-item>
        <el-form-item label="学分">
          <el-input-number v-model="viewActivityForm.creditPoints" :min="0" :precision="2" size="large" />
        </el-form-item>
        <el-form-item label="联系人活动">
          <el-radio-group v-model="viewActivityForm.isContactActivity" class="radio-group">
            <el-radio-button value="1">是</el-radio-button>
            <el-radio-button value="0">否</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="当前状态">
          <el-tag :type="getStatusType(viewActivityForm.status)" class="status-tag">
            {{ getStatusText(viewActivityForm.status) }}
          </el-tag>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="viewDialogVisible = false" class="footer-btn">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRoute } from 'vue-router'
import { getActivityList, createActivity, updateActivity, publishActivity as publishActivityApi, deleteActivity as deleteActivityApi } from '@/api/activity'
import { getCategoryList } from '@/api/category'
import { registerActivity } from '@/api/registration'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const route = useRoute()
const isAdmin = computed(() => userStore.role === 'ADMIN' || userStore.role === 'TEACHER')
const isStudent = computed(() => userStore.role === 'STUDENT')

const activities = ref([])
const categories = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('创建活动')
const activityFormRef = ref(null)

const searchForm = reactive({
  page: 1,
  size: 10,
  keyword: '',
  categoryId: null,
  status: ''
})

const activityForm = reactive({
  id: null,
  title: '',
  categoryId: null,
  content: '',
  startTime: '',
  endTime: '',
  cancelDeadline: '',
  location: '',
  maxParticipants: null,
  creditHours: 0,
  creditPoints: 0,
  isContactActivity: '0'
})

const viewActivityForm = reactive({
  id: null,
  title: '',
  categoryId: null,
  content: '',
  startTime: '',
  endTime: '',
  cancelDeadline: '',
  location: '',
  maxParticipants: null,
  creditHours: 0,
  creditPoints: 0,
  isContactActivity: '0',
  status: ''
})

const rules = {
  title: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择活动分类', trigger: 'change' }],
  content: [{ required: true, message: '请输入活动内容', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

const fetchActivities = async () => {
  try {
    const res = await getActivityList(searchForm)
    activities.value = res.records
    total.value = res.total
  } catch (error) {
    console.error('获取活动列表失败', error)
    ElMessage.error('获取活动列表失败')
  }
}

const fetchCategories = async () => {
  try {
    const res = await getCategoryList()
    categories.value = res
  } catch (error) {
    console.error('获取分类列表失败', error)
    ElMessage.error('获取分类列表失败')
  }
}

const showCreateDialog = () => {
  dialogTitle.value = '创建活动'
  Object.assign(activityForm, {
    id: null,
    title: '',
    categoryId: null,
    content: '',
    startTime: '',
    endTime: '',
    cancelDeadline: '',
    location: '',
    maxParticipants: null,
    creditHours: 0,
    creditPoints: 0,
    isContactActivity: '0'
  })
  dialogVisible.value = true
}

const editActivity = (row) => {
  dialogTitle.value = '编辑活动'
  Object.assign(activityForm, {
    ...row,
    isContactActivity: row.isContactActivity ? '1' : '0'
  })
  dialogVisible.value = true
}

const saveActivity = async () => {
  if (!activityFormRef.value) return
  
  await activityFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const formData = {
          ...activityForm,
          isContactActivity: parseInt(activityForm.isContactActivity)
        }
        if (formData.id) {
          await updateActivity(formData)
          ElMessage.success('更新成功')
        } else {
          await createActivity(formData)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        fetchActivities()
      } catch (error) {
        ElMessage.error(error.message || '保存失败')
      }
    }
  })
}

const publishActivity = async (row) => {
  try {
    await ElMessageBox.confirm('确认发布该活动吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await publishActivityApi(row.id)
    ElMessage.success('发布成功')
    fetchActivities()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发布失败')
    }
  }
}

const deleteActivity = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该活动吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteActivityApi(row.id)
    ElMessage.success('删除成功')
    fetchActivities()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const viewActivity = (row) => {
  Object.assign(viewActivityForm, {
    ...row,
    isContactActivity: row.isContactActivity ? '1' : '0'
  })
  viewDialogVisible.value = true
}

const canRegister = (row) => {
  return row.status === 'PUBLISHED' || row.status === 'STARTED'
}

const registerForActivity = async (row) => {
  try {
    await ElMessageBox.confirm(`确认报名参加"${row.title}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    await registerActivity(row.id)
    ElMessage.success('报名成功')
    fetchActivities()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '报名失败')
    }
  }
}

const resetSearch = () => {
  Object.assign(searchForm, {
    page: 1,
    size: 10,
    keyword: '',
    categoryId: null,
    status: ''
  })
  fetchActivities()
}

const getStatusType = (status) => {
  const typeMap = {
    DRAFT: 'info',
    PUBLISHED: 'success',
    STARTED: 'warning',
    ENDED: 'info',
    CANCELLED: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    DRAFT: '草稿',
    PUBLISHED: '已发布',
    STARTED: '进行中',
    ENDED: '已结束',
    CANCELLED: '已取消'
  }
  return textMap[status] || status
}

onMounted(async () => {
  await fetchActivities()
  await fetchCategories()
  
  // 检查URL参数中是否有活动ID
  const activityId = route.query.id
  if (activityId) {
    // 查找对应的活动
    const activity = activities.value.find(item => item.id === activityId)
    if (activity) {
      viewActivity(activity)
    }
  }
})
</script>

<style scoped>
.activity {
  padding: 0;
}

.activity-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border: none;
  overflow: hidden;
  background: #fff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.card-title {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.create-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  padding: 12px 24px;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.create-btn :deep(.el-icon) {
  margin-right: 6px;
}

.search-form {
  padding: 32px;
  background: #f8f9fa;
  border-radius: 12px;
  margin: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.search-input {
  width: 300px;
  border-radius: 8px;
}

.search-select {
  width: 200px;
  border-radius: 8px;
  margin-left: 16px;
}

.search-btn {
  margin-left: 16px;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  transition: all 0.3s;
}

.search-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.reset-btn {
  margin-left: 8px;
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  padding: 0 24px;
  transition: all 0.3s;
}

.reset-btn:hover {
  border-color: #c0c4cc;
  background: #f5f5f5;
}

.search-form :deep(.el-button :deep(.el-icon)) {
  margin-right: 6px;
}

.activity-table {
  margin: 0 24px 24px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.activity-table :deep(.el-table__header-wrapper) {
  background: #f8f9fa;
}

.activity-table :deep(.el-table__header th) {
  font-weight: 600;
  color: #333;
  padding: 16px;
  border-bottom: 1px solid #e9ecef;
}

.activity-table :deep(.el-table__body td) {
  padding: 16px;
  border-bottom: 1px solid #f0f2f5;
}

.activity-table :deep(.el-table__row:hover) {
  background: #f8f9fa;
}

.activity-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-text {
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.contact-tag {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
}

.participants-info {
  display: flex;
  align-items: center;
  gap: 4px;
}

.participants-count {
  font-weight: 600;
  color: #667eea;
}

.participants-max {
  color: #999;
  font-size: 13px;
}

.status-tag {
  border-radius: 10px;
  font-size: 12px;
  padding: 2px 10px;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 13px;
  transition: all 0.3s;
  color: #666;
}

.action-btn:hover {
  background: #f0f0f0;
  color: #333;
}

.action-btn.primary {
  color: #667eea;
}

.action-btn.primary:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #764ba2;
}

.action-btn.danger {
  color: #ff4d4f;
}

.action-btn.danger:hover {
  background: rgba(255, 77, 79, 0.1);
  color: #ff7875;
}

.action-btn :deep(.el-icon) {
  font-size: 14px;
}

.activity-pagination {
  margin: 24px;
  display: flex;
  justify-content: flex-end;
}

.activity-pagination :deep(.el-pagination__item.is-current) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
  color: #fff;
}

.activity-pagination :deep(.el-pagination__item:hover:not(.is-current)) {
  color: #667eea;
  border-color: #667eea;
}

.activity-dialog {
  border-radius: 16px;
  overflow: hidden;
}

.activity-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 24px 32px;
}

.activity-dialog :deep(.el-dialog__title) {
  font-size: 20px;
  font-weight: 600;
  color: #fff;
}

.activity-dialog :deep(.el-dialog__close) {
  color: rgba(255, 255, 255, 0.8);
}

.activity-dialog :deep(.el-dialog__close:hover) {
  color: #fff;
}

.dialog-form {
  padding: 32px;
}

.dialog-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.dialog-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.dialog-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s;
}

.dialog-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

.dialog-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #667eea inset;
}

.radio-group {
  display: flex;
  gap: 16px;
}

.radio-group :deep(.el-radio-button__inner) {
  border-radius: 8px;
  padding: 8px 20px;
  transition: all 0.3s;
}

.radio-group :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
  color: #fff;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 32px;
  background: #f8f9fa;
  border-top: 1px solid #e9ecef;
}

.footer-btn {
  padding: 10px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.footer-btn.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: #fff;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.footer-btn.primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

@media (max-width: 1200px) {
  .search-input {
    width: 250px;
  }
  
  .search-select {
    width: 180px;
  }
  
  .action-buttons {
    gap: 4px;
  }
  
  .action-btn {
    padding: 4px 8px;
    font-size: 12px;
  }
}
</style>