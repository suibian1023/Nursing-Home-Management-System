<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-input v-model="search.keyword" placeholder="搜索客户姓名" style="width:200px" clearable @clear="loadData" @keyup.enter="loadData" />
        <el-select v-model="search.approvalStatus" placeholder="审批状态" clearable style="width:140px" @change="loadData">
          <el-option label="待审批" :value="2" />
          <el-option label="已驳回" :value="3" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top:16px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="customerName" label="老人姓名" width="100" />
        <el-table-column prop="outDate" label="外出日期" width="110" />
        <el-table-column prop="expectBackDate" label="预计返回" width="110" />
        <el-table-column prop="reason" label="外出原因" min-width="160" show-overflow-tooltip />
        <el-table-column prop="accompanyName" label="陪同人" width="100" />
        <el-table-column label="申请状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">外出中</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">已返回</el-tag>
            <el-tag v-else-if="row.status === 2" type="warning">待审批</el-tag>
            <el-tag v-else-if="row.status === 3" type="danger">已驳回</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 2">
              <el-button size="small" type="success" @click="handleApprove(row, 1)">通过</el-button>
              <el-button size="small" type="danger" @click="handleApprove(row, 2)">驳回</el-button>
            </template>
            <el-button v-if="row.status === 3" size="small" type="success" @click="handleApprove(row, 1)">通过</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="search.pageNum"
        v-model:page-size="search.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @current-change="loadData"
        @size-change="loadData"
        style="margin-top:16px; justify-content:flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOutwardPage, approveOutward } from '@/api'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const search = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  approvalStatus: null
})

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: search.pageNum,
      pageSize: search.pageSize,
      keyword: search.keyword || undefined,
      approvalStatus: search.approvalStatus
    }
    const res = await getOutwardPage(params)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const handleApprove = async (row, approvalStatus) => {
  const label = approvalStatus === 1 ? '通过' : '驳回'
  await ElMessageBox.confirm(`确定${label}该外出申请吗？`, '审批确认', { type: 'warning' })
  await approveOutward(row.id, approvalStatus)
  ElMessage.success(`已${label}`)
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.search-bar { display: flex; gap: 12px; align-items: center; }
</style>
