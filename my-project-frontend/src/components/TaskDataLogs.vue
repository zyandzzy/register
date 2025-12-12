<script setup>
import { ref, defineProps, defineEmits } from 'vue'
import { ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'

const props = defineProps({
  logs: Array
})

const emit = defineEmits(['delete', 'refresh'])

const handleDelete = (log) => {
  ElMessageBox.confirm(
    '确认删除此操作记录？',
    '警告',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    emit('delete', log.id)
  }).catch(() => {
    // Canceled
  })
}

const getOperationLabel = (type) => {
  const labels = {
    'CREATE': '创建',
    'UPDATE': '更新',
    'DELETE': '删除',
    'QUERY': '查询',
    'COMPLETE': '完成'
  }
  return labels[type] || type
}

const getOperationType = (type) => {
  const types = {
    'CREATE': 'success',
    'UPDATE': 'warning',
    'DELETE': 'danger',
    'QUERY': 'info',
    'COMPLETE': 'success'
  }
  return types[type] || 'info'
}

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}
</script>

<template>
  <div class="logs-container">
    <div class="logs-header">
      <h3>操作日志</h3>
      <el-button type="primary" @click="emit('refresh')">刷新</el-button>
    </div>
    
    <el-table :data="logs" stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="操作类型" width="120">
        <template #default="scope">
          <el-tag :type="getOperationType(scope.row.operationType)">
            {{ getOperationLabel(scope.row.operationType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="operationDetail" label="操作详情" min-width="200" />
      <el-table-column label="操作时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.operationTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="scope">
          <el-button 
            size="small" 
            type="danger" 
            :icon="Delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-empty v-if="!logs || logs.length === 0" description="暂无操作记录" />
  </div>
</template>

<style scoped>
.logs-container {
  padding: 20px;
}

.logs-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.logs-header h3 {
  margin: 0;
  font-size: 18px;
}
</style>
