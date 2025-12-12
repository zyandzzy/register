<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import TaskList from '@/components/TaskList.vue'
import TaskForm from '@/components/TaskForm.vue'
import { get, post, deleteRequest } from '@/net'
import { Plus } from '@element-plus/icons-vue'

const tasks = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const currentTask = ref(null)
const parentTaskId = ref(null)

const loadTasks = () => {
  loading.value = true
  get('/api/task/list', (data) => {
    tasks.value = data
    loading.value = false
  }, (message) => {
    ElMessage.error(message)
    loading.value = false
  })
}

const handleCreate = () => {
  currentTask.value = null
  parentTaskId.value = null
  dialogVisible.value = true
}

const handleAddSubTask = (parentId) => {
  currentTask.value = null
  parentTaskId.value = parentId
  dialogVisible.value = true
}

const handleEdit = (task) => {
  currentTask.value = task
  parentTaskId.value = null
  dialogVisible.value = true
}

const handleSubmit = (formData) => {
  const url = formData.id ? '/api/task/update' : '/api/task/create'
  post(url, formData, () => {
    ElMessage.success(formData.id ? '任务更新成功' : '任务创建成功')
    dialogVisible.value = false
    loadTasks()
  }, (message) => {
    ElMessage.error(message)
  })
}

const handleDelete = (taskId) => {
  deleteRequest(`/api/task/delete/${taskId}`, () => {
    ElMessage.success('任务删除成功')
    loadTasks()
  }, (message) => {
    ElMessage.error(message || '删除失败')
  })
}

const handleComplete = (taskId) => {
  post(`/api/task/complete/${taskId}`, {}, () => {
    ElMessage.success('任务已标记为完成')
    loadTasks()
  }, (message) => {
    ElMessage.error(message)
  })
}

onMounted(() => {
  loadTasks()
})
</script>

<template>
  <div class="task-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">任务管理</span>
          <el-button type="primary" :icon="Plus" @click="handleCreate">新建任务</el-button>
        </div>
      </template>
      
      <div v-loading="loading">
        <TaskList
          :tasks="tasks"
          @delete="handleDelete"
          @edit="handleEdit"
          @complete="handleComplete"
          @addSubTask="handleAddSubTask"
          @refresh="loadTasks"
        />
      </div>
    </el-card>
    
    <TaskForm
      v-model:visible="dialogVisible"
      :task="currentTask"
      :parentId="parentTaskId"
      @submit="handleSubmit"
    />
  </div>
</template>

<style scoped>
.task-management {
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
