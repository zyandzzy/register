<script setup>
import {get, logout} from '@/net'
import router from "@/router";
import {useStore} from "@/store";
import {reactive, ref} from "vue";
import {
  Back,
  Bell, Calendar,
  ChatDotSquare, Collection, DataLine,
  Document, Files,
  Location, Lock, Message, Monitor,
  Notification, Operation,
  Position,
  School, Search,
  Umbrella, User
} from "@element-plus/icons-vue";

const store = useStore()
const loading = ref(true)
const searchInput = reactive({
  type: '1',
  text: ''
})

get("api/user/info", (data) => {
  store.user = data
  loading.value = false
})

function userLogout() {
  logout(() => router.push("/"))
}
</script>

<template>
  <div class="main-content" v-loading="loading" element-loading-text="正在加载,请稍后···">
    <el-container style="height: 100vh" v-if="!loading">
      <el-header class="main-content-header">
        <el-image class="logo" src="https://th.bing.com/th/id/OSAAS.2AF8D47DEA75F028F377DABC19B3040C?w=72&h=72&c=17&rs=1&o=6&dpr=1.3&pid=TechQna"></el-image>
        <div style="flex: 1; padding: 0 20px; text-align: center;">
            <el-input v-model="searchInput.text" style="width:100%; max-width: 500px" placeholder="搜索论坛相关内容···" >
              <template #prefix>
                <el-icon><Search/></el-icon>
              </template>
              <template #append>
                <el-select style="width: 120px" v-model="searchInput.type">
                  <el-option value="1" label="帖子广场"/>
                  <el-option value="2" label="校园活动"/>
                  <el-option value="3" label="表白墙"/>
                  <el-option value="4" label="教务通知"/>
                </el-select>
              </template>
            </el-input>
        </div>
        <div  class="user-info">
          <div class="profile">
            <div>{{store.user.username}}</div>
            <div>{{store.user.email}}</div>
          </div>
          <div>
            <el-dropdown>
              <el-avatar src="https://www.itbaima.cn/image/avatar/default.webp"></el-avatar>
              <template #dropdown>
                <el-dropdown-item>
                 <el-icon><operation/></el-icon>
                  个人设置
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-icon><message/></el-icon>
                  消息列表
                </el-dropdown-item>
                <el-dropdown-item @click="userLogout" divided>
                  <el-icon><back/></el-icon>
                  退出登录
                </el-dropdown-item>

              </template>
            </el-dropdown>
          </div>

        </div>

      </el-header>
      <el-container>
        <el-aside width="200px">
          <el-scrollbar style="height: calc(100vh - 75px)">
            <el-menu
                default-active="1-1"
                style="min-height: calc(100vh - 75px)"
            >
              <el-sub-menu index="1">
                <template #title>
                  <el-icon><location/></el-icon>
                  校园论坛
                </template>
                <el-menu-item index="1-1">
                  <template #title>
                    <el-icon><chat-dot-square/></el-icon>
                    帖子广场
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><bell/></el-icon>
                    失物招领
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Notification/></el-icon>
                    校园活动
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Umbrella/></el-icon>
                    表白墙
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><school/></el-icon>
                    海文考研
                    <el-tag style="margin-left: 10px" size="small">合作机构</el-tag>
                  </template>
                </el-menu-item>
              </el-sub-menu>
              <el-sub-menu index="2" >
                <template #title>
                  <el-icon><Position/></el-icon>
                  探索与发现
                </template>
                <el-menu-item>
                  <template #title>
                    <el-icon><document/></el-icon>
                    成绩查询
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><files/></el-icon>
                    课程表
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><calendar/></el-icon>
                    任务清单
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><monitor/></el-icon>
                    教务通知
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><collection/></el-icon>
                    在线图书馆
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><data-line/></el-icon>
                    预约教室
                  </template>
                </el-menu-item>
              </el-sub-menu>
              <el-sub-menu index="3">
                <template #title>
                  <el-icon><operation/></el-icon>
                  个人设置
                </template>
                <el-menu-item>
                  <template #title>
                    <el-icon><user/></el-icon>
                    个人信息设置
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><lock/></el-icon>
                    账号安全设置
                  </template>
                </el-menu-item>
              </el-sub-menu>
            </el-menu>
          </el-scrollbar>

        </el-aside>
        <el-main>Main</el-main>
      </el-container>
    </el-container>
  </div>
</template>


<style lang="less" scoped>
.main-content {
  height: 100vh;
  width: 100vw;
}

.main-content-header {
  border-bottom: solid 1px var(--el-border-color);
  height: 75px;
  display: flex;
  align-items: center;
  box-sizing: border-box;

  .logo {
    height: 55px;
  }

  .user-info {
    display: flex;
    align-items: center;
    justify-content: flex-end;

    .el-avatar:hover {
      cursor: pointer;
    }

    .profile {
      text-align: right;
      margin-right: 20px;

      :first-child {
        font-weight: bold;
        font-size: 25px;
        line-height: 30px;
      }
      :last-child {
        font-size: 18px;
        color: grey;
      }
    }
  }


}
</style>
