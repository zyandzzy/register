<script setup>
defineProps(
    {
      icon: Object,
      title: String,
      desc: String
    }
)
</script>

<template>
  <div class="card">
    <div class="card-header" v-if="title">
      <div>
        <el-icon style="margin-right: 3px; translate: 0 2px">
          <component :is="icon" />
        </el-icon>
        {{title}}
      </div>
      <div>{{desc}}</div>
    </div>
    <slot/>
  </div>
</template>

<style scoped>

/* 卡片基础样式 */
.card {
  /* 设置5像素的圆角 */
  border-radius: 5px;
  /* 设置1像素的实线边框，颜色使用CSS变量 --el-border-color */
  border: solid 1px var(--el-border-color);
  /*设置背景颜色*/
  background-color: var(--el-bg-color);

  box-sizing: border-box;
  padding: 10px;
  min-height: 20px;
}

/* 卡片头部样式 */
.card-header {
  /* 添加底部边框，用于分隔头部和主体内容 */
  border-bottom: solid 1px var(--el-border-color);
  /* 设置底部内边距，让内容和边框之间有一些间距 */
  padding-bottom: 5px;
  /* 设置底部外边距，让头部和主体内容之间有一些间距 */
  margin-bottom: 10px;

  &>:first-child {
    font-size: 18px;
    font-weight: bold;
  }

  &>:last-child {
    font-size: 13px;
    color: grey;
  }
}
</style>