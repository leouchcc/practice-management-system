<template>
  <div class="profile">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <el-button type="primary" size="large" @click="openChangePasswordDialog">修改密码</el-button>
        </div>
      </template>
      
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">
          {{ userInfo.username }}
        </el-descriptions-item>
        <el-descriptions-item label="真实姓名">
          {{ userInfo.realName }}
        </el-descriptions-item>
        <el-descriptions-item label="手机号">
          {{ userInfo.phone }}
        </el-descriptions-item>
        <el-descriptions-item label="邮箱">
          {{ userInfo.email }}
        </el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="getRoleType(userInfo.role)">{{ getRoleLabel(userInfo.role) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item :label="isStudent ? '学号' : '工号'">
          {{ userInfo.studentId || '未设置' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="changePasswordDialogVisible"
      title="修改密码"
      width="500px"
    >
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="changePasswordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="changePassword">确认修改</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import * as userApi from '@/api/user'

const userStore = useUserStore()
const userInfo = ref({
  username: '',
  realName: '',
  phone: '',
  email: '',
  role: '',
  studentId: ''
})

const changePasswordDialogVisible = ref(false)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordFormRef = ref()

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const isStudent = computed(() => userInfo.value.role === 'STUDENT')

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
  return typeMap[role] || 'info'
}

const loadUserInfo = () => {
  userInfo.value = {
    username: userStore.username || '',
    realName: userStore.userInfo?.realName || '',
    phone: userStore.userInfo?.phone || '',
    email: userStore.userInfo?.email || '',
    role: userStore.role || '',
    studentId: userStore.userInfo?.studentId || ''
  }
}

const openChangePasswordDialog = () => {
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  if (passwordFormRef.value) {
    passwordFormRef.value.resetFields()
  }
  changePasswordDialogVisible.value = true
}

const changePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await userApi.changePassword({
          oldPassword: passwordForm.value.oldPassword,
          newPassword: passwordForm.value.newPassword
        })
        
        // 如果执行到这里，说明请求成功（因为拦截器已经处理了错误情况）
        ElMessage.success('密码修改成功！请重新登录')
        changePasswordDialogVisible.value = false
        // 退出登录
        userStore.logout()
      } catch (error) {
        // 错误已经在拦截器中处理，这里不需要再显示
        console.error('修改密码失败:', error)
      }
    }
  })
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}
</style>