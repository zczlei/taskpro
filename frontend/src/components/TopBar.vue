<template>
  <div class="top-bar">
    <div class="left-content">
      <h1 class="app-title">任务管理系统</h1>
    </div>
    <div class="right-content">
      <span v-if="username" class="user-info">
        <el-icon><User /></el-icon>
        <span>{{ username }}</span>
      </span>
      <el-button type="danger" size="small" @click="handleLogout">
        <el-icon><SwitchButton /></el-icon>
        退出登录
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { User, SwitchButton } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const username = ref(userStore.name || userStore.username)

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    // 清除所有用户信息
    userStore.clearUser()
    
    // 提示用户已退出
    ElMessage.success('已退出登录')
    
    // 跳转到登录页
    router.push('/login')
  }).catch(() => {
    // 用户取消退出
  })
}
</script>

<style scoped>
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
}

.left-content, .right-content {
  display: flex;
  align-items: center;
}

.app-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  margin-right: 20px;
  color: #606266;
}

.user-info .el-icon {
  margin-right: 5px;
}
</style> 