<template>
  <div class="credit">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学时学分</span>
          <el-button type="primary" @click="handleExport" v-if="canExport">导出学分证明</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="学年">
          <el-input v-model="searchForm.academicYear" placeholder="请输入学年" clearable />
        </el-form-item>
        <el-form-item label="学期">
          <el-input v-model="searchForm.semester" placeholder="请输入学期" clearable />
        </el-form-item>
        <el-form-item label="最小学分">
          <el-input-number v-model="searchForm.minCreditPoints" :min="0" :precision="1" :step="0.5" placeholder="最小学分" clearable style="width: 120px" />
        </el-form-item>
        <el-form-item label="最大学分">
          <el-input-number v-model="searchForm.maxCreditPoints" :min="0" :precision="1" :step="0.5" placeholder="最大学分" clearable style="width: 120px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchCreditRecords">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="creditRecords" style="width: 100%">
        <el-table-column prop="activityTitle" label="活动名称" />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="studentNumber" label="学号" width="150" />
        <el-table-column prop="creditHours" label="学时" width="100" />
        <el-table-column prop="creditPoints" label="学分" width="100" />
        <el-table-column prop="academicYear" label="学年" width="120" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="createTime" label="记录时间" width="180" />
      </el-table>
      
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        @current-change="fetchCreditRecords"
        @size-change="fetchCreditRecords"
        layout="total, sizes, prev, pager, next, jumper"
      />
      
      <el-divider />
      
      <el-descriptions title="学时统计" :column="3" border>
        <el-descriptions-item label="总学时">{{ totalHours }}</el-descriptions-item>
        <el-descriptions-item label="总学分">{{ totalPoints }}</el-descriptions-item>
        <el-descriptions-item label="活动数量">{{ creditRecords.length }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getCreditRecordList } from '@/api/credit'
import { ElMessage } from 'element-plus'
import * as XLSX from 'xlsx'

const userStore = useUserStore()
const creditRecords = ref([])

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const searchForm = reactive({
  academicYear: '',
  semester: '',
  minCreditPoints: undefined,
  maxCreditPoints: undefined
})

const canExport = computed(() => {
  const role = userStore.role
  return role === 'STUDENT' || role === 'TEACHER' || role === 'ADMIN'
})

const totalHours = computed(() => {
  return creditRecords.value.reduce((sum, record) => sum + (record.creditHours || 0), 0)
})

const totalPoints = computed(() => {
  return creditRecords.value.reduce((sum, record) => sum + (record.creditPoints || 0), 0)
})

const fetchCreditRecords = async () => {
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      userId: userStore.userId,
      ...searchForm
    }
    
    const res = await getCreditRecordList(params)
    creditRecords.value = res.records
    pagination.total = res.total
  } catch (error) {
    ElMessage.error('获取学时记录失败')
  }
}

const resetSearch = () => {
  Object.assign(searchForm, {
    academicYear: '',
    semester: '',
    minCreditPoints: undefined,
    maxCreditPoints: undefined
  })
  pagination.page = 1
  fetchCreditRecords()
}

const handleExport = async () => {
  const role = userStore.role
  const userId = userStore.userId
  
  console.log('开始导出，角色:', role, '用户ID:', userId)
  
  try {
    // 检查是否有学分记录
    if (creditRecords.value.length === 0) {
      console.log('没有学分记录')
      if (role === 'STUDENT') {
        ElMessage.info('暂无该学生学分活动信息')
      } else if (role === 'TEACHER') {
        ElMessage.info('暂无联系人学分活动信息')
      } else if (role === 'ADMIN') {
        ElMessage.info('暂无学生学分活动信息')
      }
      return
    }
    
    // 准备导出数据
    const exportData = creditRecords.value.map(record => ({
      '学号': record.studentNumber || '',
      '姓名': record.studentName || '',
      '活动名称': record.activityTitle || '',
      '学时': record.creditHours || 0,
      '学分': record.creditPoints || 0,
      '学年': record.academicYear || '',
      '学期': record.semester || '',
      '记录时间': record.createTime ? new Date(record.createTime).toLocaleString() : ''
    }))
    
    // 创建工作簿和工作表
    const wb = XLSX.utils.book_new()
    const ws = XLSX.utils.json_to_sheet(exportData)
    
    // 设置文件名
    let fileName = '学分证明.xlsx'
    if (role === 'STUDENT') {
      fileName = '个人学分证明.xlsx'
    } else if (role === 'TEACHER') {
      fileName = '联系人学分详情.xlsx'
    } else if (role === 'ADMIN') {
      fileName = '所有学生学分详情.xlsx'
    }
    
    // 添加工作表到工作簿
    XLSX.utils.book_append_sheet(wb, ws, '学分记录')
    
    // 导出文件
    XLSX.writeFile(wb, fileName)
    
    console.log('导出成功')
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    if (role === 'STUDENT') {
      ElMessage.error('导出个人学分证明失败')
    } else if (role === 'TEACHER') {
      ElMessage.error('导出联系人学分详情失败')
    } else if (role === 'ADMIN') {
      ElMessage.error('导出所有学生学分详情失败')
    } else {
      ElMessage.error('导出失败')
    }
  }
}

onMounted(() => {
  fetchCreditRecords()
})
</script>

<style scoped>
.credit {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

.el-divider {
  margin: 30px 0;
}
</style>
