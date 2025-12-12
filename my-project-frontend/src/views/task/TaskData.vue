<script setup>
import {ref, reactive, onMounted} from 'vue'
import {get, deleteRequest} from '@/net'
import {ElMessage, ElMessageBox} from 'element-plus'
import {DataLine, TrendCharts, PieChart, Histogram, Delete, Refresh} from '@element-plus/icons-vue'

const loading = ref(false)
const logsLoading = ref(false)
const activeTab = ref('statistics')
const userLogs = ref([])

const statistics = ref({
  totalTasks: 0,
  pendingTasks: 0,
  inProgressTasks: 0,
  completedTasks: 0,
  cancelledTasks: 0,
  totalLogs: 0,
  createActions: 0,
  updateActions: 0,
  deleteActions: 0
})

const loadStatistics = () => {
  loading.value = true
  get('api/task-log/statistics', (data) => {
    statistics.value = data
    loading.value = false
  }, (message) => {
    ElMessage.error(message)
    loading.value = false
  })
}

const loadUserLogs = () => {
  logsLoading.value = true
  get('api/task-log/user-logs', (data) => {
    userLogs.value = data
    logsLoading.value = false
  }, (message) => {
    ElMessage.error(message)
    logsLoading.value = false
  })
}

const deleteLog = (log) => {
  ElMessageBox.confirm(
      `确定要删除此操作记录吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).then(() => {
    deleteRequest(`api/task-log/delete/${log.id}`, () => {
      ElMessage.success('日志删除成功')
      loadUserLogs()
      loadStatistics() // 刷新统计数据
    }, (message) => {
      ElMessage.error(message)
    })
  }).catch(() => {
    // 用户取消删除
  })
}

const getActionLabel = (action) => {
  const map = {
    'create': '创建',
    'update': '更新',
    'delete': '删除',
    'complete': '完成',
    'cancel': '取消'
  }
  return map[action] || action
}

const getActionType = (action) => {
  const map = {
    'create': 'success',
    'update': 'warning',
    'delete': 'danger',
    'complete': 'success',
    'cancel': 'info'
  }
  return map[action] || ''
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

// 计算百分比
const getPercentage = (value, total) => {
  if (total === 0) return 0
  return ((value / total) * 100).toFixed(1)
}

// 获取完成率
const getCompletionRate = () => {
  const total = statistics.value.totalTasks
  if (total === 0) return 0
  return getPercentage(statistics.value.completedTasks, total)
}

onMounted(() => {
  loadStatistics()
  loadUserLogs()
})
</script>

<template>
  <div class="task-data">
    <el-card shadow="never" style="margin: 20px;">
      <template #header>
        <div class="card-header">
          <div>
            <el-icon style="margin-right: 5px"><DataLine/></el-icon>
            <span>任务数据分析</span>
          </div>
          <el-button type="primary" @click="loadStatistics(); loadUserLogs()">
            <el-icon><Refresh/></el-icon>
            刷新数据
          </el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="统计分析" name="statistics">
          <div v-loading="loading">
        <!-- 概览统计卡片 -->
        <el-row :gutter="20" style="margin-bottom: 20px">
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-icon total">
                <el-icon><Histogram/></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-label">总任务数</div>
                <div class="stat-value">{{ statistics.totalTasks }}</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-icon pending">
                <el-icon><TrendCharts/></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-label">待处理</div>
                <div class="stat-value">{{ statistics.pendingTasks }}</div>
                <div class="stat-percent">{{ getPercentage(statistics.pendingTasks, statistics.totalTasks) }}%</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-icon progress">
                <el-icon><DataLine/></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-label">进行中</div>
                <div class="stat-value">{{ statistics.inProgressTasks }}</div>
                <div class="stat-percent">{{ getPercentage(statistics.inProgressTasks, statistics.totalTasks) }}%</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-icon completed">
                <el-icon><PieChart/></el-icon>
              </div>
              <div class="stat-content">
                <div class="stat-label">已完成</div>
                <div class="stat-value">{{ statistics.completedTasks }}</div>
                <div class="stat-percent">{{ getPercentage(statistics.completedTasks, statistics.totalTasks) }}%</div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 完成率进度条 -->
        <el-card shadow="never" style="margin-bottom: 20px">
          <template #header>
            <div style="font-weight: bold;">
              <el-icon style="margin-right: 5px"><TrendCharts/></el-icon>
              任务完成率
            </div>
          </template>
          <div style="padding: 20px 0">
            <el-progress 
              :percentage="parseFloat(getCompletionRate())" 
              :stroke-width="26"
              :color="[
                { color: '#f56c6c', percentage: 30 },
                { color: '#e6a23c', percentage: 60 },
                { color: '#67c23a', percentage: 100 }
              ]"
            >
              <span style="font-size: 16px; font-weight: bold;">{{ getCompletionRate() }}%</span>
            </el-progress>
          </div>
        </el-card>

        <!-- 详细统计 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card shadow="never">
              <template #header>
                <div style="font-weight: bold;">
                  <el-icon style="margin-right: 5px"><PieChart/></el-icon>
                  任务状态分布
                </div>
              </template>
              <el-table :data="[
                { label: '待处理', value: statistics.pendingTasks, type: 'info' },
                { label: '进行中', value: statistics.inProgressTasks, type: 'warning' },
                { label: '已完成', value: statistics.completedTasks, type: 'success' },
                { label: '已取消', value: statistics.cancelledTasks, type: 'danger' }
              ]" style="width: 100%">
                <el-table-column prop="label" label="状态" width="120">
                  <template #default="scope">
                    <el-tag :type="scope.row.type">{{ scope.row.label }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="value" label="数量" width="100"/>
                <el-table-column label="占比">
                  <template #default="scope">
                    <el-progress 
                      :percentage="parseFloat(getPercentage(scope.row.value, statistics.totalTasks))"
                      :color="scope.row.type === 'success' ? '#67c23a' : scope.row.type === 'warning' ? '#e6a23c' : scope.row.type === 'danger' ? '#f56c6c' : '#909399'"
                    />
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>

          <el-col :span="12">
            <el-card shadow="never">
              <template #header>
                <div style="font-weight: bold;">
                  <el-icon style="margin-right: 5px"><Histogram/></el-icon>
                  操作日志统计
                </div>
              </template>
              <el-table :data="[
                { label: '创建操作', value: statistics.createActions, icon: 'Plus' },
                { label: '更新操作', value: statistics.updateActions, icon: 'Edit' },
                { label: '删除操作', value: statistics.deleteActions, icon: 'Delete' },
                { label: '总日志数', value: statistics.totalLogs, icon: 'Document' }
              ]" style="width: 100%">
                <el-table-column prop="label" label="操作类型" width="150"/>
                <el-table-column prop="value" label="次数" width="100"/>
                <el-table-column label="可视化">
                  <template #default="scope">
                    <div style="display: flex; align-items: center;">
                      <div 
                        style="height: 20px; background: linear-gradient(to right, #409eff, #67c23a); border-radius: 4px;"
                        :style="{ width: (scope.row.value / statistics.totalLogs * 100) + '%', minWidth: '2px' }"
                      ></div>
                      <span style="margin-left: 10px; color: #909399; font-size: 12px;">
                        {{ getPercentage(scope.row.value, statistics.totalLogs) }}%
                      </span>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
        </el-row>

        <!-- 空状态提示 -->
        <el-empty 
          v-if="statistics.totalTasks === 0 && !loading" 
          description="暂无任务数据，请先创建任务"
          style="margin-top: 40px"
        >
          <el-button type="primary" @click="$router.push('/index/task-management')">
            去创建任务
          </el-button>
        </el-empty>
          </div>
        </el-tab-pane>

        <el-tab-pane label="操作记录" name="logs">
          <el-table :data="userLogs" v-loading="logsLoading" style="width: 100%" max-height="600">
            <el-table-column prop="id" label="日志ID" width="80"/>
            <el-table-column prop="taskId" label="任务ID" width="100"/>
            <el-table-column label="操作类型" width="100">
              <template #default="scope">
                <el-tag :type="getActionType(scope.row.action)">
                  {{ getActionLabel(scope.row.action) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="操作描述" min-width="200" show-overflow-tooltip/>
            <el-table-column label="操作时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="scope">
                <el-button 
                  link 
                  type="danger" 
                  :icon="Delete" 
                  @click="deleteLog(scope.row)"
                  size="small"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <el-empty 
            v-if="userLogs.length === 0 && !logsLoading" 
            description="暂无操作记录"
            style="margin-top: 40px"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<style scoped>
.task-data {
  height: 100%;
  background-color: #f7f8fa;
}

.dark .task-data {
  background-color: #242628;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin-right: 15px;
  color: white;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.pending {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.progress {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.completed {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.dark .stat-value {
  color: #ffffff;
}

.stat-percent {
  font-size: 12px;
  color: #67c23a;
  margin-top: 5px;
}
</style>
