<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <h3>东软颐养中心</h3>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#1c1c1c"
        text-color="rgba(252,251,248,0.65)"
        active-text-color="#fcfbf8"
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
  Promotion, Remove, List, Bell
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta?.title || '')

// 完整菜单配置
const allMenus = [
  {
    key: 'welcome', title: '欢迎页', icon: HomeFilled, path: '/welcome'
  },
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
  },
  {
    key: 'food-order', title: '点餐订单', icon: Food, path: '/order'
  },
  {
    key: 'call-manage', title: '呼叫管理', icon: Bell, path: '/callmanage'
  },
  {
    key: 'call-nurse', title: '呼叫管家', icon: Bell, path: '/call'
  }
]

// 各角色的菜单白名单
const roleMenus = {
  1: null, // 管理员看全部
  2: new Set(['customer-out', 'nurse-record', 'room', 'bed', 'bed-diagram', 'food-item', 'food-meal', 'food-order', 'call-manage']),
  3: new Set(['welcome', 'food-order', 'call-nurse'])
}

// 当前可见菜单
const visibleMenus = ref(allMenus)

onMounted(() => {
  const roleId = userStore.userInfo?.roleId || 1
  const allowed = roleMenus[roleId]

  if (allowed === null) {
    // 管理员：显示所有菜单（不含welcome和call-nurse，这些是给老人用的）
    visibleMenus.value = allMenus.filter(m => !['welcome', 'call-nurse'].includes(m.key))
  } else {
    // 其他角色：按白名单过滤
    visibleMenus.value = allMenus
      .map(menu => {
        if (!menu.children) {
          return allowed.has(menu.key) ? menu : null
        }
        const visibleChildren = menu.children.filter(c => allowed.has(c.key))
        return visibleChildren.length > 0
          ? { ...menu, children: visibleChildren }
          : null
      })
      .filter(Boolean)
  }

  // 根据角色设置默认首页
  if (roleId === 3 && route.path === '/dashboard') {
    router.replace('/welcome')
  }
})

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container { height: 100vh; }
.sidebar {
  background-color: #1c1c1c;
  overflow-y: auto;
  border-right: none;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}
.logo h3 {
  color: #fcfbf8;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: -0.5px;
  margin: 0;
}
.topbar {
  background: #f7f4ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #eceae4;
  padding: 0 20px;
}
.topbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.username {
  color: rgba(28, 28, 28, 0.82);
  font-size: 14px;
  font-weight: 500;
}
.el-main {
  background: #f7f4ed;
  padding: 24px;
}
</style>

<style>
/* Non-scoped styles to penetrate el-menu internals */
.sidebar .el-menu {
  border-right: none !important;
}
.sidebar .el-menu-item {
  margin: 2px 8px;
  border-radius: 8px;
  height: 44px;
  line-height: 44px;
  transition: all 0.15s ease;
}
.sidebar .el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.08) !important;
}
.sidebar .el-menu-item.is-active {
  background-color: rgba(255, 255, 255, 0.12) !important;
}
.sidebar .el-sub-menu__title {
  margin: 2px 8px;
  border-radius: 8px;
  transition: all 0.15s ease;
}
.sidebar .el-sub-menu__title:hover {
  background-color: rgba(255, 255, 255, 0.08) !important;
}
.sidebar .el-menu--inline {
  background-color: transparent !important;
}
.sidebar .el-menu--inline .el-menu-item {
  padding-left: 52px !important;
  font-size: 13px;
}
.sidebar .el-sub-menu .el-menu-item {
  background-color: transparent !important;
}
.sidebar .el-sub-menu .el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.08) !important;
}
</style>
