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
      formatter: '{a} <br/>{b}: {c} ({d}%)',
      backgroundColor: 'rgba(255,255,255,0.8)',
      borderColor: '#ddd',
      borderWidth: 1,
      textStyle: {
        color: '#333'
      }
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      data: statusData.map(item => item.name),
      textStyle: {
        color: '#606266'
      }
    },
    series: [
      {
        name: '需求状态',
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['40%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          },
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        labelLine: {
          show: false
        },
        data: statusData,
        color: ['#13c2c2', '#fa8c16', '#52c41a', '#f5222d']
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
      formatter: '{a} <br/>{b}: {c} ({d}%)',
      backgroundColor: 'rgba(255,255,255,0.8)',
      borderColor: '#ddd',
      borderWidth: 1,
      textStyle: {
        color: '#333'
      }
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      data: priorityData.map(item => item.name),
      textStyle: {
        color: '#606266'
      }
    },
    series: [
      {
        name: '优先级',
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['40%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          },
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        labelLine: {
          show: false
        },
        data: priorityData,
        color: ['#f5222d', '#faad14', '#52c41a']
      }
    ]
  }

  priorityChart.setOption(option)
}

// 初始化趋势图表
const initTrendChart = () => {
  if (!trendChartRef.value) return

  trendChart = echarts.init(trendChartRef.value)

  // 模拟近30天的数据
  const dates = Array.from({ length: 30 }, (_, i) => {
    const date = new Date()
    date.setDate(date.getDate() - 30 + i + 1)
    return `${date.getMonth() + 1}/${date.getDate()}`
  })

  // 模拟数据
  const completedData = Array.from({ length: 30 }, () => Math.floor(Math.random() * 5))
  const pendingData = Array.from({ length: 30 }, () => Math.floor(Math.random() * 3 + 1))
  const developingData = Array.from({ length: 30 }, () => Math.floor(Math.random() * 4 + 2))

  const option = {
    title: {
      text: '近30天需求变化趋势',
      textStyle: {
        color: '#303133',
        fontSize: 16
      },
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.8)',
      borderColor: '#ddd',
      borderWidth: 1,
      textStyle: {
        color: '#333'
      },
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['已完成', '待处理', '开发中'],
      bottom: '0%',
      textStyle: {
        color: '#606266'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: {
        lineStyle: {
          color: '#ddd'
        }
      },
      axisLabel: {
        color: '#606266',
        rotate: 45,
        fontSize: 10
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: false
      },
      axisLabel: {
        color: '#606266'
      },
      splitLine: {
        lineStyle: {
          color: '#eee'
        }
      }
    },
    series: [
      {
        name: '已完成',
        type: 'line',
        stack: 'Total',
        data: completedData,
        smooth: true,
        symbol: 'emptyCircle',
        symbolSize: 8,
        itemStyle: {
          color: '#52c41a'
        },
        lineStyle: {
          width: 3
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(82, 196, 26, 0.6)' },
            { offset: 1, color: 'rgba(82, 196, 26, 0.1)' }
          ])
        }
      },
      {
        name: '待处理',
        type: 'line',
        stack: 'Total',
        data: pendingData,
        smooth: true,
        symbol: 'emptyCircle',
        symbolSize: 8,
        itemStyle: {
          color: '#13c2c2'
        },
        lineStyle: {
          width: 3
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(19, 194, 194, 0.6)' },
            { offset: 1, color: 'rgba(19, 194, 194, 0.1)' }
          ])
        }
      },
      {
        name: '开发中',
        type: 'line',
        stack: 'Total',
        data: developingData,
        smooth: true,
        symbol: 'emptyCircle',
        symbolSize: 8,
        itemStyle: {
          color: '#fa8c16'
        },
        lineStyle: {
          width: 3
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(250, 140, 22, 0.6)' },
            { offset: 1, color: 'rgba(250, 140, 22, 0.1)' }
          ])
        }
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
  background-color: #f5f7fa;
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
  background-color: #f5f7fa;
  border-radius: 8px;
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
  margin-bottom: 30px;
  width: 100%;
  background: linear-gradient(135deg, #1890ff, #0050b3);
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #ffffff;
}

/* 数据卡片样式 */
.stats-cards {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  flex-wrap: wrap;
  width: 100%;
}

.stats-card {
  flex: 1;
  text-align: center;
  min-width: 200px;
  border-radius: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: none;
  overflow: hidden;
}

.stats-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.stats-card:nth-child(1) :deep(.el-card__header) {
  background: linear-gradient(135deg, #36cfc9, #13c2c2);
  color: white;
}

.stats-card:nth-child(2) :deep(.el-card__header) {
  background: linear-gradient(135deg, #faad14, #fa8c16);
  color: white;
}

.stats-card:nth-child(3) :deep(.el-card__header) {
  background: linear-gradient(135deg, #52c41a, #389e0d);
  color: white;
}

.stats-card:nth-child(4) :deep(.el-card__header) {
  background: linear-gradient(135deg, #722ed1, #531dab);
  color: white;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
}

.card-value {
  font-size: 36px;
  font-weight: bold;
  color: #1f2f3d;
  margin: 15px 0;
}

.card-desc {
  font-size: 14px;
  color: #606266;
}

/* 图表样式 */
.charts-container {
  margin-top: 30px;
  width: 100%;
}

.chart-card {
  margin-bottom: 30px;
  width: 100%;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: none;
  overflow: hidden;
}

.chart-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #1890ff, #096dd9);
  color: white;
  padding: 15px 20px;
}

.chart {
  height: 350px;
  width: 100%;
  padding: 20px;
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