<template>
  <div class="app-container">
    <SideBar />
    <div class="main-content">
      <div class="page-container">
        <div class="progress-container">
          <div class="page-header">
            <h2 class="page-title">需求进度</h2>
          </div>

          <!-- 项目列表 -->
          <el-table
            :data="projectList"
            style="width: 100%"
            border
            stripe
            v-loading="loading"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="id" label="序号" width="80" />
            <el-table-column prop="name" label="项目" min-width="150" />
            <el-table-column prop="requirements" label="需求" min-width="200" show-overflow-tooltip />
            <el-table-column prop="requirementsDetail" label="需求详细描述" min-width="300">
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
                  {{ scope.row.status }}
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
            <el-table-column prop="actualTime" label="实际上线时间" width="120" />
            <el-table-column prop="priority" label="优先级" width="100">
              <template #default="scope">
                <el-tag
                  :type="getPriorityType(scope.row.priority)"
                  effect="plain"
                >
                  {{ scope.row.priority }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="version" label="版本号" width="100" />
            <el-table-column prop="notes" label="备注" min-width="150" show-overflow-tooltip />
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
                {{ selectedProject.status }}
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
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import SideBar from '@/components/SideBar.vue'
import { useProjectStore, type Project } from '@/stores/projects'

// 获取项目store
const projectStore = useProjectStore()

// 项目列表数据
const loading = ref(false)
const detailDialogVisible = ref(false)
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

// 获取开发中的项目列表
const projectList = computed(() => projectStore.getInDevelopment)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取状态对应的类型
const getStatusType = (status: string) => {
  const statusMap = {
    '开发中': 'success',
    '测试中': 'warning',
    '已完成': 'info'
  }
  return statusMap[status] || 'info'
}

// 获取优先级对应的类型
const getPriorityType = (priority: string) => {
  const priorityMap = {
    '高': 'danger',
    '中': 'warning',
    '低': 'info'
  }
  return priorityMap[priority] || 'info'
}

// 显示详情对话框
const showDetailDialog = (project: Project) => {
  selectedProject.value = project
  detailDialogVisible.value = true
}

// 加载项目列表
const loadProjectList = () => {
  loading.value = true
  setTimeout(() => {
    total.value = projectList.value.length
    loading.value = false
  }, 500)
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

// 页面加载时获取数据
onMounted(() => {
  loadProjectList()
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

@media screen and (max-width: 768px) {
  .main-content {
    padding: 16px;
  }
  
  .page-container {
    padding: 16px;
  }
}
</style> 