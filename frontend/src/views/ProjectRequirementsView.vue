<template>
  <div class="app-container">
    <SideBar />
    <div class="main-content">
      <div class="page-container">
        <div class="requirements-container">
          <div class="page-header">
            <h2 class="page-title">需求管理</h2>
            <div class="header-actions">
              <el-button type="primary" @click="showAddDialog">新增需求</el-button>
              <el-button @click="showFieldConfig">字段配置</el-button>
              <el-button @click="showProjectConfig">项目配置</el-button>
            </div>
          </div>

          <!-- 需求列表 -->
          <el-table
            :data="tableData"
            style="width: 100%"
            border
            stripe
            v-loading="loading"
          >
            <el-table-column prop="name" label="项目名称" min-width="150" />
            <el-table-column prop="requirements" label="需求" min-width="200" show-overflow-tooltip />
            <el-table-column prop="requirementsDetail" label="需求详细描述" min-width="300" show-overflow-tooltip />
            <el-table-column prop="developer" label="需求提出人" width="120" />
            <el-table-column prop="status" label="需求状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ scope.row.status || '待处理' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="priority" label="优先级" width="100">
              <template #default="scope">
                <el-tag :type="getPriorityType(scope.row.priority)">
                  {{ scope.row.priority }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="dueDate" label="提出日期" width="120" />
            <el-table-column prop="estimatedTime" label="预计完成时间" width="120" />
            <el-table-column label="操作" width="250" fixed="right">
              <template #default="scope">
                <el-button type="primary" link @click="handleEdit(scope.row)">
                  编辑
                </el-button>
                <el-button type="danger" link @click="handleDelete(scope.row)">
                  删除
                </el-button>
                <el-button type="success" link @click="handleDevelopment(scope.row)">
                  开发
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 开发分配对话框 -->
        <el-dialog
          title="分配开发人员"
          v-model="developmentDialogVisible"
          width="400px"
        >
          <el-form
            :model="developmentForm"
            label-width="100px"
          >
            <el-form-item label="开发人员">
              <el-select v-model="developmentForm.developer" placeholder="请选择开发人员">
                <el-option label="义军" value="义军" />
                <el-option label="冬冬" value="冬冬" />
              </el-select>
            </el-form-item>
          </el-form>
          <template #footer>
            <span class="dialog-footer">
              <el-button @click="developmentDialogVisible = false">取消</el-button>
              <el-button type="primary" @click="handleDevelopmentSubmit">确定</el-button>
            </span>
          </template>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useProjectStore } from '@/stores/projects'
import { ElMessage } from 'element-plus'
import SideBar from '@/components/SideBar.vue'
import api from '@/utils/api'

const projectStore = useProjectStore()

// 需求列表数据
const requirements = ref([])

// 开发分配相关
const developmentDialogVisible = ref(false)
const developmentForm = ref({
  developer: ''
})
const currentRequirement = ref(null)

// 处理开发按钮点击
const handleDevelopment = (row) => {
  currentRequirement.value = row
  developmentForm.value.developer = ''
  developmentDialogVisible.value = true
}

// 加载需求列表
const loadRequirements = async () => {
  try {
    loading.value = true
    const response = await api.get('/api/requirements')
    requirements.value = response
  } catch (error) {
    console.error('加载需求列表失败:', error)
    ElMessage.error('加载需求列表失败')
  } finally {
    loading.value = false
  }
}

// 提交开发分配
const handleDevelopmentSubmit = async () => {
  if (!developmentForm.value.developer) {
    ElMessage.warning('请选择开发人员')
    return
  }

  if (!currentRequirement.value) {
    ElMessage.warning('需求数据异常')
    return
  }

  try {
    loading.value = true
    
    // 创建进度页面需要的项目对象
    const progressProject = {
      id: currentRequirement.value.id,
      name: currentRequirement.value.name,
      requirements: currentRequirement.value.requirements,
      requirementsDetail: currentRequirement.value.requirementsDetail,
      status: '开发中',
      developer: developmentForm.value.developer,
      estimatedTime: '',
      actualTime: '',
      priority: currentRequirement.value.priority,
      version: '',
      notes: '',
      inDevelopment: true
    }

    // 先删除原始需求
    await api.delete(`/api/requirements/${currentRequirement.value.id}`)
    
    // 使用 store 的方法来更新状态
    projectStore.startDevelopment(progressProject, developmentForm.value.developer)
    
    ElMessage.success('开发分配成功')
    developmentDialogVisible.value = false
    await loadRequirements() // 刷新数据
  } catch (error) {
    console.error('分配开发失败:', error)
    ElMessage.error('分配开发失败')
  } finally {
    loading.value = false
  }
}

// 获取未开发的需求列表
const requirementsComputed = computed(() => {
  return projectStore.getRequirements
})

// 表格数据
const tableData = computed(() => {
  return requirementsComputed.value.map(item => ({
    ...item,
    requirementsPreview: item.requirements.split('\n').slice(0, 2).join('\n') + 
      (item.requirements.split('\n').length > 2 ? '\n...' : '')
  }))
})

// 获取状态对应的类型
const getStatusType = (status: string) => {
  const statusMap = {
    '开发中': 'success',
    '测试中': 'warning',
    '已完成': 'info',
    '待处理': ''
  }
  return statusMap[status] || ''
}

// 获取优先级对应的类型
const getPriorityType = (priority: string) => {
  const priorityMap = {
    '高': 'danger',
    '中': 'warning',
    '低': 'info'
  }
  return priorityMap[priority] || ''
}

// 加载状态
const loading = ref(false)

// 编辑和删除功能占位
const handleEdit = (row) => {
  console.log('编辑', row)
}

const handleDelete = (row) => {
  console.log('删除', row)
}

// 初始加载
onMounted(() => {
  loadRequirements()
})

// 显示添加对话框
const showAddDialog = () => {
  currentRequirement.value = {
    status: '未开发',
    priority: '中',
    dueDate: new Date().toISOString().split('T')[0]
  }
  dialogTitle.value = '新增需求'
  dialogVisible.value = true
}
</script>

<style scoped>
.app-container {
  display: flex;
  min-height: 100vh;
  background-color: #f0f2f5;
}

.main-content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.page-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.03);
  padding: 24px;
  min-height: calc(100vh - 48px);
}

.requirements-container {
  height: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #1f2f3d;
}

.header-actions {
  display: flex;
  gap: 12px;
}

:deep(.el-table) {
  border-radius: 8px;
}

:deep(.el-table .cell) {
  white-space: pre-line;
}

:deep(.el-tag) {
  border-radius: 4px;
}
</style> 