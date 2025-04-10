import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface Project {
  id: number
  name: string
  requirements: string
  requirementsDetail: string
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
  const projects = ref<Project[]>([
    {
      id: 1,
      name: '诺基亚天气',
      requirements: '1.增加事件统计\n2.修改定位方式，改成卫星定位\n3.UI需求优化',
      requirementsDetail: '1. 事件统计需求：\n- 统计用户点击各功能模块的次数\n- 统计用户使用时长\n- 生成统计报表\n\n2. 定位方式改进：\n- 接入卫星定位系统\n- 提高定位精准度\n- 优化定位速度\n\n3. UI优化：\n- 重新设计首页布局\n- 优化天气图标展示\n- 改进用户交互体验',
      status: '',
      developer: '',
      estimatedTime: '',
      actualTime: '',
      priority: '中',
      version: '',
      notes: '',
      inDevelopment: false
    },
    {
      id: 2,
      name: '优米海外天气',
      requirements: '优米厂商widgets天气风格优化',
      requirementsDetail: '1. 设计规范：\n- 遵循Material Design设计规范\n- 适配不同尺寸的widgets\n\n2. 功能优化：\n- 支持自定义主题色\n- 添加天气动画效果\n- 优化数据更新机制',
      status: '开发中',
      developer: '义军',
      estimatedTime: '2025/04/11',
      actualTime: '',
      priority: '',
      version: '',
      notes: '',
      inDevelopment: true
    },
    {
      id: 3,
      name: '中兴预装',
      requirements: '后台修改默认搜索引擎时，如果用户当前是默认认引擎，则生效变更成刚修改后的引擎。如果用户当前不是默认认引擎，则不影响他使用的搜索引擎',
      requirementsDetail: '1. 搜索引擎切换逻辑：\n- 检测用户当前使用的搜索引擎\n- 判断是否为默认搜索引擎\n- 根据条件决定是否更新\n\n2. 用户体验优化：\n- 添加切换提示\n- 保存用户偏好设置\n- 提供还原选项',
      status: '测试中',
      developer: '冬冬',
      estimatedTime: '2025/04/18',
      actualTime: '',
      priority: '低',
      version: '',
      notes: '',
      inDevelopment: false
    },
    {
      id: 4,
      name: '中兴预装',
      requirements: '1、信息流支持配置H5\n2、信息流支持机型配置\n3、微米浏览器底部tab可根据机型进行精细化配置',
      requirementsDetail: '1. H5配置支持：\n- 支持自定义H5页面\n- 配置页面展示规则\n- 支持A/B测试\n\n2. 机型配置：\n- 建立机型数据库\n- 开发配置管理界面\n- 支持批量导入配置\n\n3. Tab配置：\n- 支持自定义Tab样式\n- 根据机型适配Tab布局\n- 提供Tab位置调整功能',
      status: '测试中',
      developer: '冬冬',
      estimatedTime: '2025/04/18',
      actualTime: '',
      priority: '低',
      version: '',
      notes: '',
      inDevelopment: false
    }
  ])

  // 获取未开发的项目
  const getRequirements = computed(() => {
    return projects.value.filter(p => !p.inDevelopment)
  })

  // 获取开发中的项目
  const getInDevelopment = computed(() => {
    return projects.value.filter(p => p.inDevelopment)
  })

  // 开始开发项目
  const startDevelopment = (project: Project, developer: string) => {
    // 先从未开发列表中删除
    const index = projects.value.findIndex(p => p.id === project.id)
    if (index !== -1) {
      projects.value.splice(index, 1)
    }
    
    // 添加到开发中的需求列表
    projects.value.push({
      ...project,
      status: '开发中',
      developer: developer,
      inDevelopment: true
    })
  }

  // 添加新项目
  const addProject = (project: Project) => {
    projects.value.push(project)
  }

  // 更新项目
  const updateProject = (project: Project) => {
    const index = projects.value.findIndex(p => p.id === project.id)
    if (index !== -1) {
      projects.value[index] = project
    }
  }

  // 删除项目
  const deleteProject = (id: number) => {
    const index = projects.value.findIndex(p => p.id === id)
    if (index !== -1) {
      projects.value.splice(index, 1)
    }
  }

  return {
    projects,
    getRequirements,
    getInDevelopment,
    startDevelopment,
    addProject,
    updateProject,
    deleteProject
  }
}) 