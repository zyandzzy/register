<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import TaskDataStats from '@/components/TaskDataStats.vue'
import TaskDataLogs from '@/components/TaskDataLogs.vue'
import { get } from '@/net'

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
  // Using fetch for DELETE request
  const token = localStorage.getItem('authorize') || sessionStorage.getItem('authorize')
  if (!token) {
    ElMessage.error('未登录')
    return
  }
  
  const authObj = JSON.parse(token)
  
  fetch(`/api/task-log/delete/${logId}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${authObj.token}`
    }
  })
  .then(response => response.json())
  .then(data => {
    if (data.code === 200) {
      ElMessage.success('日志删除成功')
      loadLogs()
    } else {
      ElMessage.error(data.message || '删除失败')
    }
  })
  .catch(error => {
    ElMessage.error('删除失败')
    console.error(error)
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
