<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-input v-model="search.keyword" placeholder="搜索姓名/房间号" style="width:200px" clearable @clear="loadData" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">查询</el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top:16px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="customerName" label="老人姓名" width="120" />
        <el-table-column prop="roomNo" label="房间号" width="100">
          <template #default="{ row }">
            <el-tag type="danger" effect="dark" v-if="row.status === 0">{{ row.roomNo }}</el-tag>
            <span v-else>{{ row.roomNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="呼叫时间" width="170" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'danger' : 'success'">
              {{ row.status === 0 ? '呼叫中' : '已响应' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" size="small" type="success" @click="handleRespond(row)">响应</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="search.pageNum"
        v-model:page-size="search.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @current-change="loadData" @size-change="loadData"
        style="margin-top:16px; justify-content:flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCallRecordPage, respondCall, deleteCallRecord } from '@/api'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const search = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCallRecordPage(search)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

const handleRespond = async (row) => {
  await respondCall(row.id)
  ElMessage.success('已响应呼叫')
  loadData()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该呼叫记录吗？', '提示', { type: 'warning' })
  await deleteCallRecord(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
// Auto-refresh every 10 seconds to catch new calls
const timer = setInterval(loadData, 10000)
import { onBeforeUnmount } from 'vue'
onBeforeUnmount(() => clearInterval(timer))
</script>

<style scoped>
.search-bar { display: flex; gap: 12px; }
</style>
