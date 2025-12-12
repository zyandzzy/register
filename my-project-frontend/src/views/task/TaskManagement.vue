<script setup>
import {ref, reactive, onMounted} from 'vue'
import {get, post, deleteRequest} from '@/net'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Plus, Delete, Edit, Calendar} from '@element-plus/icons-vue'

const loading = ref(false)
const tasks = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('创建任务')
const isEdit = ref(false)
const currentTaskId = ref(null)
const parentTaskForSubtask = ref(null) // 用于创建子任务时记录父任务

const taskForm = reactive({
  title: '',
  description: '',
  status: 'pending',
  priority: 'medium',
  parentId: null,
  dueDate: null
})

const statusOptions = [
  {label: '待处理', value: 'pending'},
  {label: '进行中', value: 'in_progress'},
  {label: '已完成', value: 'completed'},
  {label: '已取消', value: 'cancelled'}
]

const priorityOptions = [
  {label: '低', value: 'low'},
  {label: '中', value: 'medium'},
  {label: '高', value: 'high'},
  {label: '紧急', value: 'urgent'}
]

const getStatusTag = (status) => {
  const map = {
    'pending': 'info',
    'in_progress': 'warning',
    'completed': 'success',
    'cancelled': 'danger'
  }
  return map[status] || 'info'
}

const getPriorityTag = (priority) => {
  const map = {
    'low': 'info',
    'medium': '',
    'high': 'warning',
    'urgent': 'danger'
  }
  return map[priority] || ''
}

const loadTasks = () => {
  loading.value = true
  get('api/task/list', (data) => {
    tasks.value = data
    loading.value = false
  }, (message) => {
    ElMessage.error(message)
    loading.value = false
  })
}

const openCreateDialog = () => {
  dialogTitle.value = '创建任务'
  isEdit.value = false
  parentTaskForSubtask.value = null
  resetForm()
  dialogVisible.value = true
}

const openCreateSubtaskDialog = (parentTask) => {
  dialogTitle.value = '创建子任务'
  isEdit.value = false
  parentTaskForSubtask.value = parentTask
  resetForm()
  taskForm.parentId = parentTask.id
  dialogVisible.value = true
}

const openEditDialog = (task) => {
  dialogTitle.value = '编辑任务'
  isEdit.value = true
  parentTaskForSubtask.value = null
  currentTaskId.value = task.id
  taskForm.title = task.title
  taskForm.description = task.description
  taskForm.status = task.status
  taskForm.priority = task.priority
  taskForm.parentId = task.parentId
  taskForm.dueDate = task.dueDate
  dialogVisible.value = true
}

const resetForm = () => {
  taskForm.title = ''
  taskForm.description = ''
  taskForm.status = 'pending'
  taskForm.priority = 'medium'
  taskForm.parentId = null
  taskForm.dueDate = null
}

const submitForm = () => {
  if (!taskForm.title) {
    ElMessage.warning('请输入任务标题')
    return
  }

  if (isEdit.value) {
    post(`api/task/update/${currentTaskId.value}`, taskForm, () => {
      ElMessage.success('任务更新成功')
      dialogVisible.value = false
      loadTasks()
    }, (message) => {
      ElMessage.error(message)
    })
  } else {
    post('api/task/create', taskForm, () => {
      ElMessage.success('任务创建成功')
      dialogVisible.value = false
      resetForm()
      loadTasks()
    }, (message) => {
      ElMessage.error(message)
    })
  }
}

const deleteTask = (task) => {
  ElMessageBox.confirm(
      `确定要删除任务"${task.title}"吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).then(() => {
    deleteRequest(`api/task/delete/${task.id}`, () => {
      ElMessage.success('任务删除成功')
      loadTasks()
    }, (message) => {
      ElMessage.error(message)
    })
  }).catch(() => {
    // 用户取消删除
  })
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  loadTasks()
})
</script>

<template>
  <div class="task-management">
    <el-card shadow="never" style="margin: 20px;">
      <template #header>
        <div class="card-header">
          <div>
            <el-icon style="margin-right: 5px"><Calendar/></el-icon>
            <span>任务清单</span>
          </div>
          <el-button type="primary" :icon="Plus" @click="openCreateDialog">
            创建任务
          </el-button>
        </div>
      </template>

      <el-table :data="tasks" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"/>
        <el-table-column prop="title" label="任务标题" min-width="200">
          <template #default="scope">
            <span v-if="scope.row.parentId" style="color: #909399; margin-right: 5px;">└─</span>
            {{ scope.row.title }}
          </template>
        </el-table-column>
        <el-table-column label="父任务" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.parentId" size="small" type="info">
              ID: {{ scope.row.parentId }}
            </el-tag>
            <span v-else style="color: #909399;">根任务</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip/>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status)">
              {{ statusOptions.find(s => s.value === scope.row.status)?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="优先级" width="100">
          <template #default="scope">
            <el-tag :type="getPriorityTag(scope.row.priority)">
              {{ priorityOptions.find(p => p.value === scope.row.priority)?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="截止日期" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.dueDate) }}
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button link type="success" :icon="Plus" @click="openCreateSubtaskDialog(scope.row)" size="small">
              子任务
            </el-button>
            <el-button link type="primary" :icon="Edit" @click="openEditDialog(scope.row)" size="small">
              编辑
            </el-button>
            <el-button link type="danger" :icon="Delete" @click="deleteTask(scope.row)" size="small">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建/编辑任务对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-alert
          v-if="parentTaskForSubtask"
          :title="`为任务 '${parentTaskForSubtask.title}' 创建子任务`"
          type="info"
          :closable="false"
          style="margin-bottom: 20px"
      />
      <el-form :model="taskForm" label-width="100px">
        <el-form-item label="任务标题" required>
          <el-input v-model="taskForm.title" placeholder="请输入任务标题"/>
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input
              v-model="taskForm.description"
              type="textarea"
              :rows="4"
              placeholder="请输入任务描述"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="taskForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option
                v-for="item in statusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="taskForm.priority" placeholder="请选择优先级" style="width: 100%">
            <el-option
                v-for="item in priorityOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker
              v-model="taskForm.dueDate"
              type="datetime"
              placeholder="选择截止日期"
              style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">
            {{ isEdit ? '更新' : '创建' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.task-management {
  height: 100%;
  background-color: #f7f8fa;
}

.dark .task-management {
  background-color: #242628;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}
</style>
