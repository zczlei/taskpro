<template>
  <div class="login-container">
    <div class="login-box">
      <h2>项目管理系统</h2>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item prop="captcha" class="captcha-item">
          <el-input
            v-model="loginForm.captcha"
            placeholder="请输入验证码"
            prefix-icon="Key"
            style="width: 200px"
          />
          <div class="captcha-img" @click="refreshCaptcha">
            <canvas ref="captchaCanvas" width="120" height="40"></canvas>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="login-button" @click="handleLogin">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Key } from '@element-plus/icons-vue'
import api from '../services/api'

const router = useRouter()
const loginFormRef = ref()
const captchaCanvas = ref()
const loading = ref(false)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
  captcha: '',
})

// 验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' },
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 4, max: 4, message: '验证码长度必须为4位', trigger: 'blur' },
  ],
}

// 生成随机验证码
let captchaText = ''
const generateCaptcha = () => {
  const canvas = captchaCanvas.value
  const ctx = canvas.getContext('2d')
  const chars = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'
  
  // 清空画布
  ctx.fillStyle = '#f0f2f5'
  ctx.fillRect(0, 0, canvas.width, canvas.height)
  
  // 生成随机验证码
  captchaText = ''
  ctx.font = '28px Arial'
  ctx.fillStyle = '#333'
  
  for (let i = 0; i < 4; i++) {
    const char = chars[Math.floor(Math.random() * chars.length)]
    captchaText += char
    ctx.fillText(char, 20 + i * 25, 30)
  }
  
  // 添加干扰线
  for (let i = 0; i < 4; i++) {
    ctx.beginPath()
    ctx.moveTo(Math.random() * canvas.width, Math.random() * canvas.height)
    ctx.lineTo(Math.random() * canvas.width, Math.random() * canvas.height)
    ctx.strokeStyle = `rgba(${Math.random() * 255},${Math.random() * 255},${Math.random() * 255},0.5)`
    ctx.stroke()
  }
  
  // 添加干扰点
  for (let i = 0; i < 50; i++) {
    ctx.beginPath()
    ctx.arc(Math.random() * canvas.width, Math.random() * canvas.height, 1, 0, 2 * Math.PI)
    ctx.fillStyle = `rgba(${Math.random() * 255},${Math.random() * 255},${Math.random() * 255},0.5)`
    ctx.fill()
  }
}

// 刷新验证码
const refreshCaptcha = () => {
  generateCaptcha()
  loginForm.captcha = ''
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    
    // 验证验证码
    if (loginForm.captcha.toLowerCase() !== captchaText.toLowerCase()) {
      ElMessage.error('验证码错误')
      generateCaptcha()
      return
    }

    loading.value = true
    
    // 调用后端登录API
    try {
      const response = await api.post('/api/auth/login', {
        username: loginForm.username,
        password: loginForm.password,
        captcha: loginForm.captcha
      })
      
      // 保存token和用户信息
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('username', response.data.username)
      localStorage.setItem('name', response.data.name)
      
      ElMessage.success('登录成功')
      router.push('/personal-tasks')
    } catch (error) {
      console.error('登录失败:', error)
      if (error.response?.status === 401) {
        ElMessage.error('用户名或密码错误')
      } else {
        ElMessage.error('登录失败，请稍后重试')
      }
      generateCaptcha()
    }
  } catch (error) {
    console.error('表单验证失败:', error)
    ElMessage.error('请检查输入信息')
  } finally {
    loading.value = false
  }
}

// 组件挂载时生成验证码
onMounted(() => {
  generateCaptcha()
})
</script>

<style scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
  background-image: linear-gradient(45deg, #f0f2f5 0%, #e6f7ff 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #1f2f3d;
  font-weight: 600;
}

.login-form {
  margin-top: 30px;
}

.captcha-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.captcha-img {
  cursor: pointer;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.login-button {
  width: 100%;
}

:deep(.el-input__wrapper) {
  background-color: #f5f7fa;
}

:deep(.el-input__inner) {
  height: 42px;
}
</style> 