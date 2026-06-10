import { defineStore } from 'pinia'
import { login as loginApi, register as registerApi } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => {
    const userInfoStr = localStorage.getItem('userInfo')
    let userInfo = {}
    try {
      if (userInfoStr && userInfoStr !== 'undefined' && userInfoStr !== 'null') {
        userInfo = JSON.parse(userInfoStr)
      }
    } catch (e) {
      console.error('Failed to parse userInfo from localStorage:', e)
      userInfo = {}
    }
    return {
      token: localStorage.getItem('token') || '',
      userInfo: userInfo
    }
  },

  getters: {
    isLoggedIn: (state) => !!state.token,
    userId: (state) => state.userInfo.id,
    username: (state) => state.userInfo.username,
    role: (state) => state.userInfo.role
  },

  actions: {
    async login(credentials) {
      const response = await loginApi(credentials)
      this.token = response.token
      this.userInfo = response.user
      localStorage.setItem('token', this.token)
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
      return response
    },

    async register(userData) {
      await registerApi(userData)
    },

    logout() {
      this.token = ''
      this.userInfo = {}
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})
