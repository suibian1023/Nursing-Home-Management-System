<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span style="font-size:18px;font-weight:bold">呼叫管家</span>
        </div>
      </template>

      <div class="call-section">
        <div class="call-btn-wrapper">
          <el-button
            type="danger"
            :size="'large'"
            round
            :loading="calling"
            @click="callNurse"
            style="width:200px;height:200px;font-size:24px;border-radius:50%"
          >
            {{ calling ? '呼叫中...' : '呼叫管家' }}
          </el-button>
          <p class="call-tip">如需帮助，请点击按钮呼叫管家</p>
        </div>

        <el-divider />

        <h3 style="margin-bottom:16px">呼叫记录</h3>
        <el-table :data="callHistory" border stripe v-loading="loading" max-height="400">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="roomNo" label="房间号" width="100" />
          <el-table-column prop="createTime" label="呼叫时间" width="170" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 0 ? 'warning' : 'success'">
                {{ row.status === 0 ? '等待响应' : '已响应' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { addCallRecord, getCallRecordPage } from '@/api'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const calling = ref(false)
const loading = ref(false)
const callHistory = ref([])

const callNurse = async () => {
  calling.value = true
  try {
    await addCallRecord({
      customerId: userStore.userInfo?.customerId || userStore.userInfo?.userId,
      roomNo: userStore.userInfo?.roomNo || '',
      status: 0
    })
    ElMessage.success('已呼叫管家，请耐心等待！')
    loadHistory()
  } catch (e) {
    ElMessage.error('呼叫失败，请重试')
  } finally {
    calling.value = false
  }
}

const loadHistory = async () => {
  loading.value = true
  try {
    const res = await getCallRecordPage({ pageNum: 1, pageSize: 20 })
    callHistory.value = res.data?.records || []
  } finally { loading.value = false }
}

onMounted(loadHistory)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.call-section { text-align: center; }
.call-btn-wrapper { padding: 40px 0; }
.call-tip { margin-top: 20px; color: #5f5f5d; font-size: 16px; }
</style>
