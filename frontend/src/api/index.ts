import axios from 'axios'
import type { AxiosError } from 'axios'
import axiosRetry from 'axios-retry'

// 创建 axios 实例
const api = axios.create({
  baseURL: 'http://config.umeweb.cn/prj',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 配置重试机制
axiosRetry(api, {
  retries: 3, // 最大重试次数
  retryDelay: (retryCount) => {
    return retryCount * 1000 // 重试延迟时间（毫秒）
  },
  retryCondition: (error) => {
    // 只在网络错误或500错误时重试
    return axiosRetry.isNetworkOrIdempotentRequestError(error) || 
           (error.response?.status === 500)
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 获取存储的token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error: AxiosError) => {
    console.error('响应错误:', error)
    return Promise.reject(error)
  }
)

export default api 