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
        <template v-for="menu in visibleMenus" :key="menu.key">
          <!-- 叶子菜单 -->
          <el-menu-item v-if="!menu.children || menu.children.length === 0" :index="menu.path">
            <el-icon><component :is="menu.icon" /></el-icon>
            <span>{{ menu.title }}</span>
          </el-menu-item>
          <!-- 子菜单 -->
          <el-sub-menu v-else :index="menu.key">
            <template #title>
              <el-icon><component :is="menu.icon" /></el-icon>
              <span>{{ menu.title }}</span>
            </template>
            <el-menu-item
              v-for="child in menu.children"
              :key="child.key"
              :index="child.path"
            >{{ child.title }}</el-menu-item>
          </el-sub-menu>
        </template>
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
          <el-tag size="small" :type="userStore.userInfo?.roleId === 1 ? 'danger' : 'success'">
            {{ userStore.userInfo?.roleName || '用户' }}
          </el-tag>
          <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '管理员' }}</span>
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
import { computed, ref, onMounted, shallowRef } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getMenuByRole } from '@/api'
import {
  HomeFilled, Setting, UserFilled, OfficeBuilding, Stamp, Food,
  User, Avatar, Menu as MenuIcon, House, Grid, Document,
  Promotion, Remove, List
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta?.title || '')

// 完整菜单配置（与数据库 menusIndex 对应）
const allMenus = [
  {
    key: 'dashboard', title: '工作台', icon: HomeFilled, path: '/dashboard'
  },
  {
    key: 'system', title: '系统管理', icon: Setting, children: [
      { key: 'system-user', title: '用户管理', path: '/user' }
    ]
  },
  {
    key: 'customer', title: '老人管理', icon: UserFilled, children: [
      { key: 'customer-info', title: '客户管理', path: '/customer' },
      { key: 'customer-out', title: '外出管理', path: '/outward' },
      { key: 'customer-back', title: '退住管理', path: '/backdown' }
    ]
  },
  {
    key: 'resource', title: '资源管理', icon: OfficeBuilding, children: [
      { key: 'room', title: '房间管理', path: '/room' },
      { key: 'bed', title: '床位管理', path: '/bed' },
      { key: 'bed-diagram', title: '房位示意图', path: '/beddiagram' }
    ]
  },
  {
    key: 'nursing', title: '护理管理', icon: Stamp, children: [
      { key: 'nurse-level', title: '护理等级', path: '/nurselevel' },
      { key: 'nurse-content', title: '护理内容', path: '/nursecontent' },
      { key: 'nurse-record', title: '护理记录', path: '/nurserecord' }
    ]
  },
  {
    key: 'meal-group', title: '餐饮管理', icon: Food, children: [
      { key: 'food-item', title: '菜品管理', path: '/food' },
      { key: 'food-meal', title: '套餐管理', path: '/meal' }
    ]
  }
]

// 当前可见菜单
const visibleMenus = ref(allMenus)

onMounted(async () => {
  const roleId = userStore.userInfo?.roleId
  if (roleId === 1) {
    // 管理员：显示所有菜单
    visibleMenus.value = allMenus
  } else if (roleId) {
    // 非管理员：从后端获取允许的菜单
    try {
      const res = await getMenuByRole(roleId)
      const allowedKeys = new Set((res.data || []).map(m => m.menusIndex))
      // 过滤菜单：保留有权限的菜单项
      visibleMenus.value = allMenus
        .map(menu => {
          if (!menu.children) {
            return allowedKeys.has(menu.key) ? menu : null
          }
          const visibleChildren = menu.children.filter(c => allowedKeys.has(c.key))
          return visibleChildren.length > 0
            ? { ...menu, children: visibleChildren }
            : null
        })
        .filter(Boolean)
    } catch (e) {
      console.error('获取菜单权限失败:', e)
      visibleMenus.value = allMenus
    }
  }
})

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
