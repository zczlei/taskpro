<template>
  <MainLayout>
    <div class="app-container">
      <!-- 左侧菜单 -->
      <SideBar />

      <!-- 主要内容区域 -->
      <div class="main-content">
        <div class="page-container">
          <div class="personal-tasks-page">
            <div class="header">
              <h2>个人任务</h2>
              <div class="actions">
                <el-button type="primary" @click="showGlobalAddDialog">添加任务</el-button>
              </div>
            </div>

            <div class="kanban-container">
              <!-- 待办事项 -->
              <div class="kanban-column">
                <div class="column-header">
                  <h3>待办事项 ({{ pendingTasks.length }})</h3>
                </div>
                <div class="task-list">
                  <div v-for="task in pendingTasks" :key="task.id" class="task-card">
                    <div class="task-content">
                      <div class="task-title">{{ task.name }}</div>
                      <div class="task-description">
                        <MarkdownRenderer :content="task.description" />
                      </div>
                      <div v-if="task.imagePath" class="task-image">
                        <img :src="task.imagePath" alt="任务图片" @click="previewImage(task.imagePath)">
                      </div>
                      <div class="task-meta">
                        <el-tag :type="getPriorityType(task.priority)" size="small">
                          {{ getPriorityLabel(task.priority) }}
                        </el-tag>
                        <div class="create-date">
                          创建于: {{ formatDate(task.createdAt) }}
                        </div>
                      </div>
                    </div>
                    <div class="task-actions">
                      <el-button type="primary" link @click="handleEdit(task)">编辑</el-button>
                      <el-button type="success" link @click="handleStart(task)">开始处理</el-button>
                      <el-button type="danger" link @click="handleDelete(task)">删除</el-button>
                    </div>
                  </div>
                  <el-button class="add-card-btn" text @click="showAddDialog('PENDING')">
                    <el-icon><Plus /></el-icon> 添加卡片
                  </el-button>
                </div>
              </div>

              <!-- 正在处理的事项 -->
              <div class="kanban-column">
                <div class="column-header">
                  <h3>正在处理的事项 ({{ inProgressTasks.length }})</h3>
                </div>
                <div class="task-list">
                  <div v-for="task in inProgressTasks" :key="task.id" class="task-card">
                    <div class="task-content">
                      <div class="task-title">{{ task.name }}</div>
                      <div class="task-description">
                        <MarkdownRenderer :content="task.description" />
                      </div>
                      <div v-if="task.imagePath" class="task-image">
                        <img :src="task.imagePath" alt="任务图片" @click="previewImage(task.imagePath)">
                      </div>
                      <div class="task-meta">
                        <el-tag :type="getPriorityType(task.priority)" size="small">
                          {{ getPriorityLabel(task.priority) }}
                        </el-tag>
                        <div class="create-date">
                          创建于: {{ formatDate(task.createdAt) }}
                        </div>
                      </div>
                    </div>
                    <div class="task-actions">
                      <el-button type="primary" link @click="handleEdit(task)">编辑</el-button>
                      <el-button type="success" link @click="handleComplete(task)">完成</el-button>
                      <el-button type="danger" link @click="handleDelete(task)">删除</el-button>
                    </div>
                  </div>
                  <el-button class="add-card-btn" text @click="showAddDialog('IN_PROGRESS')">
                    <el-icon><Plus /></el-icon> 添加卡片
                  </el-button>
                </div>
              </div>

              <!-- 已完成的事项 -->
              <div class="kanban-column">
                <div class="column-header">
                  <h3>已完成的事项 ({{ completedTasks.length }})</h3>
                </div>
                <div class="task-list">
                  <div v-for="task in completedTasks" :key="task.id" class="task-card">
                    <div class="task-content">
                      <div class="task-title">{{ task.name }}</div>
                      <div class="task-description">
                        <MarkdownRenderer :content="task.description" />
                      </div>
                      <div v-if="task.imagePath" class="task-image">
                        <img :src="task.imagePath" alt="任务图片" @click="previewImage(task.imagePath)">
                      </div>
                      <div class="task-meta">
                        <el-tag :type="getPriorityType(task.priority)" size="small">
                          {{ getPriorityLabel(task.priority) }}
                        </el-tag>
                        <div class="create-date">
                          创建于: {{ formatDate(task.createdAt) }}
                        </div>
                        <div class="completion-info">
                          <el-icon><Check /></el-icon>
                          {{ task.completedAt }}
                        </div>
                      </div>
                    </div>
                    <div class="task-actions">
                      <el-button type="primary" link @click="handleReopen(task)">重新打开</el-button>
                      <el-button type="danger" link @click="handleDelete(task)">删除</el-button>
                    </div>
                  </div>
                  <el-button class="add-card-btn" text @click="showAddDialog('COMPLETED')">
                    <el-icon><Plus /></el-icon> 添加卡片
                  </el-button>
                </div>
              </div>
            </div>

            <!-- 新增/编辑对话框 -->
            <el-dialog
              v-model="dialogVisible"
              :title="dialogTitle"
              width="500px"
            >
              <el-form
                ref="taskFormRef"
                :model="taskForm"
                :rules="rules"
                label-width="100px"
              >
                <el-form-item label="任务名称" prop="name">
                  <el-input v-model="taskForm.name" placeholder="请输入任务名称" />
                </el-form-item>
                <el-form-item label="任务描述" prop="description">
                  <el-input 
                    v-model="taskForm.description" 
                    type="textarea"
                    :rows="3"
                    placeholder="请输入任务描述（支持Markdown语法）"
                  />
                  <div class="markdown-tips">
                    支持Markdown语法：**粗体**、*斜体*、# 标题、- 列表、[链接](url)、`代码`等
                  </div>
                </el-form-item>
                <el-form-item label="任务图片">
                  <el-upload
                    class="task-image-upload"
                    action="/api/files/upload"
                    :on-success="handleUploadSuccess"
                    :on-error="handleUploadError"
                    :before-upload="beforeUpload"
                    :limit="1"
                    list-type="picture-card"
                    :file-list="fileList"
                  >
                    <el-icon><Plus /></el-icon>
                  </el-upload>
                </el-form-item>
                <el-form-item label="任务状态" prop="status">
                  <el-select 
                    v-model="taskForm.status" 
                    placeholder="请选择任务状态"
                    :disabled="!isGlobalAdd"
                  >
                    <el-option label="待办事项" value="PENDING" />
                    <el-option label="正在处理的事项" value="IN_PROGRESS" />
                    <el-option label="已完成的事项" value="COMPLETED" />
                  </el-select>
                </el-form-item>
                <el-form-item label="优先级" prop="priority">
                  <el-select v-model="taskForm.priority" placeholder="请选择优先级" style="width: 100%">
                    <el-option label="低" value="LOW" />
                    <el-option label="中" value="MEDIUM" />
                    <el-option label="高" value="HIGH" />
                  </el-select>
                </el-form-item>
                <el-form-item label="截止日期" prop="dueDate">
                  <el-date-picker
                    v-model="taskForm.dueDate"
                    type="date"
                    placeholder="请选择截止日期"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-form>
              <template #footer>
                <div class="dialog-footer">
                  <el-button @click="dialogVisible = false">取消</el-button>
                  <el-button type="primary" @click="handleSave">确定</el-button>
                </div>
              </template>
            </el-dialog>
          </div>
        </div>
      </div>
    </div>

    <!-- 图片预览对话框 -->
    <el-dialog v-model="imagePreviewVisible" title="图片预览" width="50%">
      <div class="image-preview-container">
        <img :src="previewImageUrl" alt="任务图片" class="preview-image">
      </div>
    </el-dialog>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, List, Timer, User, ArrowLeft, ArrowRight, More, Plus, Check } from '@element-plus/icons-vue'
import api from '@/api'
import type { FormInstance, UploadFile, UploadFiles } from 'element-plus'
import MainLayout from '@/layouts/MainLayout.vue'
import { useUserStore } from '@/stores/user'
import SideBar from '@/components/SideBar.vue'
import MarkdownRenderer from '@/components/MarkdownRenderer.vue'

// 使用用户Store
const userStore = useUserStore()

// 任务列表
const tasks = ref([])

// 按状态分类的任务列表
const pendingTasks = computed(() => tasks.value.filter(task => task.status === 'PENDING'))
const inProgressTasks = computed(() => tasks.value.filter(task => task.status === 'IN_PROGRESS'))
const completedTasks = computed(() => tasks.value.filter(task => task.status === 'COMPLETED'))

// 加载状态
const loading = ref(false)

// 对话框状态
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const taskFormRef = ref<FormInstance>()
const taskForm = ref({
  id: null,
  name: '',
  description: '',
  priority: 'MEDIUM',
  status: 'PENDING',
  dueDate: '',
  imagePath: ''
})

// 上传相关
const fileList = ref<UploadFile[]>([])
const imagePreviewVisible = ref(false)
const previewImageUrl = ref('')

// 表单校验规则
const rules = {
  name: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择任务状态', trigger: 'change' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
  dueDate: [{ required: true, message: '请选择截止日期', trigger: 'change' }]
}

// 图片上传前的校验
const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

// 图片上传成功回调
const handleUploadSuccess = (response, file) => {
  taskForm.value.imagePath = response.fileUrl
  ElMessage.success('图片上传成功')
}

// 图片上传失败回调
const handleUploadError = () => {
  ElMessage.error('图片上传失败')
}

// 图片预览
const previewImage = (url) => {
  previewImageUrl.value = url
  imagePreviewVisible.value = true
}

// 获取优先级标签和类型
const getPriorityLabel = (priority) => {
  const labels = {
    HIGH: '高',
    MEDIUM: '中',
    LOW: '低'
  }
  return labels[priority] || priority
}

const getPriorityType = (priority) => {
  const types = {
    HIGH: 'danger',
    MEDIUM: 'warning',
    LOW: 'info'
  }
  return types[priority] || ''
}

// 格式化日期，只显示年月日
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 加载任务列表
const loadTasks = async () => {
  try {
    loading.value = true
    const response = await api.get('/api/personal-tasks')
    
    // 暂时处理：由于后端尚未添加创建者信息，先显示所有任务
    tasks.value = response
    
    // 后面可以使用下面的代码进行过滤（需要后端支持）
    // 过滤任务，仅显示当前用户的任务，除非是管理员
    /*
    if (userStore.isAdmin) {
      tasks.value = response // 管理员可以查看所有任务
    } else {
      // 普通用户只能查看自己创建的任务
      tasks.value = response.filter(task => 
        task.creator === userStore.username || 
        task.createdBy === userStore.username
      )
    }
    */
  } catch (error) {
    console.error('加载任务列表失败:', error)
    ElMessage.error('加载任务列表失败')
  } finally {
    loading.value = false
  }
}

const isGlobalAdd = ref(false)

// 显示新增任务对话框（从列内添加）
const showAddDialog = (status: string) => {
  isEdit.value = false
  isGlobalAdd.value = false
  taskForm.value = {
    id: null,
    name: '',
    description: '',
    priority: 'MEDIUM',
    status: status,
    dueDate: '',
    imagePath: ''
  }
  // 清空文件列表
  fileList.value = []
  
  dialogTitle.value = '新增任务'
  dialogVisible.value = true
}

// 显示全局新增任务对话框
const showGlobalAddDialog = () => {
  isEdit.value = false
  isGlobalAdd.value = true
  taskForm.value = {
    id: null,
    name: '',
    description: '',
    priority: 'MEDIUM',
    status: 'PENDING',
    dueDate: '',
    imagePath: ''
  }
  // 清空文件列表
  fileList.value = []
  
  dialogTitle.value = '新增任务'
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑任务'
  taskForm.value = { 
    ...row,
    dueDate: row.dueDate ? new Date(row.dueDate) : ''
  }
  
  // 重置文件列表
  fileList.value = []
  
  // 如果有图片，添加到文件列表
  if (row.imagePath) {
    fileList.value.push({
      name: row.imagePath.split('/').pop(),
      url: row.imagePath
    } as UploadFile)
  }
  
  dialogVisible.value = true
}

// 处理删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该任务吗？', '提示', {
      type: 'warning'
    })
    
    await api.delete(`/api/personal-tasks/${row.id}`)
    ElMessage.success('删除成功')
    loadTasks()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除任务失败:', error)
      ElMessage.error('删除任务失败')
    }
  }
}

// 开始处理任务
const handleStart = async (row) => {
  try {
    await api.put(`/api/personal-tasks/${row.id}/start`)
    ElMessage.success('任务已开始处理')
    loadTasks()
  } catch (error) {
    console.error('更新任务状态失败:', error)
    ElMessage.error('更新任务状态失败')
  }
}

// 完成任务
const handleComplete = async (row) => {
  try {
    await api.put(`/api/personal-tasks/${row.id}/complete`)
    ElMessage.success('任务已完成')
    loadTasks()
  } catch (error) {
    console.error('更新任务状态失败:', error)
    ElMessage.error('更新任务状态失败')
  }
}

// 重新打开任务
const handleReopen = async (row) => {
  try {
    await api.put(`/api/personal-tasks/${row.id}/reopen`)
    ElMessage.success('任务已重新打开')
    loadTasks()
  } catch (error) {
    console.error('更新任务状态失败:', error)
    ElMessage.error('更新任务状态失败')
  }
}

// 保存任务
const handleSave = async () => {
  if (!taskFormRef.value) return
  
  try {
    await taskFormRef.value.validate()
    
    // 添加当前用户信息
    const taskData = {
      ...taskForm.value,
      creator: userStore.username, // 添加创建者信息
      creatorName: userStore.name || userStore.username,
      // 处理日期格式
      dueDate: taskForm.value.dueDate ? new Date(taskForm.value.dueDate).toISOString().split('T')[0] : null
    }
    
    if (isEdit.value && taskForm.value.id) {
      // 编辑现有任务
      await api.put(`/api/personal-tasks/${taskForm.value.id}`, taskData)
      ElMessage.success('任务更新成功')
    } else {
      // 创建新任务
      await api.post('/api/personal-tasks', taskData)
      ElMessage.success('任务创建成功')
    }
    
    dialogVisible.value = false
    loadTasks()
  } catch (error) {
    console.error('保存任务失败:', error)
    if (error.response) {
      ElMessage.error(`保存失败: ${error.response.data.message || '未知错误'}`)
    } else {
      ElMessage.error('保存失败，请检查网络连接')
    }
  }
}

// 获取任务状态的显示文本
const getStatusLabel = (status: string) => {
  const statusMap = {
    'PENDING': '待办事项',
    'IN_PROGRESS': '正在处理的事项',
    'COMPLETED': '已完成的事项'
  }
  return statusMap[status] || status
}

// 页面加载时获取数据
onMounted(() => {
  loadTasks()
})
</script>

<style scoped>
.app-container {
  display: flex;
  min-height: 100vh;
  background-color: #f0f2f5;
}

.sidebar {
  width: 240px;
  background-color: #fff;
  border-right: 1px solid #dcdfe6;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.sidebar-header {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 24px;
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  background: #fff;
  border-bottom: 1px solid #dcdfe6;
}

.main-content {
  flex: 1;
  padding: 24px;
  overflow: hidden;
}

.page-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.03), 0 1px 6px -1px rgba(0, 0, 0, 0.02), 0 2px 4px 0 rgba(0, 0, 0, 0.02);
  padding: 24px;
  height: calc(100vh - 48px);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.personal-tasks-page {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 0 8px;
}

.header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
  position: relative;
}

.header h2::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 0;
  width: 40px;
  height: 3px;
  background: #1890ff;
  border-radius: 2px;
}

.actions {
  display: flex;
  gap: 12px;
}

.kanban-container {
  display: flex;
  gap: 24px;
  flex: 1;
  overflow: auto;
  padding: 8px 4px 24px 4px;
}

.kanban-column {
  flex: 1;
  background: #f9fafb;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  min-width: 320px;
  max-width: 420px;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px -1px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.kanban-column:hover {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.1);
}

.column-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 8px 8px 0 0;
}

.column-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  display: flex;
  align-items: center;
  gap: 8px;
}

.column-header h3::before {
  content: '';
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

/* 为每列标题添加不同的指示点颜色 */
.kanban-column:nth-child(1) .column-header h3::before {
  background-color: #f56c6c;
}

.kanban-column:nth-child(2) .column-header h3::before {
  background-color: #409eff;
}

.kanban-column:nth-child(3) .column-header h3::before {
  background-color: #67c23a;
}

.task-list {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  min-height: 200px;
}

.task-card {
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
}

/* 待办事项卡片样式 */
.kanban-column:nth-child(1) .task-card {
  background: #fff;
  border-left: 4px solid #f56c6c;
}

.kanban-column:nth-child(1) .task-card:hover {
  border-color: #f56c6c;
  transform: translateY(-2px);
  box-shadow: 0 4px 6px -1px rgba(245, 108, 108, 0.1), 0 2px 4px -2px rgba(245, 108, 108, 0.1);
}

/* 进行中卡片样式 */
.kanban-column:nth-child(2) .task-card {
  background: #fff;
  border-left: 4px solid #409eff;
}

.kanban-column:nth-child(2) .task-card:hover {
  border-color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 4px 6px -1px rgba(64, 158, 255, 0.1), 0 2px 4px -2px rgba(64, 158, 255, 0.1);
}

/* 已完成卡片样式 */
.kanban-column:nth-child(3) .task-card {
  background: #fff;
  border-left: 4px solid #67c23a;
}

.kanban-column:nth-child(3) .task-card:hover {
  border-color: #67c23a;
  transform: translateY(-2px);
  box-shadow: 0 4px 6px -1px rgba(103, 194, 58, 0.1), 0 2px 4px -2px rgba(103, 194, 58, 0.1);
}

.task-content {
  margin-bottom: 12px;
}

.task-title {
  font-weight: 600;
  margin-bottom: 8px;
  color: #1f2937;
  font-size: 15px;
}

.task-description {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 12px;
  white-space: pre-wrap;
  line-height: 1.5;
}

.task-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.create-date {
  font-size: 12px;
  color: #9ca3af;
}

.completion-info {
  font-size: 12px;
  color: #67c23a;
  display: flex;
  align-items: center;
  gap: 4px;
}

.task-actions {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
  margin-top: 12px;
}

.add-card-btn {
  width: 100%;
  height: 40px;
  border: 2px dashed #e5e7eb;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  transition: all 0.3s ease;
  margin-top: 8px;
}

.add-card-btn:hover {
  border-color: #1890ff;
  color: #1890ff;
  background: rgba(24, 144, 255, 0.04);
}

/* 自定义滚动条样式 */
.task-list::-webkit-scrollbar {
  width: 6px;
}

.task-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.task-list::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 3px;
}

.task-list::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

/* 对话框样式优化 */
:deep(.el-dialog) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  margin: 0;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
}

:deep(.el-dialog__title) {
  font-weight: 600;
  font-size: 18px;
  color: #1f2937;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

:deep(.el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid #e5e7eb;
}

/* 表单样式优化 */
:deep(.el-form-item__label) {
  font-weight: 500;
  color: #374151;
}

:deep(.el-input__wrapper),
:deep(.el-textarea__wrapper) {
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e7eb;
}

:deep(.el-input__wrapper:hover),
:deep(.el-textarea__wrapper:hover) {
  border-color: #1890ff;
}

/* 优先级标签样式 */
:deep(.el-tag) {
  border-radius: 4px;
  padding: 4px 8px;
  font-weight: 500;
}

:deep(.el-menu) {
  border-right: none;
}

:deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  
  &.is-active {
    background-color: #e6f4ff;
    color: #1890ff;
    border-right: 3px solid #1890ff;
  }
  
  &:hover {
    background-color: #f5f7fa;
  }
}

:deep(.el-menu-item .el-icon) {
  color: #606266;
}

:deep(.el-menu-item.is-active .el-icon) {
  color: #1890ff;
}

/* 添加图片相关样式 */
.task-image {
  margin: 10px 0;
  width: 100%;
  text-align: center;
}

.task-image img {
  max-width: 100%;
  max-height: 150px;
  object-fit: contain;
  border-radius: 4px;
  cursor: pointer;
  transition: transform 0.3s;
}

.task-image img:hover {
  transform: scale(1.05);
}

.image-preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.preview-image {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
}

.task-image-upload :deep(.el-upload--picture-card) {
  width: 120px;
  height: 120px;
  line-height: 120px;
}

/* Markdown相关样式 */
.markdown-tips {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.4;
}

.task-description {
  margin: 8px 0;
}
</style> 