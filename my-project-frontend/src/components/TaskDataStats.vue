<script setup>
import { ref, defineProps } from 'vue'

const props = defineProps({
  statistics: Object
})

// Simple bar chart component without external dependencies
</script>

<template>
  <div class="stats-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-label">总任务数</div>
            <div class="stat-value">{{ statistics?.totalTasks || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-label">已完成</div>
            <div class="stat-value completed">{{ statistics?.completedTasks || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-label">进行中</div>
            <div class="stat-value in-progress">{{ statistics?.inProgressTasks || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>操作类型统计</span>
            </div>
          </template>
          <div class="chart-container">
            <div 
              v-for="(value, key) in statistics?.operationTypeStats" 
              :key="key"
              class="stat-bar"
            >
              <div class="bar-label">{{ getOperationLabel(key) }}</div>
              <div class="bar-wrapper">
                <div 
                  class="bar-fill" 
                  :style="{ width: getPercentage(value, statistics?.operationTypeStats) + '%' }"
                ></div>
                <span class="bar-value">{{ value }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近7天操作趋势</span>
            </div>
          </template>
          <div class="chart-container">
            <div 
              v-for="(value, date) in statistics?.dailyOperationStats" 
              :key="date"
              class="stat-bar"
            >
              <div class="bar-label">{{ formatDate(date) }}</div>
              <div class="bar-wrapper">
                <div 
                  class="bar-fill" 
                  :style="{ width: getPercentage(value, statistics?.dailyOperationStats) + '%' }"
                ></div>
                <span class="bar-value">{{ value }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近7天完成趋势</span>
            </div>
          </template>
          <div class="chart-container">
            <div 
              v-for="(value, date) in statistics?.completionTrend" 
              :key="date"
              class="stat-bar"
            >
              <div class="bar-label">{{ formatDate(date) }}</div>
              <div class="bar-wrapper">
                <div 
                  class="bar-fill completed-bar" 
                  :style="{ width: getPercentage(value, statistics?.completionTrend) + '%' }"
                ></div>
                <span class="bar-value">{{ value }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  methods: {
    getOperationLabel(type) {
      const labels = {
        'CREATE': '创建',
        'UPDATE': '更新',
        'DELETE': '删除',
        'QUERY': '查询',
        'COMPLETE': '完成'
      }
      return labels[type] || type
    },
    
    getPercentage(value, data) {
      if (!data) return 0
      const values = Object.values(data)
      const max = Math.max(...values, 1)
      return (value / max) * 100
    },
    
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getMonth() + 1}/${date.getDate()}`
    }
  }
}
</script>

<style scoped>
.stats-container {
  padding: 20px;
}

.stat-card {
  text-align: center;
}

.stat-item {
  padding: 20px 0;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
}

.stat-value.completed {
  color: #67C23A;
}

.stat-value.in-progress {
  color: #E6A23C;
}

.chart-container {
  padding: 10px 0;
}

.stat-bar {
  margin-bottom: 15px;
}

.bar-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.bar-wrapper {
  position: relative;
  height: 30px;
  background: #f5f7fa;
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #409EFF, #66b1ff);
  transition: width 0.3s ease;
}

.bar-fill.completed-bar {
  background: linear-gradient(90deg, #67C23A, #85ce61);
}

.bar-value {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 12px;
  font-weight: bold;
  color: #333;
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}
</style>
