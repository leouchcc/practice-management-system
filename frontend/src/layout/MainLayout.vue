<template>
  <el-container class="main-layout">
    <el-aside width="240px">
      <div class="logo">
        <h3 class="logo-title">实践活动管理系统</h3>
        <p class="logo-subtitle">Student Activity System</p>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        class="main-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span class="menu-text">首页</span>
        </el-menu-item>
        <el-menu-item index="/activity">
          <el-icon><Calendar /></el-icon>
          <span class="menu-text">活动详情</span>
        </el-menu-item>
        <el-menu-item index="/registration">
          <el-icon><Document /></el-icon>
          <span class="menu-text">{{ isAdmin ? '活动管理' : '我的活动' }}</span>
        </el-menu-item>
        <el-menu-item index="/credit">
          <el-icon><TrendCharts /></el-icon>
          <span class="menu-text">学时学分</span>
        </el-menu-item>
        <el-menu-item index="/announcement">
          <el-icon><Bell /></el-icon>
          <span class="menu-text">公告通知</span>
        </el-menu-item>
        <el-menu-item index="/statistics" v-if="isAdmin">
          <el-icon><DataAnalysis /></el-icon>
          <span class="menu-text">数据统计</span>
        </el-menu-item>
        <el-menu-item index="/user" v-if="isAdmin">
          <el-icon><User /></el-icon>
          <span class="menu-text">用户管理</span>
        </el-menu-item>
        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <span class="menu-text">个人信息</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="header-left">
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <div class="notification-wrapper">
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge" :size="'large'">
              <el-icon @click="showNotifications" class="notification-icon" :size="40"><Bell /></el-icon>
            </el-badge>
          </div>
          <el-dropdown @command="handleCommand" class="user-dropdown">
            <div class="user-info">
              <div class="user-avatar">
                <el-icon><User /></el-icon>
              </div>
              <div class="user-details">
                <div class="user-name">刘海鸿</div>
                <div class="user-role">{{ roleLabel }}</div>
              </div>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="dropdown-menu">
                <el-dropdown-item command="logout" class="dropdown-item">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUnreadCount, getSuggestionUnreadCount } from '@/api/notification'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = ref(route.path)
const unreadCount = ref(0)

const isAdmin = computed(() => userStore.role === 'ADMIN' || userStore.role === 'TEACHER')

const roleLabel = computed(() => {
  if (userStore.role === 'ADMIN') return '管理员'
  if (userStore.role === 'TEACHER') return '教师'
  return '学生'
})

const pageTitle = computed(() => {
  if (route.name === 'Activity') {
    return isAdmin.value ? '活动发布' : '活动详情'
  } else if (route.name === 'Registration') {
    return isAdmin.value ? '活动管理' : '我的活动'
  }
  return route.meta.title || '首页'
})

watch(() => route.path, (newPath) => {
  activeMenu.value = newPath
})

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('退出成功')
    router.push('/login')
  }
}

const showNotifications = () => {
  router.push('/notification')
}

const fetchUnreadCount = async () => {
  try {
    console.log('fetchUnreadCount called, userId:', userStore.userId)
    if (userStore.userId) {
      console.log('Calling API for unread counts...')
      const [notificationCount, suggestionCount] = await Promise.all([
        getUnreadCount(),
        getSuggestionUnreadCount()
      ])
      console.log('API responses:', { notificationCount, suggestionCount })
      // 确保未读计数为0时正确显示
      unreadCount.value = Math.max(0, (notificationCount || 0) + (suggestionCount || 0))
      console.log('Calculated unreadCount:', unreadCount.value)
    } else {
      unreadCount.value = 0
    }
  } catch (error) {
    console.error('获取未读消息数失败', error)
    unreadCount.value = 0
  }
}

// 监听未读消息数更新事件
const handleUnreadCountUpdated = (event) => {
  console.log('Unread count updated event:', event.detail)
  unreadCount.value = event.detail
  console.log('Updated unreadCount:', unreadCount.value)
}

onMounted(() => {
  console.log('MainLayout mounted')
  console.log('User info:', userStore.userInfo)
  console.log('User ID:', userStore.userId)
  // 强制重置未读计数为0
  unreadCount.value = 0
  // 然后再获取实际的未读计数
  fetchUnreadCount()
  // 监听全局事件
  window.addEventListener('unreadCountUpdated', handleUnreadCountUpdated)
})

onUnmounted(() => {
  // 移除事件监听
  window.removeEventListener('unreadCountUpdated', handleUnreadCountUpdated)
})
</script>

<style scoped>
.main-layout {
  height: 100vh;
  background-color: #f8f9fa;
}

.el-aside {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 0 20px 20px 0;
  margin: 16px 0 16px 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.logo {
  padding: 32px 24px;
  text-align: center;
  background: rgba(255, 255, 255, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.logo-title {
  font-size: 20px;
  font-weight: 600;
  color: #fff;
  margin: 0 0 8px 0;
  line-height: 1.3;
}

.logo-subtitle {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
  font-weight: 400;
  letter-spacing: 1px;
}

.main-menu {
  background: transparent;
  border-right: none;
  padding: 16px 8px;
}

.main-menu :deep(.el-menu-item) {
  margin: 8px 8px;
  padding: 16px 20px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  height: auto;
  line-height: 1.5;
}

.main-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.2);
  transform: translateX(4px);
}

.main-menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.25);
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.main-menu :deep(.el-icon) {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.9);
  margin-right: 12px;
}

.main-menu :deep(.el-menu-item.is-active .el-icon) {
  color: #fff;
}

.menu-text {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.9);
  letter-spacing: 0.5px;
}

.main-menu :deep(.el-menu-item.is-active .menu-text) {
  color: #fff;
}

.el-header {
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  height: 72px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.header-left {
  display: flex;
  align-items: center;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #1a1a2e;
  letter-spacing: 0.5px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 28px;
}

.notification-wrapper {
  position: relative;
  cursor: pointer;
}

.notification-badge :deep(.el-badge__content) {
  background-color: #f56c6c;
  border: none;
  font-size: 11px;
  height: 18px;
  line-height: 18px;
  padding: 0 6px;
  border-radius: 9px;
  font-weight: 600;
}

.notification-icon {
  font-size: 40px;
  color: #606266;
  transition: all 0.3s ease;
  padding: 12px;
  border-radius: 50%;
}

.notification-icon:hover {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

.notification-dot {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 12px;
  height: 12px;
  background: #f56c6c;
  border-radius: 50%;
  border: 2px solid #fff;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 12px;
  transition: all 0.3s ease;
  background: #f8f9fa;
}

.user-info:hover {
  background: #f0f0f0;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
}

.user-role {
  font-size: 12px;
  color: #8c8c8c;
}

.dropdown-icon {
  font-size: 14px;
  color: #8c8c8c;
  margin-left: 4px;
}

.dropdown-menu {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  padding: 8px;
  min-width: 140px;
}

.dropdown-item {
  padding: 10px 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
  transition: all 0.2s ease;
}

.dropdown-item:hover {
  background: #f5f5f5;
  color: #667eea;
}

.el-main {
  padding: 24px;
  background: #f8f9fa;
  overflow-y: auto;
}
</style>