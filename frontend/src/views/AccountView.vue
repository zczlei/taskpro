<template>
  <div class="app-container">
    <!-- 左侧菜单 -->
    <div class="sidebar">
      <div class="sidebar-header">
        项目管理系统
      </div>
      <el-menu default-active="/account" router>
        <el-menu-item index="/">
          <el-icon><Document /></el-icon>
          <span>项目需求</span>
        </el-menu-item>
        <el-menu-item index="/personal-tasks">
          <el-icon><List /></el-icon>
          <span>个人任务</span>
        </el-menu-item>
        <el-menu-item index="progress">
          <el-icon><Timer /></el-icon>
          <span>项目进度</span>
        </el-menu-item>
        <el-menu-item index="/account">
          <el-icon><User /></el-icon>
          <span>账号管理</span>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <div class="page-container">
        <div class="account-container">
          <div class="page-header">
            <h2 class="page-title">账号管理</h2>
            <el-button type="primary" @click="showAddDialog">
              <el-icon><Plus /></el-icon>新增账号
            </el-button>
          </div>

          <!-- 搜索栏 -->
          <div class="search-bar">
            <el-form :inline="true" :model="searchForm">
              <el-form-item>
                <el-input
                  v-model="searchForm.keyword"
                  placeholder="搜索用户名/姓名/邮箱"
                  clearable
                  @keyup.enter="handleSearch"
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item>
                <el-select v-model="searchForm.department" placeholder="选择部门" clearable>
                  <el-option label="技术部" value="技术部" />
                  <el-option label="产品部" value="产品部" />
                  <el-option label="运营部" value="运营部" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch">搜索</el-button>
                <el-button @click="resetSearch">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 账号列表 -->
          <el-table
            :data="accountList"
            style="width: 100%"
            border
            stripe
            v-loading="loading"
          >
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="name" label="姓名" />
            <el-table-column prop="email" label="邮箱" />
            <el-table-column prop="phone" label="手机号" />
            <el-table-column prop="department" label="部门" />
            <el-table-column prop="position" label="职位" />
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button type="primary" link @click="handleEdit(scope.row)">
                  编辑
                </el-button>
                <el-button type="primary" link @click="handleResetPassword(scope.row)">
                  重置密码
                </el-button>
                <el-button type="danger" link @click="handleDelete(scope.row)">
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

          <!-- 新增/编辑账号对话框 -->
          <el-dialog
            :title="dialogType === 'add' ? '新增账号' : '编辑账号'"
            v-model="dialogVisible"
            width="500px"
          >
            <el-form
              ref="accountFormRef"
              :model="accountForm"
              :rules="accountRules"
              label-width="80px"
            >
              <el-form-item label="用户名" prop="username">
                <el-input v-model="accountForm.username" :disabled="dialogType === 'edit'" />
              </el-form-item>
              <el-form-item label="姓名" prop="name">
                <el-input v-model="accountForm.name" />
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="accountForm.email" />
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="accountForm.phone" />
              </el-form-item>
              <el-form-item label="部门" prop="department">
                <el-select v-model="accountForm.department" placeholder="请选择部门">
                  <el-option label="技术部" value="技术部" />
                  <el-option label="产品部" value="产品部" />
                  <el-option label="运营部" value="运营部" />
                </el-select>
              </el-form-item>
              <el-form-item label="职位" prop="position">
                <el-input v-model="accountForm.position" />
              </el-form-item>
              <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
                <el-input v-model="accountForm.password" type="password" show-password />
              </el-form-item>
            </el-form>
            <template #footer>
              <span class="dialog-footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSubmit">确定</el-button>
              </span>
            </template>
          </el-dialog>

          <!-- 重置密码对话框 -->
          <el-dialog
            title="重置密码"
            v-model="resetPasswordVisible"
            width="400px"
          >
            <el-form
              ref="resetPasswordFormRef"
              :model="resetPasswordForm"
              :rules="resetPasswordRules"
              label-width="80px"
            >
              <el-form-item label="新密码" prop="password">
                <el-input v-model="resetPasswordForm.password" type="password" show-password />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="resetPasswordForm.confirmPassword" type="password" show-password />
              </el-form-item>
            </el-form>
            <template #footer>
              <span class="dialog-footer">
                <el-button @click="resetPasswordVisible = false">取消</el-button>
                <el-button type="primary" @click="handleResetPasswordSubmit">确定</el-button>
              </span>
            </template>
          </el-dialog>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Document, List, Timer, User } from '@element-plus/icons-vue'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  department: ''
})

// 账号列表数据
const loading = ref(false)
const accountList = ref([
  {
    id: 1,
    username: 'admin',
    name: '管理员',
    email: 'admin@example.com',
    phone: '13800138000',
    department: '技术部',
    position: '开发工程师',
    createTime: '2024-03-20 10:00:00'
  }
])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 加载账号列表
const loadAccountList = () => {
  loading.value = true
  // 模拟API调用
  setTimeout(() => {
    total.value = accountList.value.length
    loading.value = false
  }, 500)
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadAccountList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.department = ''
  handleSearch()
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  loadAccountList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadAccountList()
}

// 新增/编辑对话框
const dialogVisible = ref(false)
const dialogType = ref('add')
const accountFormRef = ref(null)
const accountForm = reactive({
  username: '',
  name: '',
  email: '',
  phone: '',
  department: '',
  position: '',
  password: ''
})

// 表单验证规则
const accountRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请选择部门', trigger: 'change' }
  ],
  position: [
    { required: true, message: '请输入职位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
}

// 显示新增对话框
const showAddDialog = () => {
  dialogType.value = 'add'
  Object.keys(accountForm).forEach(key => {
    accountForm[key] = ''
  })
  dialogVisible.value = true
}

// 显示编辑对话框
const handleEdit = (row) => {
  dialogType.value = 'edit'
  Object.keys(accountForm).forEach(key => {
    if (key !== 'password') {
      accountForm[key] = row[key]
    }
  })
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!accountFormRef.value) return
  
  await accountFormRef.value.validate((valid) => {
    if (valid) {
      // 模拟API调用
      setTimeout(() => {
        if (dialogType.value === 'add') {
          // 新增账号
          const newAccount = {
            ...accountForm,
            id: accountList.value.length + 1,
            createTime: new Date().toLocaleString()
          }
          accountList.value.push(newAccount)
          ElMessage.success('添加成功')
        } else {
          // 编辑账号
          const index = accountList.value.findIndex(item => item.id === accountForm.id)
          if (index !== -1) {
            accountList.value[index] = {
              ...accountList.value[index],
              ...accountForm
            }
          }
          ElMessage.success('修改成功')
        }
        dialogVisible.value = false
        loadAccountList()
      }, 500)
    }
  })
}

// 删除账号
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除该账号吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 模拟API调用
    setTimeout(() => {
      const index = accountList.value.findIndex(item => item.id === row.id)
      if (index !== -1) {
        accountList.value.splice(index, 1)
      }
      ElMessage.success('删除成功')
      loadAccountList()
    }, 500)
  })
}

// 重置密码
const resetPasswordVisible = ref(false)
const resetPasswordFormRef = ref(null)
const resetPasswordForm = reactive({
  userId: null,
  password: '',
  confirmPassword: ''
})

const resetPasswordRules = {
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== resetPasswordForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const handleResetPassword = (row) => {
  resetPasswordForm.userId = row.id
  resetPasswordForm.password = ''
  resetPasswordForm.confirmPassword = ''
  resetPasswordVisible.value = true
}

const handleResetPasswordSubmit = async () => {
  if (!resetPasswordFormRef.value) return
  
  await resetPasswordFormRef.value.validate((valid) => {
    if (valid) {
      // 模拟API调用
      setTimeout(() => {
        ElMessage.success('密码重置成功')
        resetPasswordVisible.value = false
      }, 500)
    }
  })
}

// 页面加载时获取数据
onMounted(() => {
  loadAccountList()
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
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.03);
  padding: 24px;
  height: calc(100vh - 48px);
  overflow: hidden;
  display: flex;
  flex-direction: column;
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

.account-container {
  padding: 0;
  background-color: transparent;
  min-height: auto;
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

.search-bar {
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-table) {
  border-radius: 8px;
}

@media screen and (max-width: 768px) {
  .search-bar {
    padding: 12px;
  }
  
  :deep(.el-form--inline .el-form-item) {
    margin-right: 0;
    margin-bottom: 10px;
  }
}
</style> 