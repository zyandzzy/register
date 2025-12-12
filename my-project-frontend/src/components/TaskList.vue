<script setup>
import { ref, defineProps, defineEmits } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Edit, Check, Plus } from '@element-plus/icons-vue'

const props = defineProps({
  tasks: Array
})

const emit = defineEmits(['delete', 'edit', 'complete', 'addSubTask', 'refresh'])

const handleDelete = (task) => {
  ElMessageBox.confirm(
    task.children && task.children.length > 0 
      ? '删除此任务将同时删除所有子任务，确认删除？' 
      : '确认删除此任务？',
    '警告',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    emit('delete', task.id)
  }).catch(() => {
    // Canceled
  })
}

const handleComplete = (task) => {
  if (task.status === 'COMPLETED') {
    ElMessage.info('该任务已完成')
    return
  }
  emit('complete', task.id)
}

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<template>
  <div class="task-list">
    <div class="task-header">
      <el-button type="primary" @click="emit('refresh')" :icon="Plus">刷新任务列表</el-button>
    </div>
    
    <el-tree
      :data="tasks"
      node-key="id"
      default-expand-all
      :expand-on-click-node="false"
      class="task-tree"
    >
      <template #default="{ node, data }">
        <div class="task-node">
          <div class="task-content" :class="{ 'completed': data.status === 'COMPLETED' }">
            <div class="task-title">
              <el-tag v-if="data.status === 'COMPLETED'" type="success" size="small">已完成</el-tag>
              <el-tag v-else type="info" size="small">进行中</el-tag>
              <span class="content-text">{{ data.content }}</span>
            </div>
            <div class="task-time">
              <span>开始: {{ formatDate(data.startTime) }}</span>
              <span style="margin-left: 20px">结束: {{ formatDate(data.endTime) }}</span>
            </div>
          </div>
          <div class="task-actions">
            <el-button 
              size="small" 
              :icon="Check" 
              @click.stop="handleComplete(data)"
              :disabled="data.status === 'COMPLETED'"
            >完成</el-button>
            <el-button 
              size="small" 
              type="primary" 
              :icon="Plus" 
              @click.stop="emit('addSubTask', data.id)"
            >添加子任务</el-button>
            <el-button 
              size="small" 
              type="warning" 
              :icon="Edit" 
              @click.stop="emit('edit', data)"
            >编辑</el-button>
            <el-button 
              size="small" 
              type="danger" 
              :icon="Delete" 
              @click.stop="handleDelete(data)"
            >删除</el-button>
          </div>
        </div>
      </template>
    </el-tree>
    
    <el-empty v-if="!tasks || tasks.length === 0" description="暂无任务，请添加新任务" />
  </div>
</template>

<style scoped>
.task-list {
  padding: 20px;
}

.task-header {
  margin-bottom: 20px;
}

.task-tree {
  background: transparent;
}

.task-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  border-bottom: 1px solid #eee;
}

.task-content {
  flex: 1;
  min-width: 0;
}

.task-content.completed .content-text {
  text-decoration: line-through;
  color: #999;
}

.task-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 5px;
}

.content-text {
  font-size: 16px;
  font-weight: 500;
}

.task-time {
  font-size: 12px;
  color: #666;
}

.task-actions {
  display: flex;
  gap: 5px;
}
</style>
