<script setup>
import Card from "@/components/Card.vue";
import {Message, Refresh, Select, User} from "@element-plus/icons-vue";
import {useStore} from "@/store";
import {computed} from "vue";

const store = useStore()

// 计算注册时间并格式化
const registerTime = computed(() => new Date(store.user.registerTime).toLocaleString())
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
        <el-form label-position="right" label-width="80px" style="margin: 0 10px 10px 10px; max-width: 500px">
          <el-form-item label="用户名">
            <el-input/>
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group>
              <el-radio label="1">男</el-radio>
              <el-radio label="2">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input/>
          </el-form-item>
          <el-form-item label="QQ号">
            <el-input/>
          </el-form-item>
          <el-form-item label="微信号">
            <el-input/>
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input type="textarea" :rows="6"/>
          </el-form-item>
          <div>
            <!-- 为了让按钮和表单项对齐，可以给它也加上左边距 -->
            <el-button :icon="Select" type="success" style="margin-left: 80px;">保存用户信息</el-button>
          </div>
        </el-form>
      </card>

      <!-- 电子邮件设置卡片 -->
      <card :icon="Message" style="margin-top: 10px" title="电子邮件设置" desc="您可以在这里修改默认绑定的电子邮件地址">
        <el-form label-position="right" label-width="100px" style="margin: 0 10px 10px 10px; max-width: 500px">
          <el-form-item label="电子邮件地址">
            <el-input/>
          </el-form-item>
          <el-form-item label="验证码">
            <el-row style="width: 100%" gutter="10">
              <el-col :span="18">
                <el-input placeholder="请获取验证码"/>
              </el-col>
              <el-col :span="6">
                <el-button type="success" plain style="width: 100%">获取验证码</el-button>
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
            我们专注于计算机科学相关学科教研，主要在Bilibili平台发布视频教程，经过两年时间的努力，目前已经包含整套Java开发初级到中级技术栈 / C语言与数据结构系列课程，足以保证广大应届 …
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