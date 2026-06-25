<template>
  <div class="welcome-page">
    <div class="welcome-card">
      <div class="welcome-icon">
        <el-icon :size="80" color="#1c1c1c"><HomeFilled /></el-icon>
      </div>
      <h1>欢迎来到东软颐养中心</h1>
      <p class="subtitle">祝您在这里生活愉快、健康长寿</p>
      <div class="info-cards">
        <div class="info-card">
          <el-icon :size="36" color="#2d6a4f"><Food /></el-icon>
          <h3>点餐服务</h3>
          <p>可以在"点餐"页面选择喜欢的菜品，我们会准时为您准备</p>
        </div>
        <div class="info-card">
          <el-icon :size="36" color="#b8860b"><Bell /></el-icon>
          <h3>呼叫管家</h3>
          <p>如需帮助，可以在"呼叫管家"页面一键呼叫，管家会尽快响应</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { HomeFilled, Food, Bell } from '@element-plus/icons-vue'
</script>

<style scoped>
.welcome-page { display: flex; justify-content: center; align-items: center; min-height: calc(100vh - 140px); }
.welcome-card { text-align: center; padding: 60px 40px; background: #fcfbf8; border: 1px solid #eceae4; border-radius: 16px; max-width: 700px; }
.welcome-icon { margin-bottom: 24px; }
.welcome-card h1 { font-size: 32px; color: #1c1c1c; margin-bottom: 8px; font-weight: 600; letter-spacing: -1.2px; }
.subtitle { font-size: 16px; color: #5f5f5d; margin-bottom: 48px; line-height: 1.5; }
.info-cards { display: flex; gap: 24px; justify-content: center; }
.info-card { flex: 1; padding: 32px 20px; border: 1px solid #eceae4; border-radius: 12px; background: #f7f4ed; transition: box-shadow 0.15s ease; }
.info-card:hover { box-shadow: rgba(0, 0, 0, 0.06) 0px 2px 8px; }
.info-card h3 { margin: 16px 0 8px; color: #1c1c1c; font-size: 18px; font-weight: 600; letter-spacing: -0.5px; }
.info-card p { color: #5f5f5d; font-size: 14px; line-height: 1.6; }
</style>
