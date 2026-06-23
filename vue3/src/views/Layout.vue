<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <h3>东软颐养中心</h3>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>工作台</span>
        </el-menu-item>
        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/user">用户管理</el-menu-item>
          <el-menu-item index="/role">角色管理</el-menu-item>
          <el-menu-item index="/menu">菜单管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="elder">
          <template #title>
            <el-icon><UserFilled /></el-icon>
            <span>老人管理</span>
          </template>
          <el-menu-item index="/customer">客户管理</el-menu-item>
          <el-menu-item index="/outward">外出管理</el-menu-item>
          <el-menu-item index="/backdown">退住管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="resource">
          <template #title>
            <el-icon><OfficeBuilding /></el-icon>
            <span>资源管理</span>
          </template>
          <el-menu-item index="/room">房间管理</el-menu-item>
          <el-menu-item index="/bed">床位管理</el-menu-item>
          <el-menu-item index="/beddiagram">房位示意图</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="nurse">
          <template #title>
            <el-icon><Stamp /></el-icon>
            <span>护理管理</span>
          </template>
          <el-menu-item index="/nurselevel">护理等级</el-menu-item>
          <el-menu-item index="/nursecontent">护理内容</el-menu-item>
          <el-menu-item index="/nurserecord">护理记录</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="meal-group">
          <template #title>
            <el-icon><Food /></el-icon>
            <span>餐饮管理</span>
          </template>
          <el-menu-item index="/food">菜品管理</el-menu-item>
          <el-menu-item index="/meal">套餐管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="topbar">
        <div class="topbar-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentTitle">{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="topbar-right">
          <span class="username">{{ userStore.userInfo?.realName || userStore.userInfo?.username || '管理员' }}</span>
          <el-button text @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta?.title || '')

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container { height: 100vh; }
.sidebar { background-color: #304156; overflow-y: auto; }
.logo { height: 60px; display: flex; align-items: center; justify-content: center; }
.logo h3 { color: #fff; font-size: 16px; margin: 0; }
.topbar { background: #fff; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #e6e6e6; padding: 0 20px; }
.topbar-right { display: flex; align-items: center; gap: 12px; }
.username { color: #606266; font-size: 14px; }
.el-main { background: #f0f2f5; padding: 20px; }
</style>
