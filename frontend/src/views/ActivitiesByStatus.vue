<template>
  <div class="activities-by-status">
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
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewDetail(scope.row)">
              查看详情
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getActivityList } from '@/api/activity'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const activityList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const statusFilter = computed(() => route.query.status)
const statusNameFilter = computed(() => route.query.statusName)

const pageTitle = computed(() => {
  if (statusNameFilter.value) {
    return `活动状态分布 - ${statusNameFilter.value}`
  }
  return '活动状态分布'
})

const loadActivities = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
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

const viewDetail = (activity) => {
  router.push({
    name: 'ActivityDetail',
    params: { id: activity.id }
  })
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
.activities-by-status {
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
