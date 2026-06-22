<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="card in cards" :key="card.title">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon" :style="{ background: card.color }">
              <el-icon :size="28"><component :is="card.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ card.value }}</div>
              <div class="stat-title">{{ card.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-card style="margin-top:20px">
      <template #header>欢迎使用东软颐养中心管理系统</template>
      <p>本系统用于管理养老院的日常运营，包括老人信息管理、床位分配、护理服务、餐饮管理等。</p>
      <p>请通过左侧菜单导航到各功能模块。</p>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCustomerPage, getBedList } from '@/api'

const cards = ref([
  { title: '在住老人', value: 0, icon: 'UserFilled', color: '#409EFF' },
  { title: '总床位', value: 0, icon: 'OfficeBuilding', color: '#67C23A' },
  { title: '空余床位', value: 0, icon: 'HomeFilled', color: '#E6A23C' },
  { title: '员工人数', value: 0, icon: 'Avatar', color: '#F56C6C' }
])

onMounted(async () => {
  try {
    const [custRes, bedRes] = await Promise.all([
      getCustomerPage({ pageNum: 1, pageSize: 1 }),
      getBedList()
    ])
    cards.value[0].value = custRes.data?.total || 0
    if (bedRes.data) {
      const beds = bedRes.data || []
      const total = beds.length
      const used = beds.filter(b => b.isUsed === 1).length
      cards.value[1].value = total
      cards.value[2].value = total - used
    }
  } catch { /* ignore */ }
})
</script>

<style scoped>
.stat-card { display: flex; align-items: center; gap: 16px; }
.stat-icon { width: 56px; height: 56px; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: #fff; }
.stat-value { font-size: 24px; font-weight: bold; color: #303133; }
.stat-title { font-size: 13px; color: #909399; margin-top: 4px; }
</style>
