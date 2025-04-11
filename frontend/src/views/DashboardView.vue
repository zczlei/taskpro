<template>
  <MainLayout>
    <div class="app-container">
      <SideBar />
      <div class="main-content">
        <div class="page-container">
          <div class="dashboard-container">
            <div class="page-header">
              <h2 class="page-title">数据看板</h2>
            </div>

            <!-- 数据概览卡片 -->
            <div class="stats-cards">
              <el-card class="stats-card">
                <template #header>
                  <div class="card-header">
                    <span>需求总量</span>
                    <el-icon><Document /></el-icon>
                  </div>
                </template>
                <div class="card-value">{{ totalRequirements }}</div>
                <div class="card-desc">所有需求总数</div>
              </el-card>

              <el-card class="stats-card">
                <template #header>
                  <div class="card-header">
                    <span>开发中需求</span>
                    <el-icon><Loading /></el-icon>
                  </div>
                </template>
                <div class="card-value">{{ inProgressRequirements }}</div>
                <div class="card-desc">当前处于开发状态的需求</div>
              </el-card>

              <el-card class="stats-card">
                <template #header>
                  <div class="card-header">
                    <span>已完成需求</span>
                    <el-icon><SuccessFilled /></el-icon>
                  </div>
                </template>
                <div class="card-value">{{ completedRequirements }}</div>
                <div class="card-desc">已交付的需求数量</div>
              </el-card>

              <el-card class="stats-card">
                <template #header>
                  <div class="card-header">
                    <span>需求完成率</span>
                    <el-icon><DataLine /></el-icon>
                  </div>
                </template>
                <div class="card-value">{{ completionRate }}%</div>
                <div class="card-desc">已完成需求占比</div>
              </el-card>
            </div>

            <!-- 图表区域 -->
            <div class="charts-container">
              <el-row :gutter="24">
                <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                  <el-card class="chart-card">
                    <template #header>
                      <div class="card-header">
                        <span>需求状态分布</span>
                      </div>
                    </template>
                    <div ref="statusChartRef" class="chart"></div>
                  </el-card>
                </el-col>
                <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                  <el-card class="chart-card">
                    <template #header>
                      <div class="card-header">
                        <span>需求优先级分布</span>
                      </div>
                    </template>
                    <div ref="priorityChartRef" class="chart"></div>
                  </el-card>
                </el-col>
              </el-row>

              <el-row :gutter="24" style="margin-top: 24px;">
                <el-col :span="24">
                  <el-card class="chart-card">
                    <template #header>
                      <div class="card-header">
                        <span>近期需求趋势</span>
                      </div>
                    </template>
                    <div ref="trendChartRef" class="chart trend-chart"></div>
                  </el-card>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { Document, Loading, SuccessFilled, DataLine } from '@element-plus/icons-vue'
import MainLayout from '@/layouts/MainLayout.vue'
import SideBar from '@/components/SideBar.vue'
import { useProjectStore } from '@/stores/projects'
import * as echarts from 'echarts/core'
import { PieChart, BarChart, LineChart } from 'echarts/charts'
import {
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent,
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

// 注册必须的组件
echarts.use([
  PieChart,
  BarChart,
  LineChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent,
  CanvasRenderer
])

const projectStore = useProjectStore()
const statusChartRef = ref(null)
const priorityChartRef = ref(null)
const trendChartRef = ref(null)

let statusChart: echarts.ECharts | null = null
let priorityChart: echarts.ECharts | null = null
let trendChart: echarts.ECharts | null = null

// 计算总需求数
const totalRequirements = computed(() => {
  return projectStore.projects.length
})

// 计算开发中需求数
const inProgressRequirements = computed(() => {
  return projectStore.projects.filter(p => p.status === 'IN_DEVELOPMENT').length
})

// 计算已完成需求数
const completedRequirements = computed(() => {
  return projectStore.projects.filter(p => p.status === 'COMPLETED').length
})

// 计算需求完成率
const completionRate = computed(() => {
  if (totalRequirements.value === 0) return 0
  return Math.round((completedRequirements.value / totalRequirements.value) * 100)
})

// 初始化状态分布图表
const initStatusChart = () => {
  if (!statusChartRef.value) return

  statusChart = echarts.init(statusChartRef.value)

  const statusData = [
    { value: projectStore.projects.filter(p => p.status === 'PENDING').length, name: '待处理' },
    { value: projectStore.projects.filter(p => p.status === 'IN_DEVELOPMENT').length, name: '开发中' },
    { value: projectStore.projects.filter(p => p.status === 'COMPLETED').length, name: '已完成' },
    { value: projectStore.projects.filter(p => p.status === 'CANCELLED').length, name: '已取消' }
  ]

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 10,
      data: statusData.map(item => item.name)
    },
    series: [
      {
        name: '需求状态',
        type: 'pie',
        radius: ['50%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 12,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: statusData,
        color: ['#909399', '#E6A23C', '#67C23A', '#909399']
      }
    ]
  }

  statusChart.setOption(option)
}

// 初始化优先级分布图表
const initPriorityChart = () => {
  if (!priorityChartRef.value) return

  priorityChart = echarts.init(priorityChartRef.value)

  const priorityData = [
    { value: projectStore.projects.filter(p => p.priority === 'HIGH').length, name: '高' },
    { value: projectStore.projects.filter(p => p.priority === 'MEDIUM').length, name: '中' },
    { value: projectStore.projects.filter(p => p.priority === 'LOW').length, name: '低' }
  ]

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 10,
      data: priorityData.map(item => item.name)
    },
    series: [
      {
        name: '优先级',
        type: 'pie',
        radius: ['50%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 12,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: priorityData,
        color: ['#F56C6C', '#E6A23C', '#909399']
      }
    ]
  }

  priorityChart.setOption(option)
}

// 初始化趋势图表
const initTrendChart = () => {
  if (!trendChartRef.value) return

  trendChart = echarts.init(trendChartRef.value)

  // 模拟数据 - 实际使用时可以从后端获取真实数据
  const months = ['1月', '2月', '3月', '4月', '5月', '6月']
  const newRequirements = [12, 19, 30, 22, 24, 30]
  const completedRequirements = [5, 10, 15, 20, 18, 25]

  const option = {
    title: {
      text: '需求增长与完成趋势'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['新增需求', '完成需求']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: months
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '新增需求',
        type: 'line',
        stack: 'Total',
        data: newRequirements,
        color: '#409EFF'
      },
      {
        name: '完成需求',
        type: 'line',
        stack: 'Total',
        data: completedRequirements,
        color: '#67C23A'
      }
    ]
  }

  trendChart.setOption(option)
}

// 处理窗口大小变化，重新绘制图表
const handleResize = () => {
  statusChart?.resize()
  priorityChart?.resize()
  trendChart?.resize()
}

// 组件挂载时加载数据
onMounted(async () => {
  try {
    await projectStore.loadRequirements()
    
    // 初始化各个图表
    initStatusChart()
    initPriorityChart()
    initTrendChart()
    
    // 添加窗口大小变化的事件监听
    window.addEventListener('resize', handleResize)
  } catch (error) {
    console.error('加载数据失败:', error)
  }
})
</script>

<style scoped>
.app-container {
  display: flex;
  min-height: 100vh;
  background-color: #f0f2f5;
  width: 100vw;
  max-width: 100%;
}

.main-content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  width: calc(100% - 220px);
  max-width: none;
  box-sizing: border-box;
  margin-left: auto;
  margin-right: 0;
}

.page-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.03);
  padding: 24px;
  min-height: calc(100vh - 48px);
  overflow: auto;
  max-width: none;
  width: 100%;
}

.dashboard-container {
  height: 100%;
  width: 100%;
  overflow: hidden;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  width: 100%;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #1f2f3d;
}

/* 数据卡片样式 */
.stats-cards {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
  flex-wrap: wrap;
  width: 100%;
}

.stats-card {
  flex: 1;
  text-align: center;
  min-width: 200px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-value {
  font-size: 36px;
  font-weight: bold;
  color: #1f2f3d;
  margin: 10px 0;
}

.card-desc {
  font-size: 14px;
  color: #909399;
}

/* 图表样式 */
.charts-container {
  margin-top: 20px;
  width: 100%;
}

.chart-card {
  margin-bottom: 20px;
  width: 100%;
}

.chart {
  height: 350px;
  width: 100%;
}

.trend-chart {
  height: 400px;
}

@media (max-width: 768px) {
  .stats-cards {
    flex-direction: column;
  }
  
  .el-col {
    width: 100% !important;
  }
}
</style> 