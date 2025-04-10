<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'
import FieldConfig from '@/components/FieldConfig.vue'
import SideBar from '@/components/SideBar.vue'
import { useProjectStore } from '@/stores/projects'

// 项目列表
const projectList = ref([])

// 需求列表
const requirements = ref([])

// 加载状态
const loading = ref(false)

// 对话框状态
const requirementDialogVisible = ref(false)
const fieldConfigVisible = ref(false)
const isEdit = ref(false)
const form = ref({})
const formRef = ref(null)

// 项目配置相关
const projectDialogVisible = ref(false)

// 开发分配相关
const showDevelopmentDialog = ref(false)
const developmentForm = ref({
  developer: ''
})
const currentRequirement = ref(null)

// 表单字段配置
const formFields = ref([
  {
    label: '项目名称',
    prop: 'projectName',
    type: 'select',
    width: 150,
    visible: true,
    required: true,
    options: []
  },
  {
    label: '需求',
    prop: 'description',
    type: 'input',
    width: 200,
    visible: true,
    required: true
  },
  {
    label: '需求详细描述',
    prop: 'details',
    type: 'textarea',
    width: 300,
    visible: true,
    required: false
  },
  {
    label: '需求提出人',
    prop: 'assignee',
    type: 'input',
    width: 120,
    visible: true,
    required: true
  },
  {
    label: '需求状态',
    prop: 'status',
    type: 'select',
    width: 120,
    visible: true,
    required: true,
    options: [
      { label: '待处理', value: 'PENDING' },
      { label: '进行中', value: 'IN_PROGRESS' },
      { label: '已完成', value: 'COMPLETED' },
      { label: '已取消', value: 'CANCELLED' }
    ]
  },
  {
    label: '优先级',
    prop: 'priority',
    type: 'select',
    width: 120,
    visible: true,
    required: true,
    options: [
      { label: '高', value: 'HIGH' },
      { label: '中', value: 'MEDIUM' },
      { label: '低', value: 'LOW' }
    ]
  },
  {
    label: '提出日期',
    prop: 'dueDate',
    type: 'date',
    width: 150,
    visible: true,
    required: true
  },
  {
    label: '预计完成时间',
    prop: 'estimatedCompletionDate',
    type: 'date',
    width: 150,
    visible: true,
    required: false
  }
])

// 计算可见字段
const visibleFields = computed(() => {
  return formFields.value.filter(field => field.visible)
})

// 表单校验规则
const formRules = computed(() => {
  const rules = {}
  visibleFields.value.forEach(field => {
    if (field.required) {
      rules[field.prop] = [
        { required: true, message: `请输入${field.label}`, trigger: 'blur' }
      ]
    }
  })
  return rules
})

// 获取选项标签
const getOptionLabel = (value, options = []) => {
  const option = options.find(opt => opt.value === value)
  return option ? option.label : value
}

// 获取状态标签类型
const getTagType = (value, prop) => {
  const types = {
    status: {
      'PENDING': 'info',
      'IN_PROGRESS': 'warning',
      'COMPLETED': 'success',
      'CANCELLED': 'danger'
    },
    priority: {
      'HIGH': 'danger',
      'MEDIUM': 'warning',
      'LOW': 'info'
    }
  }
  return types[prop]?.[value] || ''
}

// 加载需求列表
const loadRequirements = async () => {
  try {
    loading.value = true
    // api实例已配置响应拦截器，会自动提取response.data
    const response = await api.get('/api/requirements')
    // 过滤出状态为"待处理"(PENDING)的需求
    requirements.value = Array.isArray(response) 
      ? response.filter(req => req.status === 'PENDING')
      : []
  } catch (error) {
    console.error('加载需求列表失败:', error)
    ElMessage.error('加载需求列表失败')
  } finally {
    loading.value = false
  }
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false
  form.value = {
    assignee: '宇航',
    status: 'PENDING',
    priority: 'MEDIUM',
    dueDate: new Date()
  }
  requirementDialogVisible.value = true
}

// 显示字段配置
const showFieldConfig = () => {
  fieldConfigVisible.value = true
}

// 处理字段配置保存
const handleFieldConfigSave = (fields) => {
  formFields.value = fields
  fieldConfigVisible.value = false
}

// 处理编辑
const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  requirementDialogVisible.value = true
}

// 处理删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该需求吗？', '提示', {
      type: 'warning'
    })
    
    await api.delete(`/api/requirements/${row.id}`)
    ElMessage.success('删除成功')
    loadRequirements()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除需求失败:', error)
      ElMessage.error('删除需求失败')
    }
  }
}

// 处理保存
const handleSave = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (isEdit.value) {
      await api.put(`/api/requirements/${form.value.id}`, form.value)
    } else {
      await api.post('/api/requirements', form.value)
    }
    
    ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
    requirementDialogVisible.value = false
    loadRequirements()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('保存需求失败:', error)
      ElMessage.error('保存需求失败')
    }
  }
}

// 显示项目配置
const showProjectConfig = () => {
  projectDialogVisible.value = true
}

// 添加项目
const addProject = () => {
  projectList.value.push({ name: '' })
}

// 删除项目
const removeProject = (index) => {
  projectList.value.splice(index, 1)
}

// 加载项目列表
const loadProjects = async () => {
  try {
    console.log('开始加载项目列表...')
    const response = await api.get('/api/projects')
    console.log('项目列表加载成功:', response)
    projectList.value = response.map(project => ({
      id: project.id,
      name: project.name
    }))
    
    // 更新项目名称字段的选项
    const projectField = formFields.value.find(field => field.prop === 'projectName')
    if (projectField) {
      projectField.options = projectList.value.map(project => ({
        label: project.name,
        value: project.name
      }))
    }
  } catch (error) {
    console.error('加载项目列表失败:', error)
    console.error('错误详情:', error.response || error.message)
    ElMessage.error('加载项目列表失败')
  }
}

// 保存项目配置
const saveProjects = async () => {
  try {
    // 验证项目名称不能为空
    const emptyProjects = projectList.value.some(project => !project.name.trim())
    if (emptyProjects) {
      ElMessage.warning('项目名称不能为空')
      return
    }

    // 保存到后端
    await api.post('/api/projects', projectList.value)
    ElMessage.success('保存成功')
    projectDialogVisible.value = false
    
    // 重新加载项目列表
    await loadProjects()
  } catch (error) {
    console.error('保存项目配置失败:', error)
    ElMessage.error('保存项目配置失败')
  }
}

// 初始化 projectStore
const projectStore = useProjectStore()

// 处理开发按钮点击
const handleDevelopment = async (row) => {
  currentRequirement.value = row
  developmentForm.value.developer = ''
  showDevelopmentDialog.value = true
}

// 提交开发分配
const handleDevelopmentSubmit = async () => {
  try {
    if (!currentRequirement.value) {
      ElMessage.warning('当前没有选中的需求')
      return
    }
    
    if (!developmentForm.value.developer) {
      ElMessage.warning('请选择开发人员')
      return
    }
    
    const progressProject = {
      ...currentRequirement.value,  // 保留原有的所有字段
      status: 'IN_DEVELOPMENT',
      developer: developmentForm.value.developer,
      startDate: new Date().toISOString()
    }

    console.log('准备提交的项目数据:', progressProject)
    await projectStore.startDevelopment(progressProject)
    ElMessage.success('开发分配成功')
    showDevelopmentDialog.value = false
    developmentForm.value.developer = ''
    
    // 刷新需求列表
    await loadRequirements()
    
  } catch (error) {
    console.error('提交开发失败:', error)
    const errorMessage = error.response?.data?.message || error.message || '提交开发失败'
    ElMessage.error(errorMessage)
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadRequirements()
  loadProjects()
})
</script>

<template>
  <div class="requirement-page">
    <SideBar />
    <div class="main-content">
      <div class="page-container">
        <!-- 需求管理页面 -->
        <div class="content-wrapper">
          <div class="header">
            <h2>需求管理</h2>
            <div class="actions">
              <el-button type="primary" @click="showAddDialog">新增需求</el-button>
              <el-button @click="showFieldConfig">字段配置</el-button>
              <el-button @click="showProjectConfig">项目配置</el-button>
            </div>
          </div>

          <el-table 
            :data="requirements" 
            border 
            style="width: 100%"
            v-loading="loading"
          >
            <!-- 添加序号列 -->
            <el-table-column 
              label="序号" 
              prop="id" 
              width="80"
              fixed="left"
            />
            
            <template v-for="field in visibleFields" :key="field.prop">
              <el-table-column 
                :prop="field.prop" 
                :label="field.label" 
                :width="field.width ? field.width + 'px' : 'auto'"
              >
                <template v-if="field.type === 'select'" #default="scope">
                  <el-tag
                    :type="getTagType(scope.row[field.prop], field.prop)"
                    effect="light"
                  >
                    {{ getOptionLabel(scope.row[field.prop], field.options) }}
                  </el-tag>
                </template>
              </el-table-column>
            </template>
            
            <el-table-column label="操作" fixed="right" width="200px">
              <template #default="scope">
                <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
                <el-button link type="primary" @click="handleDevelopment(scope.row)">开发</el-button>
                <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 需求表单对话框 -->
          <el-dialog
            :title="isEdit ? '编辑需求' : '新增需求'"
            v-model="requirementDialogVisible"
            width="50%"
            align-center
          >
            <el-form
              ref="formRef"
              :model="form"
              :rules="formRules"
              label-width="120px"
            >
              <template v-for="field in visibleFields" :key="field.prop">
                <el-form-item 
                  :label="field.label"
                  :prop="field.prop"
                  :rules="field.required ? [{ required: true, message: `请输入${field.label}`, trigger: 'blur' }] : []"
                >
                  <template v-if="field.type === 'input'">
                    <el-input v-model="form[field.prop]" :placeholder="`请输入${field.label}`" />
                  </template>
                  <template v-else-if="field.type === 'textarea'">
                    <el-input 
                      v-model="form[field.prop]" 
                      type="textarea" 
                      :rows="3"
                      :placeholder="`请输入${field.label}`"
                    />
                  </template>
                  <template v-else-if="field.type === 'select'">
                    <el-select 
                      v-model="form[field.prop]" 
                      :placeholder="`请选择${field.label}`"
                      style="width: 100%"
                    >
                      <el-option
                        v-for="option in field.options"
                        :key="option.value"
                        :label="option.label"
                        :value="option.value"
                      />
                    </el-select>
                  </template>
                  <template v-else-if="field.type === 'date'">
                    <el-date-picker
                      v-model="form[field.prop]"
                      type="date"
                      :placeholder="`请选择${field.label}`"
                      style="width: 100%"
                    />
                  </template>
                </el-form-item>
              </template>
            </el-form>
            <template #footer>
              <span class="dialog-footer">
                <el-button @click="requirementDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSave">确定</el-button>
              </span>
            </template>
          </el-dialog>
          
          <!-- 字段配置对话框 -->
          <FieldConfig
            v-model:visible="fieldConfigVisible"
            :fields="formFields"
            @save="handleFieldConfigSave"
          />
          
          <!-- 项目配置对话框 -->
          <el-dialog
            title="项目配置"
            v-model="projectDialogVisible"
            width="50%"
            align-center
          >
            <div class="project-config">
              <div class="project-header">
                <h3>配置项目</h3>
                <el-button type="primary" @click="addProject">新增项目</el-button>
              </div>
              <el-form>
                <div v-for="(project, index) in projectList" :key="index" class="project-item">
                  <el-form-item label="项目名称">
                    <div class="project-input">
                      <el-input v-model="project.name" placeholder="请输入项目名称" />
                      <el-button type="danger" @click="removeProject(index)">删除</el-button>
                    </div>
                  </el-form-item>
                </div>
              </el-form>
            </div>
            
            <template #footer>
              <span class="dialog-footer">
                <el-button @click="projectDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="saveProjects">保存</el-button>
              </span>
            </template>
          </el-dialog>
          
          <!-- 开发人员分配对话框 -->
          <el-dialog
            title="分配开发人员"
            v-model="showDevelopmentDialog"
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
                <el-button @click="showDevelopmentDialog = false">取消</el-button>
                <el-button type="primary" @click="handleDevelopmentSubmit">确定</el-button>
              </span>
            </template>
          </el-dialog>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.requirement-page {
  display: flex;
  min-height: 100vh;
  background-color: #f0f2f5;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.page-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.03);
  padding: 24px;
  min-height: calc(100vh - 48px);
}

.content-wrapper {
  height: 100%;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header h2 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #1f2f3d;
}

.actions {
  display: flex;
  gap: 10px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.project-config .project-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.project-config h3 {
  margin: 0;
}

.project-item {
  margin-bottom: 10px;
}

.project-input {
  display: flex;
  gap: 10px;
}

:deep(.el-menu) {
  border-right: none;
}

:deep(.el-table) {
  width: 100%;
  border-radius: 4px;
  margin-bottom: 20px;
}

:deep(.el-table .cell) {
  white-space: pre-line;
}

:deep(.el-table__fixed-right) {
  height: 100% !important;
}
</style> 