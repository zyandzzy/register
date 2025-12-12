<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import TaskDataStats from '@/components/TaskDataStats.vue'
import TaskDataLogs from '@/components/TaskDataLogs.vue'
import { get, deleteRequest } from '@/net'

const activeTab = ref('statistics')
const statistics = ref(null)
const logs = ref([])
const loading = ref(false)

const loadStatistics = () => {
  loading.value = true
  get('/api/task-log/statistics', (data) => {
    statistics.value = data
    loading.value = false
  }, (message) => {
    ElMessage.error(message)
    loading.value = false
  })
}

const loadLogs = () => {
  loading.value = true
  get('/api/task-log/list', (data) => {
    logs.value = data
    loading.value = false
  }, (message) => {
    ElMessage.error(message)
    loading.value = false
  })
}

const handleDeleteLog = (logId) => {
  deleteRequest(`/api/task-log/delete/${logId}`, () => {
    ElMessage.success('日志删除成功')
    loadLogs()
  }, (message) => {
    ElMessage.error(message || '删除失败')
  })
}

const handleTabChange = (tabName) => {
  if (tabName === 'statistics') {
    loadStatistics()
  } else if (tabName === 'logs') {
    loadLogs()
  }
}

onMounted(() => {
  loadStatistics()
})
</script>

<template>
  <div class="data-analysis">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">数据分析</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="统计分析" name="statistics">
          <div v-loading="loading">
            <TaskDataStats :statistics="statistics" />
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="操作日志" name="logs">
          <div v-loading="loading">
            <TaskDataLogs 
              :logs="logs" 
              @delete="handleDeleteLog"
              @refresh="loadLogs"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<style scoped>
.data-analysis {
  padding: 20px;
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 20px;
  font-weight: bold;
}
</style>
