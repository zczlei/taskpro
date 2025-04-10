import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/api'

export interface Project {
  id: number
  projectName: string
  description: string
  details: string
  status: string
  developer: string
  estimatedTime: string
  actualTime: string
  priority: string
  version: string
  notes: string
  inDevelopment?: boolean
}

export const useProjectStore = defineStore('projects', () => {
  const projects = ref<Project[]>([])

  // 加载需求列表
  const loadRequirements = async () => {
    try {
      console.log('开始加载需求列表...')
      const response = await api.get('/api/requirements')
      console.log('需求列表API响应:', response)
      
      // 直接使用response，因为axios拦截器已经处理了data的提取
      projects.value = response.map((item: any) => {
        return {
          ...item,
          inDevelopment: item.status === 'IN_DEVELOPMENT',
          // 将后端的estimatedCompletionDate映射到前端的estimatedTime
          estimatedTime: item.estimatedCompletionDate,
          // 使用从后端接收到的actualCompletionDate字段
          actualTime: item.actualCompletionDate
        };
      })
      
      console.log('处理后的需求列表:', projects.value)
      console.log('开发中的需求:', projects.value.filter(p => p.status === 'IN_DEVELOPMENT'))
      
      return projects.value
    } catch (error) {
      console.error('加载需求失败:', error)
      throw error
    }
  }

  // 获取未开发的需求
  const getRequirements = computed(() => {
    return projects.value.filter(p => p.status === 'PENDING')
  })

  // 获取开发中的需求
  const getInDevelopment = computed(() => {
    const inDevelopment = projects.value.filter(p => p.status === 'IN_DEVELOPMENT')
    console.log('获取开发中需求，筛选结果:', inDevelopment)
    return inDevelopment
  })

  // 获取除了待处理外的所有需求
  const getNonPendingRequirements = computed(() => {
    const nonPending = projects.value.filter(p => p.status !== 'PENDING')
    console.log('获取非待处理需求，筛选结果:', nonPending)
    return nonPending
  })

  // 开始开发项目
  const startDevelopment = async (project: Project) => {
    try {
      console.log('开始提交开发请求，项目数据:', project)
      
      // 更新需求状态为开发中
      const response = await api.put(`/api/requirements/${project.id}`, {
        ...project,
        status: 'IN_DEVELOPMENT'
      })
      
      console.log('API响应:', response)
      
      // 更新本地状态
      const index = projects.value.findIndex(p => p.id === project.id)
      if (index !== -1) {
        projects.value[index] = {
          ...project,
          status: 'IN_DEVELOPMENT'
        }
      }
      console.log('更新后的需求列表:', projects.value)
      
      // 重新加载需求列表以确保数据同步
      await loadRequirements()
      
      return response
    } catch (error: any) {
      console.error('开始开发失败，详细错误:', {
        message: error.message,
        response: error.response?.data,
        status: error.response?.status,
        config: error.config
      })
      throw error
    }
  }

  // 添加新项目
  const addProject = async (project: Project) => {
    try {
      const response = await api.post('/api/requirements', project)
      if (response.status === 200 || response.status === 201) {
        await loadRequirements()
      }
    } catch (error) {
      console.error('添加需求失败:', error)
      throw error
    }
  }

  // 更新项目
  const updateProject = async (project: Project) => {
    try {
      console.log('开始更新需求，项目数据:', project)
      
      // 准备提交到后端的数据
      const projectData = {
        ...project,
        // 将前端的estimatedTime映射到后端的estimatedCompletionDate
        estimatedCompletionDate: project.estimatedTime,
        // 将前端的actualTime映射到后端的actualCompletionDate
        actualCompletionDate: project.actualTime
      }
      
      console.log('提交到后端的数据:', projectData)
      
      const response = await api.put(`/api/requirements/${project.id}`, projectData)
      console.log('更新API响应:', response)
      
      if (response.status === 200) {
        const index = projects.value.findIndex(p => p.id === project.id)
        if (index !== -1) {
          projects.value[index] = {
            ...project,
            // 确保开发人员字段正确保存
            developer: project.developer || ''
          }
        }
        console.log('本地更新后的项目数据:', projects.value[index])
      }
    } catch (error) {
      console.error('更新需求失败:', error)
      throw error
    }
  }

  // 删除项目
  const deleteProject = async (id: number) => {
    try {
      const response = await api.delete(`/api/requirements/${id}`)
      if (response.status === 200) {
        const index = projects.value.findIndex(p => p.id === id)
        if (index !== -1) {
          projects.value.splice(index, 1)
        }
      }
    } catch (error) {
      console.error('删除需求失败:', error)
      throw error
    }
  }

  return {
    projects,
    getRequirements,
    getInDevelopment,
    getNonPendingRequirements,
    startDevelopment,
    addProject,
    updateProject,
    deleteProject,
    loadRequirements
  }
}) 