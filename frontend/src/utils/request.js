import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(
  config => {
    console.log('Request config:', config)
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    const userInfo = localStorage.getItem('userInfo')
    if (userInfo && userInfo !== 'undefined' && userInfo !== 'null') {
      try {
        const user = JSON.parse(userInfo)
        if (user.id) {
          config.headers['userId'] = user.id
        }
      } catch (e) {
        console.error('Failed to parse userInfo:', e)
      }
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    console.log('Response:', response)
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      if (res.code === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res.data
  },
  error => {
    console.error('Request error:', error)
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
