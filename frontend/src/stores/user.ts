import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', () => {
  // 状态
  const username = ref(localStorage.getItem('username') || '')
  const name = ref(localStorage.getItem('name') || '')
  const role = ref(localStorage.getItem('role') || 'user')
  const token = ref(localStorage.getItem('token') || '')

  // 计算属性
  const isAdmin = computed(() => role.value === 'admin')
  const isLoggedIn = computed(() => !!token.value)

  // 动作
  function setUser(userData: { username: string; name: string; role: string; token: string }) {
    username.value = userData.username
    name.value = userData.name
    role.value = userData.role
    token.value = userData.token

    // 保存到localStorage
    localStorage.setItem('username', userData.username)
    localStorage.setItem('name', userData.name)
    localStorage.setItem('role', userData.role)
    localStorage.setItem('token', userData.token)
  }

  function clearUser() {
    username.value = ''
    name.value = ''
    role.value = 'user'
    token.value = ''

    // 清除localStorage
    localStorage.removeItem('username')
    localStorage.removeItem('name')
    localStorage.removeItem('role')
    localStorage.removeItem('token')
  }

  return {
    username,
    name,
    role,
    token,
    isAdmin,
    isLoggedIn,
    setUser,
    clearUser
  }
}) 