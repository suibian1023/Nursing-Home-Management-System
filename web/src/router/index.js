import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '工作台', icon: 'HomeFilled' }
      },
      {
        path: 'user',
        name: 'UserList',
        component: () => import('@/views/user/UserList.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'customer',
        name: 'CustomerList',
        component: () => import('@/views/customer/CustomerList.vue'),
        meta: { title: '客户管理', icon: 'UserFilled' }
      },
      {
        path: 'bed',
        name: 'BedList',
        component: () => import('@/views/bed/BedList.vue'),
        meta: { title: '床位管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'beddiagram',
        name: 'BedDiagram',
        component: () => import('@/views/bed/BedDiagram.vue'),
        meta: { title: '房位示意图', icon: 'Grid' }
      },
      {
        path: 'room',
        name: 'RoomList',
        component: () => import('@/views/room/RoomList.vue'),
        meta: { title: '房间管理', icon: 'House' }
      },
      {
        path: 'food',
        name: 'FoodList',
        component: () => import('@/views/food/FoodList.vue'),
        meta: { title: '菜品管理', icon: 'Food' }
      },
      {
        path: 'meal',
        name: 'MealList',
        component: () => import('@/views/meal/MealList.vue'),
        meta: { title: '套餐管理', icon: 'Dish' }
      },
      {
        path: 'nurselevel',
        name: 'NurseLevelList',
        component: () => import('@/views/nurse/NurseLevelList.vue'),
        meta: { title: '护理等级', icon: 'Stamp' }
      },
      {
        path: 'nursecontent',
        name: 'NurseContentList',
        component: () => import('@/views/nurse/NurseContentList.vue'),
        meta: { title: '护理内容', icon: 'List' }
      },
      {
        path: 'nurserecord',
        name: 'NurseRecordList',
        component: () => import('@/views/nurse/NurseRecordList.vue'),
        meta: { title: '护理记录', icon: 'Document' }
      },
      {
        path: 'outward',
        name: 'OutwardList',
        component: () => import('@/views/outward/OutwardList.vue'),
        meta: { title: '外出管理', icon: 'Promotion' }
      },
      {
        path: 'backdown',
        name: 'BackdownList',
        component: () => import('@/views/backdown/BackdownList.vue'),
        meta: { title: '退住管理', icon: 'Remove' }
      },
      {
        path: 'approval',
        name: 'ApprovalList',
        component: () => import('@/views/approval/ApprovalList.vue'),
        meta: { title: '审批管理', icon: 'Promotion' }
      },
      {
        path: 'welcome',
        name: 'Welcome',
        component: () => import('@/views/Welcome.vue'),
        meta: { title: '欢迎页', icon: 'HomeFilled' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})

export default router
