<script setup>
import {get, logout} from '@/net'
import router from "@/router";
import {useStore} from "@/store";
import {ref} from "vue";

const store = useStore()
const loading = ref(true)

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
        <div style="flex: 1" class="user-info">
          <div class="profile">
            <div>{{store.user.username}}</div>
            <div>{{store.user.email}}</div>
          </div>

          <el-avatar src="https://www.itbaima.cn/image/avatar/default.webp"></el-avatar>
        </div>

      </el-header>
      <el-container>
        <el-aside width="200px">Aside</el-aside>
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
</style>
