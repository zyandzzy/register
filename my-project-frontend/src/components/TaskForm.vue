<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue'

const props = defineProps({
  visible: Boolean,
  task: Object,
  parentId: Number
})

const emit = defineEmits(['update:visible', 'submit'])

const formData = ref({
  id: null,
  parentId: null,
  content: '',
  startTime: '',
  endTime: ''
})

const formRules = {
  content: [
    { required: true, message: '请输入任务内容', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ]
}

watch(() => props.visible, (newVal) => {
  if (newVal) {
    if (props.task) {
      // Edit mode
      formData.value = {
        id: props.task.id,
        parentId: props.task.parentId,
        content: props.task.content,
        startTime: props.task.startTime,
        endTime: props.task.endTime
      }
    } else {
      // Create mode
      formData.value = {
        id: null,
        parentId: props.parentId || null,
        content: '',
        startTime: '',
        endTime: ''
      }
    }
  }
})

const formRef = ref(null)

const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      emit('submit', formData.value)
    }
  })
}

const handleClose = () => {
  emit('update:visible', false)
  formRef.value?.resetFields()
}
</script>

<template>
  <el-dialog
    :model-value="visible"
    :title="task ? '编辑任务' : (parentId ? '添加子任务' : '新建任务')"
    width="600px"
    @close="handleClose"
  >
    <el-form 
      ref="formRef"
      :model="formData" 
      :rules="formRules"
      label-width="100px"
    >
      <el-form-item label="任务内容" prop="content">
        <el-input 
          v-model="formData.content" 
          type="textarea"
          :rows="4"
          placeholder="请输入任务内容"
        />
      </el-form-item>
      
      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker
          v-model="formData.startTime"
          type="datetime"
          placeholder="选择开始时间"
          style="width: 100%"
        />
      </el-form-item>
      
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker
          v-model="formData.endTime"
          type="datetime"
          placeholder="选择结束时间"
          style="width: 100%"
        />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style scoped>
</style>
