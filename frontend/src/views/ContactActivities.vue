<template>
  <div class="contact-activities">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ pageTitle }}</span>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>
      
      <div class="contact-info" v-if="contactInfo">
        <el-descriptions :column="4" border>
          <el-descriptions-item label="联系人姓名">{{ contactInfo.realName }}</el-descriptions-item>
          <el-descriptions-item label="学号">{{ contactInfo.studentId }}</el-descriptions-item>
          <el-descriptions-item label="总学时">{{ contactInfo.totalHours }}</el-descriptions-item>
          <el-descriptions-item label="总学分">{{ contactInfo.totalCredits }}</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <el-table :data="activityList" style="width: 100%; margin-top: 20px" v-loading="loading">
        <el-table-column prop="activityTitle" label="活动名称" width="200" />
        <el-table-column prop="activityStartTime" label="活动开始时间" width="180" />
        <el-table-column prop="activityEndTime" label="活动结束时间" width="180" />
        <el-table-column prop="registrationStatus" label="报名状态" width="100">
          <template #default="scope">
            <el-tag :type="getRegistrationStatusType(scope.row.registrationStatus)">
              {{ getRegistrationStatusName(scope.row.registrationStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="creditHours" label="获得学时" width="100" />
        <el-table-column prop="creditPoints" label="获得学分" width="100" />
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getCreditRecordList } from '@/api/credit'
import { getRegistrationList } from '@/api/registration'
import { getUserList } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const activityList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const contactInfo = ref(null)

const contactId = computed(() => route.query.contactId)
const contactName = computed(() => route.query.contactName)

const pageTitle = computed(() => {
  if (contactName.value) {
    return `${contactName.value} 的活动记录`
  }
  return '联系人活动记录'
})

const loadActivities = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    if (contactId.value) {
      params.userId = contactId.value
    }
    
    const creditResponse = await getCreditRecordList(params)
    const registrationResponse = await getRegistrationList(params)
    
    // 获取用户信息以获取学号
    let userInfo = null
    if (contactId.value) {
      const userResponse = await getUserList({ page: 1, size: 1000 })
      if (userResponse && userResponse.records) {
        userInfo = userResponse.records.find(user => user.id === parseInt(contactId.value))
      }
    }
    
    if (creditResponse && creditResponse.records) {
      total.value = creditResponse.total || 0
      
      const registrationMap = new Map()
      if (registrationResponse && registrationResponse.records) {
        registrationResponse.records.forEach(reg => {
          registrationMap.set(reg.activityId, reg)
        })
      }
      
      activityList.value = creditResponse.records.map(record => {
        const registration = registrationMap.get(record.activityId)
        return {
          id: record.id,
          activityId: record.activityId,
          activityTitle: record.activityTitle || '-',
          activityStartTime: record.activityStartTime || '-',
          activityEndTime: record.activityEndTime || '-',
          registrationStatus: 'COMPLETED', // 获得学分的活动都显示为已完成
          creditHours: record.creditHours || 0,
          creditPoints: record.creditPoints || 0,
          evaluationScore: record.evaluationScore
        }
      })
      
      let totalHours = 0
      let totalCredits = 0
      creditResponse.records.forEach(record => {
        totalHours += record.creditHours || 0
        totalCredits += record.creditPoints || 0
      })
      
      contactInfo.value = {
        realName: contactName.value,
        studentId: userInfo ? userInfo.studentId : '-',
        totalHours,
        totalCredits
      }
    }
  } catch (error) {
    ElMessage.error('获取活动记录失败')
    console.error('获取活动记录失败:', error)
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.back()
}

const getRegistrationStatusName = (status) => {
  const statusMap = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝',
    'CANCELLED': '已取消',
    'COMPLETED': '已完成'
  }
  return statusMap[status] || status
}

const getRegistrationStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'CANCELLED': 'info',
    'COMPLETED': 'success'
  }
  return typeMap[status] || 'default'
}

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.contact-activities {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.contact-info {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
