<template>
  <div class="sidebar">
    <div class="sidebar-header">
      <!-- 移除标题 -->
    </div>
    <el-menu
      :default-active="$route.path"
      class="el-menu-vertical"
      router
    >
      <el-menu-item index="/requirement-management">
        <el-icon><Document /></el-icon>
        <span>需求管理</span>
      </el-menu-item>
      <el-menu-item index="/personal-tasks">
        <el-icon><List /></el-icon>
        <span>个人任务</span>
      </el-menu-item>
      <el-menu-item index="/progress">
        <el-icon><Timer /></el-icon>
        <span>需求进度</span>
      </el-menu-item>
      <el-menu-item v-if="userStore.isAdmin" index="/account">
        <el-icon><User /></el-icon>
        <span>账号管理</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { Document, List, Timer, User } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
</script>

<style scoped>
.sidebar {
  width: 240px;
  border-right: 1px solid #dcdfe6;
  background-color: #fff;
}

.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid #dcdfe6;
}

.sidebar-header h2 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.el-menu-vertical {
  border-right: none;
}

.el-menu-item {
  display: flex;
  align-items: center;
}

.el-menu-item .el-icon {
  margin-right: 8px;
}
</style> 