<template>
  <div class="dashboard">
    <!-- 数据卡片 -->
    <el-row :gutter="20">
      <el-col :span="6" v-if="!isAdmin">
        <el-card class="data-card" @click="$router.push('/credit')">
          <div class="card-content">
            <div class="card-icon hours-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-value">{{ totalHours }}</div>
              <div class="card-label">总学时</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6" v-if="!isAdmin">
        <el-card class="data-card" @click="$router.push('/credit')">
          <div class="card-content">
            <div class="card-icon points-icon">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-value">{{ totalPoints }}</div>
              <div class="card-label">总学分</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="data-card" @click="$router.push('/activity')">
          <div class="card-content">
            <div class="card-icon activity-icon">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-value">{{ activityCount }}</div>
              <div class="card-label">{{ isAdmin ? '总活动数' : '参与活动' }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="data-card" @click="$router.push('/announcement')">
          <div class="card-content">
            <div class="card-icon todo-icon">
              <el-icon><Message /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-value">{{ todoCount }}</div>
              <div class="card-label">{{ isAdmin ? '待审核证明' : '待办事项' }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6" v-if="isAdmin">
        <el-card class="data-card quick-action-card" @click="$router.push('/user')">
          <div class="card-content">
            <div class="card-icon user-manage-icon">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-value">用户管理</div>
              <div class="card-label">管理用户信息</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6" v-if="isAdmin">
        <el-card class="data-card quick-action-card" @click="$router.push('/profile')">
          <div class="card-content">
            <div class="card-icon profile-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-value">个人信息</div>
              <div class="card-label">查看个人资料</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 内容区域 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 最新公告 -->
      <el-col :span="12">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Bell /></el-icon>
                最新公告
              </span>
              <el-button link type="primary" @click="$router.push('/announcement')">
                查看更多 <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </template>
          <div class="announcement-list">
            <div 
              v-for="item in announcements" 
              :key="item.id" 
              class="announcement-item"
              @click="viewAnnouncement(item)"
            >
              <div class="announcement-title">{{ item.title }}</div>
              <div class="announcement-time">
                <el-icon><Clock /></el-icon>
                {{ formatDate(item.publishTime) }}
              </div>
            </div>
            <div v-if="announcements.length === 0" class="empty-state">
              <el-empty description="暂无公告" :image-size="80" />
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 最新活动 -->
      <el-col :span="12">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Calendar /></el-icon>
                最新活动
              </span>
              <el-button link type="primary" @click="$router.push('/activity')">
                查看更多 <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </template>
          <div class="activity-list">
            <div 
              v-for="item in activities" 
              :key="item.id" 
              class="activity-item"
            >
              <div class="activity-info">
                <div class="activity-title">{{ item.title }}</div>
                <div class="activity-time">
                  <el-icon><Clock /></el-icon>
                  {{ formatDate(item.startTime) }}
                </div>
              </div>
              <el-button type="primary" size="small" @click="viewActivity(item)">
                查看
              </el-button>
            </div>
            <div v-if="activities.length === 0" class="empty-state">
              <el-empty description="暂无活动" :image-size="80" />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="todo-row" v-if="isAdmin">
      <el-col :span="24">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><DataAnalysis /></el-icon>
                数据概览
              </span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="chart-container">
                <div class="chart-title">学分分布</div>
                <div ref="creditChart" class="chart"></div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="chart-container">
                <div class="chart-title">活动参与趋势</div>
                <div ref="trendChart" class="chart"></div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="todo-row" v-if="!isAdmin">
      <el-col :span="24">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Position /></el-icon>
                快速入口
              </span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="quick-entry-item" @click="$router.push('/activity')">
                <div class="entry-icon activity-entry">
                  <el-icon><Calendar /></el-icon>
                </div>
                <div class="entry-info">
                  <div class="entry-label">活动发布</div>
                  <div class="entry-desc">查看和发布活动</div>
                </div>
                <el-icon class="entry-arrow"><ArrowRight /></el-icon>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="quick-entry-item" @click="$router.push('/registration')">
                <div class="entry-icon registration-entry">
                  <el-icon><Document /></el-icon>
                </div>
                <div class="entry-info">
                  <div class="entry-label">活动管理</div>
                  <div class="entry-desc">管理我的活动</div>
                </div>
                <el-icon class="entry-arrow"><ArrowRight /></el-icon>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="quick-entry-item" @click="$router.push('/credit')">
                <div class="entry-icon credit-entry">
                  <el-icon><TrendCharts /></el-icon>
                </div>
                <div class="entry-info">
                  <div class="entry-label">学时学分</div>
                  <div class="entry-desc">查看我的学分</div>
                </div>
                <el-icon class="entry-arrow"><ArrowRight /></el-icon>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="quick-entry-item" @click="$router.push('/profile')">
                <div class="entry-icon profile-entry">
                  <el-icon><User /></el-icon>
                </div>
                <div class="entry-info">
                  <div class="entry-label">个人信息</div>
                  <div class="entry-desc">编辑个人资料</div>
                </div>
                <el-icon class="entry-arrow"><ArrowRight /></el-icon>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- 活动详情对话框 -->
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
        <div class="dialog-footer">
          <el-button @click="viewDialogVisible = false" class="footer-btn">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick, reactive } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { getTotalCredits, getCreditRecordList } from '@/api/credit'
import { getActivityList } from '@/api/activity'
import { getAnnouncementList } from '@/api/announcement'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const userStore = useUserStore()
const router = useRouter()

const totalHours = ref(0)
const totalPoints = ref(0)
const activityCount = ref(0)
const todoCount = ref(0)
const pendingProofCount = ref(0)
const pendingActivityCount = ref(0)
const announcements = ref([])
const activities = ref([])

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

const creditChart = ref(null)
const trendChart = ref(null)

const isAdmin = computed(() => userStore.role === 'ADMIN' || userStore.role === 'TEACHER')

const todoLabel = computed(() => {
  if (isAdmin.value) {
    return `待审核 ${todoCount.value}`
  }
  return `待审核 ${todoCount.value}`
})

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

const fetchData = async () => {
  try {
    if (!userStore.userId) {
      return
    }
    
    if (isAdmin.value) {
      await fetchAdminData()
    } else {
      await fetchStudentData()
    }
  } catch (error) {
    console.error('获取数据失败', error)
    ElMessage.error('获取数据失败')
  }
}

const fetchStudentData = async () => {
  const creditsRes = await getTotalCredits(userStore.userId)
  if (creditsRes) {
    totalHours.value = creditsRes.totalHours || 0
    totalPoints.value = creditsRes.totalPoints || 0
  }

  const creditRecordRes = await getCreditRecordList({ page: 1, size: 1, studentId: userStore.userId })
  if (creditRecordRes) {
    activityCount.value = creditRecordRes.total || 0
  }

  const announcementRes = await getAnnouncementList({ page: 1, size: 3, status: 'PUBLISHED' })
  if (announcementRes) {
    announcements.value = announcementRes.records || []
  }

  const activityRes = await getActivityList({ page: 1, size: 3, status: 'PUBLISHED' })
  if (activityRes) {
    activities.value = activityRes.records || []
  }

  pendingProofCount.value = 2
  pendingActivityCount.value = 1
  todoCount.value = pendingProofCount.value + pendingActivityCount.value
}

const fetchAdminData = async () => {
  const activityRes = await getActivityList({ page: 1, size: 1 })
  if (activityRes) {
    activityCount.value = activityRes.total || 0
  }

  const announcementRes = await getAnnouncementList({ page: 1, size: 3, status: 'PUBLISHED' })
  if (announcementRes) {
    announcements.value = announcementRes.records || []
  }

  totalHours.value = 3
  totalPoints.value = 2
  todoCount.value = 3
}

const viewAnnouncement = (item) => {
  ElMessage.info(item.content || item.title)
}

const viewActivity = (item) => {
  Object.assign(viewActivityForm, {
    title: item.title || '',
    content: item.content || '',
    startTime: item.startTime || '',
    endTime: item.endTime || '',
    location: item.location || '',
    creditHours: item.creditHours || 0,
    creditPoints: item.creditPoints || 0
  })
  viewDialogVisible.value = true
}

const goToProofs = () => {
  ElMessage.info('跳转到实践证明页面')
}

const goToActivities = () => {
  ElMessage.info('跳转到活动页面')
}

const initCharts = async () => {
  if (!isAdmin.value) return
  
  await nextTick()
  
  if (creditChart.value) {
    const creditChartInstance = echarts.init(creditChart.value)
    const creditOption = {
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      color: ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe'],
      series: [
        {
          name: '学分分布',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 20,
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: [
            { value: 30, name: '社会实践' },
            { value: 25, name: '志愿服务' },
            { value: 20, name: '学术活动' },
            { value: 15, name: '文体活动' },
            { value: 10, name: '其他' }
          ]
        }
      ]
    }
    creditChartInstance.setOption(creditOption)
  }
  
  if (trendChart.value) {
    const trendChartInstance = echarts.init(trendChart.value)
    const trendOption = {
      tooltip: { trigger: 'axis' },
      legend: { data: ['参与人数', '活动数量'], bottom: 10 },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '15%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['1月', '2月', '3月', '4月', '5月', '6月']
      },
      yAxis: { type: 'value' },
      color: ['#667eea', '#764ba2'],
      series: [
        {
          name: '参与人数',
          type: 'line',
          stack: 'Total',
          data: [120, 132, 101, 134, 90, 230],
          smooth: true,
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(102, 126, 234, 0.8)' },
              { offset: 1, color: 'rgba(102, 126, 234, 0.2)' }
            ])
          }
        },
        {
          name: '活动数量',
          type: 'line',
          stack: 'Total',
          data: [220, 182, 191, 234, 290, 330],
          smooth: true,
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(118, 75, 162, 0.8)' },
              { offset: 1, color: 'rgba(118, 75, 162, 0.2)' }
            ])
          }
        }
      ]
    }
    trendChartInstance.setOption(trendOption)
  }
}

onMounted(() => {
  fetchData()
  initCharts()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.data-card {
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
}

.data-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.data-card:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-content {
  display: flex;
  align-items: center;
  padding: 24px;
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 24px;
  color: #fff;
}

.hours-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.points-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.activity-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.todo-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.user-manage-icon {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.profile-icon {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.quick-action-card .card-value {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1.2;
  margin-bottom: 8px;
}

.quick-action-card .card-label {
  font-size: 13px;
  color: #666;
}

.card-info {
  flex: 1;
}

.card-value {
  font-size: 32px;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1;
  margin-bottom: 8px;
}

.card-label {
  font-size: 14px;
  color: #666;
}

.content-card {
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
}

.card-title .el-icon {
  margin-right: 8px;
  font-size: 18px;
  color: #667eea;
}

.announcement-list {
  padding: 8px 0;
}

.announcement-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;
}

.announcement-item:hover {
  background: #f8f9fa;
  padding-left: 12px;
}

.announcement-item:last-child {
  border-bottom: none;
}

.announcement-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.4;
}

.announcement-time {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
}

.announcement-time .el-icon {
  margin-right: 4px;
  font-size: 12px;
}

.activity-list {
  padding: 8px 0;
}

.activity-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s;
}

.activity-item:hover {
  background: #f8f9fa;
  padding-left: 12px;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-info {
  flex: 1;
}

.activity-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.4;
}

.activity-time {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
}

.activity-time .el-icon {
  margin-right: 4px;
  font-size: 12px;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}

.todo-row {
  margin-top: 20px;
}

.todo-item {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s;
  cursor: pointer;
}

.todo-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.todo-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 20px;
  color: #fff;
}

.proof-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.todo-info {
  flex: 1;
}

.todo-count {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1;
  margin-bottom: 4px;
}

.todo-label {
  font-size: 14px;
  color: #666;
}

.chart-container {
  padding: 20px;
}

.chart-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  text-align: center;
}

.chart {
  width: 100%;
  height: 300px;
  border-radius: 8px;
  overflow: hidden;
}

.quick-entry-item {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s;
  cursor: pointer;
}

.quick-entry-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  background: #fff;
}

.entry-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 20px;
  color: #fff;
}

.activity-entry {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.registration-entry {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.credit-entry {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.profile-entry {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.entry-info {
  flex: 1;
}

.entry-label {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 4px;
}

.entry-desc {
  font-size: 12px;
  color: #666;
}

.entry-arrow {
  font-size: 16px;
  color: #999;
  transition: all 0.3s;
}

.quick-entry-item:hover .entry-arrow {
  color: #667eea;
  transform: translateX(4px);
}
</style>
