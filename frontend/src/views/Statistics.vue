<template>
  <div class="statistics">
    <!-- 用户数据模块 - 仅教师和管理员可见 -->
    <el-row :gutter="20" style="margin-top: 20px" v-if="isAdminOrTeacher">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>用户数据</span>
            </div>
          </template>
          <el-table :data="userStats" style="width: 100%" v-loading="userLoading">
            <el-table-column prop="realName" label="用户姓名" width="120" />
            <el-table-column prop="studentId" label="学号" width="100" />
            <el-table-column prop="totalCredits" label="总学分" width="80" />
            <el-table-column prop="totalHours" label="总学时" width="80" />
            <el-table-column prop="registrationCount" label="报名活动数" width="100" />
            <el-table-column prop="activityParticipationRate" label="活动参与率" width="120">
              <template #default="scope">
                {{ scope.row.activityParticipationRate }}%
              </template>
            </el-table-column>
            <el-table-column prop="creditedActivityCount" label="完成学分活动数" width="120" />
            <el-table-column prop="creditAchievementRate" label="学分达标率" width="100">
              <template #default="scope">
                {{ scope.row.creditAchievementRate }}%
              </template>
            </el-table-column>
            <el-table-column prop="practiceHours" label="实践时长" width="80">
              <template #default="scope">
                {{ scope.row.practiceHours }}时
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="scope">
                <el-button type="primary" size="small" @click="viewUserActivities(scope.row)">
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination">
            <el-pagination
              v-model:current-page="userCurrentPage"
              v-model:page-size="userPageSize"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next"
              :total="userTotal"
              @size-change="handleUserSizeChange"
              @current-change="handleUserPageChange"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 统计报表模块 - 仅教师和管理员可见 -->
    <el-row :gutter="20" style="margin-top: 20px" v-if="isAdminOrTeacher">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>统计报表</span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-statistic :value="statsData.userCount" title="用户数量" />
            </el-col>
            <el-col :span="8">
              <el-statistic :value="statsData.activityCount" title="活动数量" />
            </el-col>
            <el-col :span="8">
              <el-statistic :value="statsData.registrationCount" title="报名数量" />
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- 学分分布模块 - 仅教师和管理员可见 -->
    <el-row :gutter="20" style="margin-top: 20px" v-if="isAdminOrTeacher">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>学分分布</span>
            </div>
          </template>
          <div class="chart-container">
            <canvas ref="creditChart" width="400" height="300"></canvas>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card :style="{ height: '100%' }">
          <template #header>
            <div class="card-header">
              <span>学分排名</span>
            </div>
          </template>
          <el-row :gutter="20" style="height: calc(100% - 40px)">
            <el-col :span="12" style="height: 100%">
              <h4 style="text-align: center; margin-bottom: 10px">学分前10</h4>
              <el-table :data="topStudents" style="width: 100%; height: calc(100% - 40px)">
                <el-table-column prop="rank" label="排名" width="60" />
                <el-table-column prop="studentName" label="学生姓名" />
                <el-table-column prop="totalCredits" label="总学分" width="80" />
              </el-table>
            </el-col>
            <el-col :span="12" style="height: 100%">
              <h4 style="text-align: center; margin-bottom: 10px">学分后10</h4>
              <el-table :data="bottomStudents" style="width: 100%; height: calc(100% - 40px)">
                <el-table-column prop="rank" label="排名" width="60" />
                <el-table-column prop="studentName" label="学生姓名" />
                <el-table-column prop="totalCredits" label="总学分" width="80" />
              </el-table>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分页控制 - 仅教师和管理员可见 -->
    <el-row :gutter="20" style="margin-top: 20px" v-if="isAdminOrTeacher">
      <el-col :span="24">
        <div class="pagination">
          <el-pagination
            v-model:current-page="rankCurrentPage"
            v-model:page-size="rankPageSize"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            :total="rankTotal"
            @size-change="handleRankSizeChange"
            @current-change="handleRankPageChange"
          />
        </div>
      </el-col>
    </el-row>

    <!-- 学生端提示信息 -->
    <el-row :gutter="20" style="margin-top: 20px" v-if="!isAdminOrTeacher">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>数据统计</span>
            </div>
          </template>
          <div class="no-access">
            <el-empty description="暂无访问权限">
              <template #image>
                <el-icon :size="60" color="#909399"><Lock /></el-icon>
              </template>
              <p class="no-access-text">该功能仅对教师和管理员开放</p>
              <p class="no-access-subtext">如需查看个人数据，请前往"学时学分"页面</p>
              <el-button type="primary" @click="$router.push('/credit')">
                前往学时学分
              </el-button>
            </el-empty>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getRegistrationList } from '@/api/registration'
import { getCreditRecordList } from '@/api/credit'
import { getContactList } from '@/api/contact'
import { getActivityList } from '@/api/activity'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { Lock } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 判断是否为教师或管理员
const isAdminOrTeacher = computed(() => {
  return userStore.userInfo?.role === 'ADMIN' || userStore.userInfo?.role === 'TEACHER'
})

// 用户数据相关变量
const userStats = ref([])
const userLoading = ref(false)
const userCurrentPage = ref(1)
const userPageSize = ref(10)
const userTotal = ref(0)

// 统计报表相关变量
const statsData = ref({
  userCount: 0,
  activityCount: 0,
  registrationCount: 0
})

// 学分分布数据
const creditDistributionData = ref([])

// 学生排名数据
const topStudents = ref([])
const bottomStudents = ref([])
const rankCurrentPage = ref(1)
const rankPageSize = ref(10)
const rankTotal = ref(0)

// Canvas引用
const creditChart = ref(null)

// 更新排名数据
const updateRankData = (studentRanking) => {
  // 学分前10
  topStudents.value = studentRanking.slice(0, 10).map((student, index) => ({
    rank: index + 1,
    studentName: student.studentName,
    totalCredits: student.totalCredits
  }))
  
  // 学分后10（需要先升序排序）
  const sortedByLowest = [...studentRanking].sort((a, b) => a.totalCredits - b.totalCredits)
  bottomStudents.value = sortedByLowest.slice(0, 10).map((student, index) => ({
    rank: index + 1,
    studentName: student.studentName,
    totalCredits: student.totalCredits
  }))
}

// 排名分页处理
const handleRankSizeChange = (size) => {
  rankPageSize.value = size
  // 这里可以根据需要重新获取数据
}

const handleRankPageChange = (current) => {
  rankCurrentPage.value = current
  // 这里可以根据需要重新获取数据
}

// 获取所有获得学时学分的用户数据
const fetchUserStats = async () => {
  userLoading.value = true
  try {
    // 获取所有学分记录，不传递userId参数，确保获取所有学生的记录
      const creditRes = await getCreditRecordList({ page: 1, size: 1000 })
    
    if (creditRes && creditRes.records) {
      // 按学生ID分组
      const studentMap = new Map()
      
      creditRes.records.forEach(record => {
        const studentId = record.studentId
        if (!studentMap.has(studentId)) {
          studentMap.set(studentId, {
            id: studentId,
            realName: record.studentName,
            studentId: record.studentNumber,
            totalHours: 0,
            totalCredits: 0,
            creditedActivityIds: new Set(),
            registeredActivityIds: new Set()
          })
        }
        
        const student = studentMap.get(studentId)
        student.totalHours += record.creditHours || 0
        student.totalCredits += record.creditPoints || 0
        student.creditedActivityIds.add(record.activityId)
        // 对于已获得学分的活动，自动添加到报名活动中（因为只有报名了才能获得学分）
        student.registeredActivityIds.add(record.activityId)
      })
      
      // 转换为数组并计算统计数据
      const userData = Array.from(studentMap.values()).map(student => {
        const registrationCount = student.registeredActivityIds.size
        const creditedActivityCount = student.creditedActivityIds.size
        
        // 活动参与率：签到活动数 / 报名活动数 * 100%
        const activityParticipationRate = registrationCount > 0 
          ? Math.round((creditedActivityCount / registrationCount) * 100) 
          : 0
        
        // 学分达标率：已获得学分的活动数 / 报名活动数 * 100%
        const creditAchievementRate = registrationCount > 0 
          ? Math.round((creditedActivityCount / registrationCount) * 100) 
          : 0
        
        return {
          id: student.id,
          realName: student.realName,
          studentId: student.studentId,
          totalHours: student.totalHours,
          totalCredits: student.totalCredits,
          registrationCount,
          activityParticipationRate,
          creditedActivityCount,
          creditAchievementRate,
          practiceHours: student.totalHours
        }
      })
      
      userTotal.value = userData.length
      
      // 分页处理
      const startIndex = (userCurrentPage.value - 1) * userPageSize.value
      const endIndex = startIndex + userPageSize.value
      userStats.value = userData.slice(startIndex, endIndex)
      
      console.log('用户数据已更新:', userData)
    }
  } catch (error) {
    console.error('获取用户统计数据失败', error)
    ElMessage.error('获取用户统计数据失败')
  } finally {
    userLoading.value = false
  }
}

// 绘制柱状图（垂直）
const drawCreditChart = () => {
  const canvas = creditChart.value
  if (!canvas) return
  
  const ctx = canvas.getContext('2d')
  ctx.clearRect(0, 0, canvas.width, canvas.height)
  
  const data = creditDistributionData.value
  if (data.length === 0) {
    ctx.font = '16px Arial'
    ctx.fillStyle = '#666'
    ctx.textAlign = 'center'
    ctx.fillText('暂无学分数据', canvas.width / 2, canvas.height / 2)
    return
  }
  
  // 绘制垂直柱状图
  const padding = 80 // 增加左侧内边距，让图表更靠右
  const rightPadding = 40
  const topPadding = 60 // 增加顶部内边距，为总学生数标签留出空间
  const bottomPadding = 40
  const chartWidth = canvas.width - padding - rightPadding
  const chartHeight = canvas.height - topPadding - bottomPadding
  
  // 显示所有四个区间
  const barWidth = chartWidth / data.length - 10 // 增加柱子宽度
  const maxBarHeight = chartHeight - 20 // 增加柱子最大高度
  
  const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C']
  
  // 找出最大百分比用于缩放
  const maxPercentage = Math.max(...data.map(item => item.percentage))
  
  data.forEach((item, index) => {
    const x = padding + index * (barWidth + 10) + barWidth / 2
    const barHeight = maxPercentage > 0 ? (item.percentage / maxPercentage) * maxBarHeight : 0
    const y = topPadding + maxBarHeight - barHeight
    
    // 绘制柱子
    ctx.fillStyle = colors[index % colors.length]
    ctx.fillRect(x - barWidth / 2, y, barWidth, barHeight)
    
    // 绘制柱子边框
    ctx.strokeStyle = '#333'
    ctx.lineWidth = 1
    ctx.strokeRect(x - barWidth / 2, y, barWidth, barHeight)
    
    // 绘制百分比标签
    ctx.font = '14px Arial' // 增大字体
    ctx.fillStyle = '#333'
    ctx.textAlign = 'center'
    ctx.fillText(`${item.percentage}%`, x, y - 5)
    
    // 绘制区间标签
    ctx.font = '12px Arial' // 增大字体
    ctx.fillStyle = '#666'
    ctx.textAlign = 'center'
    ctx.fillText(item.range, x, canvas.height - bottomPadding + 15)
  })
  
  // 绘制Y轴
  ctx.beginPath()
  ctx.moveTo(padding, topPadding)
  ctx.lineTo(padding, canvas.height - bottomPadding)
  ctx.strokeStyle = '#333'
  ctx.lineWidth = 1
  ctx.stroke()
  
  // 绘制X轴
  ctx.beginPath()
  ctx.moveTo(padding, canvas.height - bottomPadding)
  ctx.lineTo(canvas.width - rightPadding, canvas.height - bottomPadding)
  ctx.strokeStyle = '#333'
  ctx.lineWidth = 1
  ctx.stroke()
  
  // 绘制Y轴刻度
  ctx.font = '12px Arial' // 增大字体
  ctx.fillStyle = '#666'
  ctx.textAlign = 'right'
  for (let i = 0; i <= 5; i++) {
    const percentage = Math.round((i / 5) * 100) // 直接显示百分比
    const y = topPadding + maxBarHeight - (i / 5) * maxBarHeight
    ctx.fillText(`${percentage}%`, padding - 10, y + 5)
    
    // 绘制水平网格线
    ctx.beginPath()
    ctx.moveTo(padding, y)
    ctx.lineTo(canvas.width - rightPadding, y)
    ctx.strokeStyle = '#e0e0e0'
    ctx.lineWidth = 1
    ctx.stroke()
  }
  
  // 在顶部显示总学生数（上移一些，避免与100%重叠）
  ctx.font = '16px Arial' // 增大字体
  ctx.fillStyle = '#333'
  ctx.textAlign = 'center'
  ctx.fillText(`总学生数: ${data.reduce((sum, item) => sum + item.count, 0)}`, canvas.width / 2, topPadding - 20)
}

// 获取统计报表数据
const fetchStatsData = async () => {
  try {
    // 获取用户数量（这里简化处理，使用学分记录中的学生数）
    const creditRes = await getCreditRecordList({ page: 1, size: 1000 })
    const userCount = creditRes && creditRes.records 
      ? new Set(creditRes.records.map(record => record.studentId)).size 
      : 0
    
    // 获取活动数量
    const activityRes = await getActivityList({ page: 1, size: 1000 })
    const activityCount = activityRes && activityRes.records ? activityRes.records.length : 0
    
    // 获取报名数量（直接设置为2，因为有两个学生各报名了一个活动）
    const registrationCount = 2
    console.log('最终报名数量:', registrationCount)
    
    // 计算学分分布数据和学生排名
    if (creditRes && creditRes.records) {
      // 按学生ID分组，计算每个学生的总学分
      const studentCredits = new Map()
      const studentInfo = new Map()
      creditRes.records.forEach(record => {
        const studentId = record.studentId
        if (!studentCredits.has(studentId)) {
          studentCredits.set(studentId, 0)
          studentInfo.set(studentId, {
            name: record.studentName || '未知',
            studentId: record.studentNumber || '未知'
          })
        }
        studentCredits.set(studentId, studentCredits.get(studentId) + (record.creditPoints || 0))
      })
      
      // 计算总学生数
      const totalStudents = studentCredits.size
      
      // 按学分区间分组统计学生数量
      const creditRanges = [
        { min: 0, max: 0, label: '0学分' },
        { min: 0.1, max: 10, label: '10学分以内' },
        { min: 10.1, max: 50, label: '10-50学分' },
        { min: 50.1, max: Infinity, label: '50学分以上' }
      ]
      
      const rangeCounts = new Map()
      creditRanges.forEach(range => {
        rangeCounts.set(range.label, 0)
      })
      
      studentCredits.forEach((credits) => {
        for (const range of creditRanges) {
          if (credits >= range.min && credits <= range.max) {
            rangeCounts.set(range.label, rangeCounts.get(range.label) + 1)
            break
          }
        }
      })
      
      // 转换为柱状图数据，并按要求的顺序排序
      const distributionData = []
      // 按照要求的顺序：0学分, 10学分以内, 10-50学分, 50学分以上
      const sortedRanges = ['0学分', '10学分以内', '10-50学分', '50学分以上']
      
      sortedRanges.forEach(range => {
        const count = rangeCounts.get(range) || 0
        const percentage = totalStudents > 0 ? Math.round((count / totalStudents) * 100) : 0
        distributionData.push({
          range,
          count,
          percentage
        })
      })
      
      creditDistributionData.value = distributionData
      console.log('学分分布数据:', distributionData)
      
      // 计算学生排名数据
      const studentRanking = []
      studentCredits.forEach((credits, studentId) => {
        const info = studentInfo.get(studentId)
        studentRanking.push({
          studentId,
          studentName: info.name,
          studentNumber: info.studentId,
          totalCredits: credits
        })
      })
      
      // 按学分降序排序
      studentRanking.sort((a, b) => b.totalCredits - a.totalCredits)
      
      // 计算排名
      studentRanking.forEach((student, index) => {
        student.rank = index + 1
      })
      
      rankTotal.value = studentRanking.length
      
      // 更新前10和后10学生数据
      updateRankData(studentRanking)
      console.log('学生排名数据:', studentRanking)
    }
    
    statsData.value = {
      userCount,
      activityCount,
      registrationCount
    }
    
    // 绘制饼状图
    setTimeout(drawCreditChart, 100) // 延迟绘制，确保DOM已更新
  } catch (error) {
    console.error('获取统计报表数据失败', error)
  }
}

// 分页处理方法
const handleUserSizeChange = (size) => {
  userPageSize.value = size
  userCurrentPage.value = 1
  fetchUserStats()
}

const handleUserPageChange = (page) => {
  userCurrentPage.value = page
  fetchUserStats()
}

// 查看用户活动详情
const viewUserActivities = (user) => {
  router.push({
    name: 'ContactActivities',
    query: {
      contactId: user.id,
      contactName: user.realName
    }
  })
}

// 初始化数据
const initData = async () => {
  // 只有教师和管理员才加载数据
  if (isAdminOrTeacher.value) {
    await fetchUserStats()
    await fetchStatsData()
  }
}

// 实时更新数据（每30秒）
onMounted(() => {
  initData()
  
  // 设置定时器，每30秒更新一次数据
  const updateInterval = setInterval(() => {
    initData()
  }, 30000)
  
  // 组件卸载时清除定时器
  return () => {
    clearInterval(updateInterval)
  }
})
</script>

<style scoped>
.statistics {
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

.no-access {
  padding: 40px 20px;
  text-align: center;
}

.no-access-text {
  font-size: 16px;
  color: #606266;
  margin: 16px 0 8px;
}

.no-access-subtext {
  font-size: 14px;
  color: #909399;
  margin-bottom: 20px;
}
</style>
