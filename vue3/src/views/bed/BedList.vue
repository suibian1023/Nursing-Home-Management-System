<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-input v-model="search.roomNo" placeholder="搜索房间号" style="width:200px" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button type="success" @click="openAdd">新增床位</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top:16px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="bedNo" label="床位号" width="120" />
        <el-table-column prop="roomNo" label="房间号" width="120" />
        <el-table-column prop="isUsed" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.isUsed)">
              {{ statusLabel(row.isUsed) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
            <el-button
              v-if="row.isUsed !== 0"
              size="small"
              type="success"
              @click="changeStatus(row, 0)"
            >置空闲</el-button>
            <el-button
              v-if="row.isUsed === 2"
              size="small"
              type="warning"
              @click="changeStatus(row, 1)"
            >置占用</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑床位' : '新增床位'" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="床位号" prop="bedNo"><el-input v-model="form.bedNo" /></el-form-item>
        <el-form-item label="房间号" prop="roomNo"><el-input v-model="form.roomNo" /></el-form-item>
        <el-form-item label="状态" prop="isUsed">
          <el-select v-model="form.isUsed" style="width:100%">
            <el-option label="空闲" :value="0" />
            <el-option label="占用" :value="1" />
            <el-option label="待打扫" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBedPage, addBed, updateBed, deleteBed, getBedById, updateBedStatus } from '@/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const search = reactive({ roomNo: '' })

const form = reactive({ id: null, bedNo: '', roomNo: '', isUsed: 0 })
const rules = {
  bedNo: [{ required: true, message: '请输入床位号', trigger: 'blur' }],
  roomNo: [{ required: true, message: '请输入房间号', trigger: 'blur' }]
}

const statusLabel = (v) => {
  if (v === 1) return '占用'
  if (v === 2) return '待打扫'
  return '空闲'
}

const statusTagType = (v) => {
  if (v === 1) return 'danger'
  if (v === 2) return 'warning'
  return 'success'
}

const changeStatus = async (row, newStatus) => {
  await updateBedStatus({ id: row.id, isUsed: newStatus })
  ElMessage.success(`已将 ${row.bedNo} 置为${statusLabel(newStatus)}`)
  loadData()
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { pageNum: pagination.pageNum, pageSize: pagination.pageSize }
    if (search.roomNo) params.roomNo = search.roomNo
    const res = await getBedPage(params)
    const pageData = res.data || {}
    tableData.value = pageData.records || []
    pagination.total = pageData.total || 0
  } finally { loading.value = false }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleSizeChange = () => {
  pagination.pageNum = 1
  loadData()
}

const handlePageChange = () => {
  loadData()
}

const openAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }
const openEdit = async (row) => { isEdit.value = true; const res = await getBedById(row.id); Object.assign(form, res.data); dialogVisible.value = true }

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该床位吗？', '提示', { type: 'warning' })
  await deleteBed(id)
  ElMessage.success('删除成功')
  loadData()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (isEdit.value) { await updateBed(form); ElMessage.success('修改成功') }
  else { await addBed(form); ElMessage.success('新增成功') }
  dialogVisible.value = false
  loadData()
}

const resetForm = () => {
  Object.assign(form, { id: null, bedNo: '', roomNo: '', isUsed: 0 })
  formRef.value?.resetFields()
}

onMounted(loadData)
</script>

<style scoped>
.search-bar { display: flex; gap: 12px; }
.pagination-bar { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
