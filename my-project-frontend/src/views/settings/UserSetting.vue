<script setup>
import Card from "@/components/Card.vue";
import {Message, Refresh, Select, User} from "@element-plus/icons-vue";
import {useStore} from "@/store";
import {computed, reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import {get, post} from "@/net";

const store = useStore()
const desc = ref('')
// 计算注册时间并格式化
const registerTime = computed(() => new Date(store.user.registerTime).toLocaleString())
const baseFormRef = ref()
const emailFormRef = ref()
const baseForm = reactive({
  username: '',
  gender : 0,
  phone : '',
  qq: '',
  wx: '',
  desc :''
})

const emailForm = reactive({
  email: '',
  code: ''
})

const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'))
  } else if(!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)){
    callback(new Error('用户名不能包含特殊字符，只能是中文/英文'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { validator: validateUsername, trigger: ['blur', 'change'] },
    { min: 2, max: 8, message: '用户名的长度必须在2-8个字符之间', trigger: ['blur', 'change'] },
  ], email: [
    { required: true, message: '请输入邮件地址', trigger: 'blur' },
    {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change']}
  ], code: [
    { required: true, message: '请输入获取的验证码', trigger: 'blur' },
  ]
}

const loading = reactive({
  form: true,
  base: false
})

function saveDetails() {
  baseFormRef.value.validate(isValid => {
    if(isValid) {
      loading.base = true
      post("api/user/save-details", baseForm, () => {
        ElMessage.success("用户信息保存成功")
        store.user.username = baseForm.username
        desc.value = baseForm.desc
        loading.base = false
      }, (message) => {
        ElMessage.warning(message)
        loading.base = false
      })
    }
  })
}

get('api/user/details', data => {
  baseForm.username = store.user.username
  baseForm.phone = data.phone
  baseForm.gender = data.gender
  baseForm.qq = data.qq
  baseForm.wx = data.wx
  desc.value = data.desc
  emailForm.email = store.user.email
  baseForm.desc = data.desc
})

const isEmailValid = ref(false)
const coldTime = ref(0)

const onValidate = (prop, isValid) => {
  if(prop === 'email')
    isEmailValid.value = isValid
}

const sendCode = () => {
  emailFormRef.value.validate((isValid) => {
    // 1. 启动倒计时“引擎”并设置初始时间
    // 这个逻辑现在是独立的，不放在任何回调里
    coldTime.value = 60;
    const handle = setInterval(() => {
      coldTime.value--;
      if (coldTime.value <= 0) { // 使用 <= 0 更安全
        clearInterval(handle);
      }
    }, 1000);

    get(`api/auth/ask-code?email=${emailForm.email}&type=modify`,
        () => {
          ElMessage.success(`验证码已成功发送至邮件: ${emailForm.email}, 请注意查收`)
        },
        (message) => {
          ElMessage.warning(message)
        })
  })

}

const updateEmail = () => {
  emailFormRef.value.validate((isValid) => {
    post(`api/user/modify-email`,emailForm ,() => {
      ElMessage.success("邮件更新成功")
    })
  })

}
</script>

<template>
  <div style="display: flex">
    <!-- 左侧设置区域 -->
    <div class="setting-left">
      <!-- 账号信息设置卡片 -->
      <card :icon="User" title="账号信息设置" desc="您可以在这里编辑您的个人信息，并在隐私设置里决定是否对外展示">
        <!--
          修改 el-form:
          1. label-position="right"  -> 标签在右侧
          2. label-width="80px"      -> 标签宽度统一为80px
          3. style 添加 max-width: 500px -> 限制表单最大宽度，输入框就不会太长
        -->
        <el-form :model="baseForm" :rules="rules" ref="baseFormRef" label-position="right" label-width="80px" style="margin: 0 10px 10px 10px; max-width: 500px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="baseForm.username" maxlength="10"/>
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="baseForm.gender">
              <el-radio :label="0">男</el-radio>
              <el-radio :label="1">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="baseForm.phone" maxlength="11"/>
          </el-form-item>
          <el-form-item label="QQ号" prop="qq">
            <el-input v-model="baseForm.qq" maxlength="13"/>
          </el-form-item>
          <el-form-item label="微信号" prop="wx">
            <el-input v-model="baseForm.wx" maxlength="20"/>
          </el-form-item>
          <el-form-item label="个人简介" prop="desc">
            <el-input v-model="baseForm.desc" type="textarea" :rows="6" maxlength="200"/>
          </el-form-item>
          <div>
            <!-- 为了让按钮和表单项对齐，可以给它也加上左边距 -->
            <el-button
                @click="saveDetails"
                :loading="loading.base"
                :disabled="loading.base"
                :icon="loading.base ? undefined : Select"
                type="success"
                style="margin-left: 80px;">
              保存用户信息
            </el-button>
          </div>
        </el-form>
      </card>

      <!-- 电子邮件设置卡片 -->
      <card :icon="Message" style="margin-top: 10px" title="电子邮件设置" desc="您可以在这里修改默认绑定的电子邮件地址">
        <el-form :model="emailForm" @validate="onValidate" :rules="rules" ref="emailFormRef" label-position="right" label-width="110px" style="margin: 0 10px 10px 10px; max-width: 500px">
          <el-form-item label="电子邮件地址" prop="email">
            <el-input v-model="emailForm.email"/>
          </el-form-item>
          <el-form-item label="验证码" prop="code">
            <el-row style="width: 100%" gutter="10">
              <el-col :span="18">
                <el-input v-model="emailForm.code" placeholder="请获取验证码"/>
              </el-col>
              <el-col :span="6">
                <el-button type="success" @click="sendCode"
                           :disabled="!isEmailValid || coldTime > 0">
                  {{coldTime > 0 ? '请稍后 ' + coldTime + ' 秒' : '获取验证码'}}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <div>
            <el-button type="success" :icon="Refresh" style="margin-left: 100px;">更新电子邮件</el-button>
          </div>
        </el-form>
      </card>
    </div>

    <!-- 右侧信息展示区域 -->
    <div class="setting-right">
      <div style="position: sticky;top: 20px">
        <card>
          <div style="text-align: center; padding: 15px 5px 0 5px">
            <el-avatar size="70" src="https://www.itbaima.cn/image/avatar/default.webp"></el-avatar>
            <div style="font-weight: bold">你好, {{store.user.username}}</div>
          </div>
          <el-divider style="margin: 10px 0"></el-divider>
          <div style="font-size: 14px;color: grey;padding: 10px">
            {{desc || '我是个人简介'}}
          </div>
        </card>
        <card style="margin-top: 10px; font-size: 14px;padding: 15px;">
          <div><strong>注册时间:</strong> {{registerTime}}</div>
          <div style="color: grey;margin-top: 5px;">欢迎加入我们的学习论坛</div>
        </card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.setting-left {
  flex: 1;
  margin: 20px 100px 20px 100px;
}
.setting-right {
  width: 300px;
  margin: 20px 20px 20px 0; /* 调整右边距，让左右区域更协调 */
}
</style>