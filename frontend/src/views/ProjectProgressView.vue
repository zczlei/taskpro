<template>
  <MainLayout>
    <div class="app-container">
      <SideBar />
      <div class="main-content">
        <div class="page-container">
          <div class="progress-container">
            <div class="page-header">
              <h2 class="page-title">需求进度跟踪</h2>
            </div>

            <!-- 项目列表 -->
            <el-table
              :data="displayProjects"
              style="width: 100%"
              border
              stripe
              v-loading="loading"
            >
              <el-table-column type="selection" width="55" />
              <el-table-column prop="id" label="序号" width="80" />
              <el-table-column prop="projectName" label="项目" min-width="150" />
              <el-table-column prop="description" label="需求" min-width="200" show-overflow-tooltip />
              <el-table-column prop="details" label="需求详细描述" min-width="300">
                <template #default="scope">
                  <el-button type="primary" link @click="showDetailDialog(scope.row)">
                    查看详情
                  </el-button>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="进度" width="100">
                <template #default="scope">
                  <el-tag
                    :type="getStatusType(scope.row.status)"
                    effect="light"
                  >
                    {{ getStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="developer" label="开发人员" width="100">
                <template #default="scope">
                  <el-tag
                    :type="scope.row.developer === '冬冬' ? 'warning' : 'info'"
                    effect="plain"
                  >
                    {{ scope.row.developer }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="estimatedTime" label="预计上线时间" width="120" />
              <el-table-column prop="actualTime" label="实际上线时间" width="120">
                <template #default="{ row }">
                  {{ row.actualTime || '-' }}
                </template>
              </el-table-column>
              <el-table-column prop="priority" label="优先级" width="100">
                <template #default="scope">
                  <el-tag
                    :type="getPriorityType(scope.row.priority)"
                    effect="plain"
                  >
                    {{ getPriorityText(scope.row.priority) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="version" label="版本号" width="100" />
              <el-table-column prop="notes" label="备注" min-width="150" show-overflow-tooltip />
              
              <!-- 操作列 -->
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="scope">
                  <el-button type="primary" size="small" link @click="handleEdit(scope.row)">
                    编辑
                  </el-button>
                  <el-button type="danger" size="small" link @click="handleDelete(scope.row)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="pagination">
              <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 50, 100]"
                :total="total"
                layout="total, sizes, prev, pager, next"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              />
            </div>
          </div>

          <!-- 需求详情对话框 -->
          <el-dialog
            title="需求详细描述"
            v-model="detailDialogVisible"
            width="60%"
          >
            <div class="requirements-detail">
              <h3>{{ selectedProject.name }}</h3>
              <div class="detail-item">
                <label>需求概述：</label>
                <p>{{ selectedProject.requirements }}</p>
              </div>
              <div class="detail-item">
                <label>详细描述：</label>
                <div class="detail-content">
                  {{ selectedProject.requirementsDetail || '暂无详细描述' }}
                </div>
              </div>
              <div class="detail-item">
                <label>开发状态：</label>
                <el-tag :type="getStatusType(selectedProject.status)" effect="light">
                  {{ getStatusText(selectedProject.status) }}
                </el-tag>
              </div>
              <div class="detail-item">
                <label>负责人：</label>
                <el-tag :type="selectedProject.developer === '冬冬' ? 'warning' : 'info'" effect="plain">
                  {{ selectedProject.developer }}
                </el-tag>
              </div>
            </div>
            <template #footer>
              <span class="dialog-footer">
                <el-button @click="detailDialogVisible = false">关闭</el-button>
              </span>
            </template>
          </el-dialog>

          <!-- 编辑需求对话框 -->
          <el-dialog
            :title="editDialogTitle"
            v-model="editDialogVisible"
            width="60%"
          >
            <el-form
              ref="editFormRef"
              :model="editForm"
              label-width="120px"
              :rules="formRules"
            >
              <el-form-item label="项目名称" prop="projectName">
                <el-input v-model="editForm.projectName" />
              </el-form-item>
              <el-form-item label="需求" prop="description">
                <el-input v-model="editForm.description" />
              </el-form-item>
              <el-form-item label="需求详细描述" prop="details">
                <el-input v-model="editForm.details" type="textarea" :rows="4" />
              </el-form-item>
              <el-form-item label="开发状态" prop="status">
                <el-select v-model="editForm.status" style="width: 100%">
                  <el-option label="开发中" value="IN_DEVELOPMENT" />
                  <el-option label="已完成" value="COMPLETED" />
                  <el-option label="已取消" value="CANCELLED" />
                  <el-option label="待处理" value="PENDING" />
                </el-select>
              </el-form-item>
              <el-form-item label="开发人员" prop="developer">
                <div class="developer-select-container">
                  <el-select v-model="editForm.developer" style="width: 100%">
                    <el-option
                      v-for="dev in developerList"
                      :key="dev"
                      :label="dev"
                      :value="dev"
                    />
                  </el-select>
                  <el-button type="primary" @click="showDeveloperConfig" size="small">配置</el-button>
                </div>
              </el-form-item>
              <el-form-item label="优先级" prop="priority">
                <el-select v-model="editForm.priority" style="width: 100%">
                  <el-option label="高" value="HIGH" />
                  <el-option label="中" value="MEDIUM" />
                  <el-option label="低" value="LOW" />
                </el-select>
              </el-form-item>
              <el-form-item label="预计上线时间" prop="estimatedTime">
                <el-date-picker
                  v-model="editForm.estimatedTime"
                  type="date"
                  placeholder="选择预计上线时间"
                  style="width: 100%"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
              <el-form-item label="实际上线时间" prop="actualTime">
                <el-date-picker
                  v-model="editForm.actualTime"
                  type="date"
                  placeholder="选择实际上线时间"
                  style="width: 100%"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
              <el-form-item label="版本号" prop="version">
                <el-input v-model="editForm.version" />
              </el-form-item>
              <el-form-item label="备注" prop="notes">
                <el-input v-model="editForm.notes" type="textarea" :rows="3" />
              </el-form-item>
            </el-form>
            <template #footer>
              <span class="dialog-footer">
                <el-button @click="editDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSave" :loading="saveLoading">保存</el-button>
              </span>
            </template>
          </el-dialog>

          <!-- 开发人员配置对话框 -->
          <el-dialog
            title="开发人员配置"
            v-model="developerConfigVisible"
            width="500px"
          >
            <div class="developer-config">
              <div class="developer-header">
                <el-button type="primary" @click="addDeveloper">添加开发人员</el-button>
              </div>
              <el-table :data="developerList" border style="width: 100%">
                <el-table-column label="开发人员">
                  <template #default="{ row, $index }">
                    <el-input v-model="developerList[$index]" placeholder="请输入开发人员姓名" />
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100">
                  <template #default="{ $index }">
                    <el-button type="danger" link @click="removeDeveloper($index)">
                      删除
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div class="dialog-footer" style="margin-top: 20px;">
                <el-button @click="developerConfigVisible = false">取消</el-button>
                <el-button type="primary" @click="saveDeveloperConfig">
                  确定
                </el-button>
              </div>
            </div>
          </el-dialog>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import SideBar from '@/components/SideBar.vue'
import { useProjectStore, type Project } from '@/stores/projects'
import { ElMessageBox, ElMessage } from 'element-plus'
import MainLayout from '@/layouts/MainLayout.vue'

// 获取项目store
const projectStore = useProjectStore()

// 项目列表数据
const loading = ref(false)
const detailDialogVisible = ref(false)
const editDialogVisible = ref(false)
const saveLoading = ref(false)
const editFormRef = ref(null)
const selectedProject = ref<Project>({
  id: 0,
  name: '',
  requirements: '',
  requirementsDetail: '',
  status: '',
  developer: '',
  estimatedTime: '',
  actualTime: '',
  priority: '',
  version: '',
  notes: ''
})
const editForm = ref({
  id: 0,
  projectName: '',
  description: '',
  details: '',
  status: '',
  developer: '',
  priority: '',
  estimatedTime: '',
  actualTime: '',
  version: '',
  notes: ''
})
const editDialogTitle = ref('编辑需求')
const developerConfigVisible = ref(false)
const developerList = ref<string[]>(['义军', '冬冬'])

// 获取开发中的项目列表
const projectList = computed(() => projectStore.getNonPendingRequirements)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 计算属性：需要显示的项目列表
const displayProjects = computed(() => {
  return projectList.value
    .slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
});

// 获取状态对应的类型
const getStatusType = (status: string) => {
  const statusMap = {
    'IN_DEVELOPMENT': 'warning',
    'COMPLETED': 'success',
    'CANCELLED': 'info',
    'PENDING': 'info'
  }
  return statusMap[status] || 'info'
}

// 获取优先级对应的类型
const getPriorityType = (priority: string) => {
  const priorityMap = {
    'HIGH': 'danger',
    'MEDIUM': 'warning',
    'LOW': 'info'
  }
  return priorityMap[priority] || 'info'
}

// 获取状态显示文本
const getStatusText = (status: string) => {
  const statusMap = {
    'IN_DEVELOPMENT': '开发中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消',
    'PENDING': '待处理'
  }
  return statusMap[status] || status
}

// 获取优先级显示文本
const getPriorityText = (priority: string) => {
  const priorityMap = {
    'HIGH': '高',
    'MEDIUM': '中',
    'LOW': '低'
  }
  return priorityMap[priority] || priority
}

// 显示详情对话框
const showDetailDialog = (project: Project) => {
  selectedProject.value = project
  detailDialogVisible.value = true
}

// 加载项目列表
const loadProjectList = () => {
  loading.value = true
  try {
    total.value = projectList.value.length
  } finally {
    loading.value = false
  }
}

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadProjectList()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadProjectList()
}

// 编辑需求
const handleEdit = (row: Project) => {
  console.log('编辑的行数据:', row)
  
  editForm.value = { 
    ...row,
    // 将estimatedCompletionDate映射到estimatedTime
    estimatedTime: row.estimatedCompletionDate,
    // 使用后端返回的actualTime
    actualTime: row.actualTime
  }
  editDialogVisible.value = true
}

// 删除需求
const handleDelete = async (row: Project) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该需求吗？此操作不可恢复。',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    loading.value = true
    await projectStore.deleteProject(Number(row.id))
    ElMessage.success('删除成功')
    await projectStore.loadRequirements()
    loadProjectList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除需求失败:', error)
      ElMessage.error('删除需求失败: ' + (error.message || '未知错误'))
    }
  } finally {
    loading.value = false
  }
}

// 表单校验规则
const formRules = {
  projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入需求描述', trigger: 'blur' }],
  status: [{ required: true, message: '请选择开发状态', trigger: 'change' }]
}

// 保存需求
const handleSave = async () => {
  if (!editFormRef.value) return
  
  try {
    await editFormRef.value.validate()
    
    saveLoading.value = true
    // 打印保存前的数据，检查字段
    console.log('保存前数据:', editForm.value)
    
    // 确保正确更新项目，将前端字段映射到后端字段
    await projectStore.updateProject({
      ...editForm.value,
      // 确保开发人员字段正确设置
      developer: editForm.value.developer || '',
      // 将estimatedTime映射到estimatedCompletionDate
      estimatedCompletionDate: editForm.value.estimatedTime
      // actualTime会在stores/projects.ts中被映射到actualCompletionDate
    })
    
    ElMessage.success('保存成功')
    editDialogVisible.value = false
    await projectStore.loadRequirements()
    loadProjectList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('保存需求失败:', error)
      ElMessage.error('保存需求失败: ' + (error.message || '未知错误'))
    }
  } finally {
    saveLoading.value = false
  }
}

// 显示开发人员配置对话框
const showDeveloperConfig = () => {
  developerConfigVisible.value = true
}

// 添加开发人员
const addDeveloper = () => {
  developerList.value.push('')
}

// 移除开发人员
const removeDeveloper = (index: number) => {
  developerList.value.splice(index, 1)
}

// 保存开发人员配置
const saveDeveloperConfig = () => {
  // 过滤掉空的开发人员
  developerList.value = developerList.value.filter(dev => dev.trim() !== '')
  // 去重
  developerList.value = [...new Set(developerList.value)]
  developerConfigVisible.value = false
  
  // 保存到本地存储
  localStorage.setItem('developerList', JSON.stringify(developerList.value))
}

// 页面加载时获取数据
onMounted(async () => {
  loading.value = true
  try {
    // 从本地存储加载开发人员列表
    const storedDevelopers = localStorage.getItem('developerList')
    if (storedDevelopers) {
      developerList.value = JSON.parse(storedDevelopers)
    }
    
    console.log('正在加载需求列表...')
    await projectStore.loadRequirements()
    console.log('需求列表加载完成，非待处理的需求:', projectStore.getNonPendingRequirements)
    loadProjectList()
  } catch (error) {
    console.error('加载需求列表失败:', error)
  } finally {
    loading.value = false
  }
})
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
  overflow: auto;
}

.progress-container {
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
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

.requirements-detail {
  padding: 20px;
}

.requirements-detail h3 {
  margin: 0 0 20px 0;
  color: #1f2f3d;
  font-size: 18px;
}

.detail-item {
  margin-bottom: 20px;
}

.detail-item label {
  font-weight: bold;
  color: #606266;
  margin-bottom: 8px;
  display: block;
}

.detail-item p {
  margin: 0;
  white-space: pre-line;
  color: #303133;
}

.detail-content {
  background-color: #f5f7fa;
  padding: 16px;
  border-radius: 4px;
  white-space: pre-line;
  color: #303133;
  line-height: 1.6;
}

.developer-select-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.developer-select-container .el-select {
  flex: 1;
}

.developer-config {
  .developer-header {
    margin-bottom: 20px;
  }
}

@media screen and (max-width: 768px) {
  .main-content {
    padding: 16px;
  }
  
  .page-container {
    padding: 16px;
  }
}
</style> 