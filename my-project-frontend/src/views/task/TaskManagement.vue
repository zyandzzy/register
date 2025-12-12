<script setup>
import {ref, reactive, onMounted, computed} from 'vue'
import {get, post, deleteRequest} from '@/net'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Plus, Delete, Edit, Calendar, FolderOpened, Document, Files} from '@element-plus/icons-vue'

const loading = ref(false)
const tasks = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('创建任务')
const isEdit = ref(false)
const currentTaskId = ref(null)
const parentTaskForSubtask = ref(null)

const taskForm = reactive({
  title: '',
  description: '',
  status: 'pending',
  priority: 'medium',
  parentId: null,
  startDate: null,
  endDate: null,
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

// 组织任务为树形结构
const taskTree = computed(() => {
  const rootTasks = tasks.value.filter(t => !t.parentId)
  const childMap = {}
  
  tasks.value.forEach(task => {
    if (task.parentId) {
      if (!childMap[task.parentId]) {
        childMap[task.parentId] = []
      }
      childMap[task.parentId].push(task)
    }
  })
  
  const buildTree = (task) => {
    return {
      ...task,
      children: childMap[task.id] || []
    }
  }
  
  return rootTasks.map(buildTree)
})

const getStatusTag = (status) => {
  const map = {
    'pending': 'info',
    'in_progress': 'warning',
    'completed': 'success',
    'cancelled': 'danger'
  }
  return map[status] || 'info'
}

const getStatusColor = (status) => {
  const map = {
    'pending': '#909399',
    'in_progress': '#E6A23C',
    'completed': '#67C23A',
    'cancelled': '#F56C6C'
  }
  return map[status] || '#909399'
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

const getPriorityIcon = (priority) => {
  const map = {
    'low': '🔵',
    'medium': '🟡',
    'high': '🟠',
    'urgent': '🔴'
  }
  return map[priority] || '⚪'
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
  taskForm.startDate = task.startDate
  taskForm.endDate = task.endDate
  taskForm.dueDate = task.dueDate
  dialogVisible.value = true
}

const resetForm = () => {
  taskForm.title = ''
  taskForm.description = ''
  taskForm.status = 'pending'
  taskForm.priority = 'medium'
  taskForm.parentId = null
  taskForm.startDate = null
  taskForm.endDate = null
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
    <div class="task-header">
      <div class="header-title">
        <el-icon :size="24"><Files/></el-icon>
        <span>任务管理</span>
      </div>
      <el-button type="primary" :icon="Plus" @click="openCreateDialog" size="large">
        创建任务
      </el-button>
    </div>

    <div class="task-content" v-loading="loading">
      <el-empty v-if="taskTree.length === 0 && !loading" description="暂无任务，点击上方按钮创建新任务" />
      
      <div v-else class="task-list">
        <!-- 根任务卡片 -->
        <div v-for="rootTask in taskTree" :key="rootTask.id" class="task-group">
          <div class="task-card root-task" :class="{'completed': rootTask.status === 'completed'}">
            <div class="task-card-header">
              <div class="task-title-section">
                <el-icon :size="20"><FolderOpened/></el-icon>
                <span class="task-title">{{ rootTask.title }}</span>
                <el-tag :type="getStatusTag(rootTask.status)" size="small">
                  {{ statusOptions.find(s => s.value === rootTask.status)?.label }}
                </el-tag>
              </div>
              <div class="task-actions">
                <el-button :icon="Plus" @click="openCreateSubtaskDialog(rootTask)" size="small" type="success" title="添加子任务"/>
                <el-button :icon="Edit" @click="openEditDialog(rootTask)" size="small" type="primary" title="编辑"/>
                <el-button :icon="Delete" @click="deleteTask(rootTask)" size="small" type="danger" title="删除"/>
              </div>
            </div>
            
            <div class="task-card-body">
              <div class="task-meta">
                <span class="meta-item">
                  <span class="meta-label">优先级</span>
                  <span>{{ getPriorityIcon(rootTask.priority) }} {{ priorityOptions.find(p => p.value === rootTask.priority)?.label }}</span>
                </span>
                <span class="meta-item" v-if="rootTask.startDate">
                  <span class="meta-label">开始</span>
                  <span>{{ formatDate(rootTask.startDate) }}</span>
                </span>
                <span class="meta-item" v-if="rootTask.endDate">
                  <span class="meta-label">结束</span>
                  <span>{{ formatDate(rootTask.endDate) }}</span>
                </span>
                <span class="meta-item" v-if="rootTask.dueDate">
                  <span class="meta-label">截止</span>
                  <span>{{ formatDate(rootTask.dueDate) }}</span>
                </span>
                <span class="meta-item" v-if="rootTask.children && rootTask.children.length > 0">
                  <span class="meta-label">子任务</span>
                  <span>{{ rootTask.children.length }} 个</span>
                </span>
              </div>
              <div class="task-description" v-if="rootTask.description">
                {{ rootTask.description }}
              </div>
            </div>

            <!-- 子任务列表 -->
            <div v-if="rootTask.children && rootTask.children.length > 0" class="subtasks">
              <div class="subtasks-header">
                <span>子任务列表</span>
              </div>
              <div v-for="(subtask, index) in rootTask.children" :key="subtask.id" class="subtask-item">
                <div class="subtask-connector">
                  <div class="connector-line" v-if="index < rootTask.children.length - 1"></div>
                  <div class="connector-dot"></div>
                </div>
                <div class="subtask-card" :class="{'completed': subtask.status === 'completed'}">
                  <div class="subtask-header">
                    <div class="subtask-title-section">
                      <el-icon :size="16"><Document/></el-icon>
                      <span class="subtask-title">{{ subtask.title }}</span>
                      <el-tag :type="getStatusTag(subtask.status)" size="small">
                        {{ statusOptions.find(s => s.value === subtask.status)?.label }}
                      </el-tag>
                    </div>
                    <div class="subtask-actions">
                      <el-button :icon="Edit" @click="openEditDialog(subtask)" link type="primary" size="small">编辑</el-button>
                      <el-button :icon="Delete" @click="deleteTask(subtask)" link type="danger" size="small">删除</el-button>
                    </div>
                  </div>
                  <div class="subtask-meta">
                    <span v-if="subtask.description" class="subtask-desc">{{ subtask.description }}</span>
                    <div class="subtask-dates">
                      <span v-if="subtask.startDate" class="date-item">🕐 开始: {{ formatDate(subtask.startDate) }}</span>
                      <span v-if="subtask.endDate" class="date-item">🏁 结束: {{ formatDate(subtask.endDate) }}</span>
                      <span v-if="subtask.dueDate" class="date-item">⏰ 截止: {{ formatDate(subtask.dueDate) }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 创建/编辑任务对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="650px">
      <el-alert
          v-if="parentTaskForSubtask"
          :title="`为任务 '${parentTaskForSubtask.title}' 创建子任务`"
          type="success"
          :closable="false"
          show-icon
          style="margin-bottom: 20px"
      />
      <el-form :model="taskForm" label-width="100px">
        <el-form-item label="任务标题" required>
          <el-input v-model="taskForm.title" placeholder="请输入任务标题" clearable/>
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input
              v-model="taskForm.description"
              type="textarea"
              :rows="3"
              placeholder="请输入任务描述"
          />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
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
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级">
              <el-select v-model="taskForm.priority" placeholder="请选择优先级" style="width: 100%">
                <el-option
                    v-for="item in priorityOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                >
                  <span>{{ getPriorityIcon(item.value) }} {{ item.label }}</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="开始时间">
          <el-date-picker
              v-model="taskForm.startDate"
              type="datetime"
              placeholder="选择开始时间"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
              v-model="taskForm.endDate"
              type="datetime"
              placeholder="选择结束时间"
              style="width: 100%"
          />
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
  min-height: 100vh;
  background: #f0f2f5;
  padding: 20px;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.header-title .el-icon {
  color: #409EFF;
}

.task-content {
  min-height: 400px;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.task-group {
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.task-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border-left: 4px solid #409EFF;
}

.task-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.task-card.completed {
  border-left-color: #67C23A;
  background: linear-gradient(to right, rgba(103, 194, 58, 0.03), white);
}

.task-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.task-title-section {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
}

.task-title-section .el-icon {
  color: #409EFF;
}

.task-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  flex: 1;
}

.task-actions {
  display: flex;
  gap: 8px;
}

.task-card-body {
  margin-top: 16px;
}

.task-meta {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #606266;
  padding: 4px 12px;
  background: #f5f7fa;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}

.meta-label {
  font-weight: 600;
  color: #909399;
}

.meta-label::after {
  content: ':';
  margin-left: 2px;
}

.task-description {
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  color: #606266;
  line-height: 1.6;
  margin-top: 8px;
  border: 1px solid #e4e7ed;
}

.subtasks {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px dashed #dcdfe6;
}

.subtasks-header {
  font-size: 13px;
  font-weight: 600;
  color: #909399;
  margin-bottom: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.subtask-item {
  display: flex;
  gap: 12px;
  margin-bottom: 10px;
  position: relative;
}

.subtask-connector {
  position: relative;
  width: 20px;
  display: flex;
  justify-content: center;
  padding-top: 6px;
}

.connector-line {
  position: absolute;
  left: 50%;
  top: 18px;
  bottom: -10px;
  width: 2px;
  background: #dcdfe6;
  transform: translateX(-50%);
}

.connector-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #409EFF;
  border: 2px solid white;
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.3);
  z-index: 1;
}

.subtask-card {
  flex: 1;
  background: white;
  border-radius: 8px;
  padding: 14px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.subtask-card:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.subtask-card.completed {
  background: rgba(103, 194, 58, 0.05);
  border-color: #67C23A;
}

.subtask-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.subtask-title-section {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.subtask-title-section .el-icon {
  color: #409EFF;
}

.subtask-title {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

.subtask-actions {
  display: flex;
  gap: 8px;
}

.subtask-meta {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.subtask-desc {
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
}

.subtask-dates {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.date-item {
  font-size: 12px;
  color: #909399;
  padding: 2px 8px;
  background: #f5f7fa;
  border-radius: 4px;
}

/* 暗色模式适配 */
.dark .task-management {
  background: #1a1a1a;
}

.dark .task-header {
  background: #2c2c2c;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
}

.dark .header-title {
  color: #e4e7ed;
}

.dark .task-card {
  background: #2c2c2c;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
}

.dark .task-title {
  color: #e4e7ed;
}

.dark .task-description {
  background: #363636;
  color: #c0c4cc;
  border-color: #4c4c4c;
}

.dark .meta-item {
  background: #363636;
  color: #c0c4cc;
  border-color: #4c4c4c;
}

.dark .subtask-card {
  background: #2c2c2c;
  border-color: #4c4c4c;
}

.dark .subtask-title {
  color: #e4e7ed;
}

.dark .subtask-desc {
  color: #c0c4cc;
}

.dark .date-item {
  background: #363636;
  color: #c0c4cc;
}
</style>
