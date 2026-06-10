<template>
  <div class="user-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span>{{ isTeacher ? '联系人列表' : (showContacts ? '联系人列表' : '用户列表') }}</span>
            <el-switch
              v-if="isAdmin"
              v-model="showContacts"
              active-text="我的联系人"
              inactive-text="所有用户"
              style="margin-left: 20px"
              @change="handleViewChange"
            />
          </div>
          <el-button type="primary" @click="showSearchDialog">
            <el-icon><Plus /></el-icon>
            添加联系人
          </el-button>
        </div>
      </template>
      
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用户名、真实姓名或学号"
          style="width: 300px; margin-right: 10px"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
      
      <el-table :data="userList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="用户ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="studentId" label="学号/工号" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="scope">
            <el-tag :type="getRoleType(scope.row.role)">
              {{ getRoleLabel(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <template v-if="isAdmin">
          <el-table-column prop="status" label="状态" width="80">
            <template #default="scope">
              <el-switch
                v-model="scope.row.status"
                :active-value="1"
                :inactive-value="0"
                @change="(val) => handleStatusChange(scope.row, val)"
              />
            </template>
          </el-table-column>
        </template>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <template v-if="isAdmin && !showContacts">
              <el-button type="primary" size="small" @click="handleEditUser(scope.row)" style="margin-right: 5px">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="danger" size="small" @click="handleDeleteUser(scope.row.id)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
            <template v-else-if="isTeacher || (isAdmin && showContacts)">
              <el-button type="danger" size="small" @click="handleDeleteContact(scope.row.id)">
                <el-icon><Delete /></el-icon>
                删除联系人
              </el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
      
      <template v-if="isAdmin">
        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="loadUserList"
            @current-change="loadUserList"
          />
        </div>
      </template>
    </el-card>
    
    <!-- 搜索用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="添加联系人"
      width="500px"
    >
      <el-form :model="searchForm" :rules="searchRules" ref="searchFormRef" label-width="80px">
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="searchForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="学号/工号" prop="studentId">
          <el-input v-model="searchForm.studentId" placeholder="请输入学号/工号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="sendContactRequest">添加联系人</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const isAdmin = userStore.role === 'ADMIN'
const isTeacher = userStore.role === 'TEACHER'

const showContacts = ref(false)

const userList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')

const dialogVisible = ref(false)
const searchForm = reactive({
  realName: '',
  studentId: ''
})

const searchFormRef = ref(null)

const isMounted = ref(true)

const searchRules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  studentId: [{ required: true, message: '请输入学号/工号', trigger: 'blur' }]
}

const getRoleLabel = (role) => {
  const roleMap = {
    'STUDENT': '学生',
    'TEACHER': '教师',
    'ADMIN': '管理员'
  }
  return roleMap[role] || role
}

const getRoleType = (role) => {
  const typeMap = {
    'STUDENT': 'info',
    'TEACHER': 'warning',
    'ADMIN': 'danger'
  }
  return typeMap[role] || 'default'
}

const loadUserList = async () => {
  loading.value = true
  try {
    let response
    if (isTeacher || showContacts.value) {
      // 教师端或管理员查看联系人列表
      response = await request({
        url: '/contact/list',
        method: 'get'
      })
      
      if (isMounted.value && response) {
        userList.value = response
        total.value = response.length
      }
    } else {
      // 管理员端获取所有用户
      response = await request({
        url: '/user/list',
        method: 'get',
        params: {
          page: currentPage.value,
          size: pageSize.value,
          keyword: searchKeyword.value
        }
      })
      
      if (isMounted.value && response) {
        userList.value = response.records
        total.value = response.total
      }
    }
  } catch (error) {
    if (isMounted.value) {
      ElMessage.error('获取用户列表失败')
      console.error('获取用户列表失败:', error)
    }
  } finally {
    if (isMounted.value) {
      loading.value = false
    }
  }
}

const handleViewChange = () => {
  currentPage.value = 1
  loadUserList()
}

const handleSearch = () => {
  if (isAdmin) {
    currentPage.value = 1
    loadUserList()
  } else {
    // 教师端联系人列表暂时不支持搜索
    ElMessage.info('联系人列表暂时不支持搜索')
  }
}

const showSearchDialog = () => {
  Object.assign(searchForm, {
    realName: '',
    studentId: ''
  })
  dialogVisible.value = true
}

const sendContactRequest = async () => {
  if (!searchFormRef.value) return
  
  await searchFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await request({
          url: '/contact/request',
          method: 'post',
          data: searchForm
        })
        
        if (isMounted.value) {
          ElMessage.success('联系人添加成功')
          dialogVisible.value = false
          loadUserList()
        }
      } catch (error) {
        if (isMounted.value) {
          ElMessage.error('添加联系人失败，请稍后重试')
          console.error('添加联系人失败:', error)
        }
      }
    }
  })
}

const handleDeleteContact = async (contactId) => {
  try {
    await ElMessageBox.confirm('确定要删除该联系人吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request({
      url: `/contact/${contactId}`,
      method: 'delete'
    })
    
    if (isMounted.value) {
      ElMessage.success('联系人已删除')
      loadUserList()
    }
  } catch (error) {
    if (isMounted.value && error !== 'cancel') {
      ElMessage.error('删除联系人失败')
      console.error('删除联系人失败:', error)
    }
  }
}

const handleStatusChange = async (user, newStatus) => {
  try {
    await request({
      url: '/user',
      method: 'put',
      data: {
        id: user.id,
        username: user.username,
        realName: user.realName,
        studentId: user.studentId,
        phone: user.phone,
        email: user.email,
        role: user.role,
        status: newStatus
      }
    })
    if (isMounted.value) {
      ElMessage.success('用户状态已更新')
      loadUserList()
    }
  } catch (error) {
    if (isMounted.value) {
      ElMessage.error('更新用户状态失败')
      console.error('更新用户状态失败:', error)
    }
  }
}

const handleEditUser = (user) => {
  if (isMounted.value) {
    ElMessage.info('编辑用户功能暂未实现')
  }
}

const handleDeleteUser = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request({
      url: `/user/${id}`,
      method: 'delete'
    })
    
    if (isMounted.value) {
      ElMessage.success('用户已删除')
      loadUserList()
    }
  } catch (error) {
    if (isMounted.value && error !== 'cancel') {
      ElMessage.error('删除用户失败')
      console.error('删除用户失败:', error)
    }
  }
}

onMounted(() => {
  loadUserList()
})

onUnmounted(() => {
  isMounted.value = false
})
</script>

<style scoped>
.user-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
}

.search-bar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>