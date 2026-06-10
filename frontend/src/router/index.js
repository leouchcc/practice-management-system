import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    meta: { requiresAuth: true },
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'activity',
        name: 'Activity',
        component: () => import('@/views/Activity.vue'),
        meta: { title: '活动发布' }
      },
      {
        path: 'registration',
        name: 'Registration',
        component: () => import('@/views/Registration.vue'),
        meta: { title: '活动管理' }
      },
      {
        path: 'credit',
        name: 'Credit',
        component: () => import('@/views/Credit.vue'),
        meta: { title: '学时学分' }
      },
      {
        path: 'announcement',
        name: 'Announcement',
        component: () => import('@/views/Announcement.vue'),
        meta: { title: '公告通知' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/User.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/Statistics.vue'),
        meta: { title: '数据统计' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人信息' }
      },
      {
        path: 'teacher-activities',
        name: 'TeacherPublishedActivities',
        component: () => import('@/views/TeacherPublishedActivities.vue'),
        meta: { title: '我发布的活动' }
      },
      {
        path: 'activities-by-status',
        name: 'ActivitiesByStatus',
        component: () => import('@/views/ActivitiesByStatus.vue'),
        meta: { title: '活动状态分布' }
      },
      {
        path: 'contact-activities',
        name: 'ContactActivities',
        component: () => import('@/views/ContactActivities.vue'),
        meta: { title: '联系人活动记录' }
      },
      {
        path: 'notification',
        name: 'NotificationCenter',
        component: () => import('@/views/NotificationCenter.vue'),
        meta: { title: '通知中心' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.token) {
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && userStore.token) {
    next('/')
  } else {
    next()
  }
})

export default router
