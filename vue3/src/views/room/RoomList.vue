<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-input v-model="search.keyword" placeholder="搜索房间号/房型" style="width:200px" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button type="success" @click="openAdd">新增房间</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top:16px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="roomNo" label="房间号" width="120" />
        <el-table-column prop="buildingNo" label="楼号" width="80" />
        <el-table-column prop="roomType" label="房型" width="100" />
        <el-table-column prop="floor" label="楼层" width="80" />
        <el-table-column prop="bedCount" label="床位总数" width="80" />
        <el-table-column prop="emptyBed" label="空床数" width="80" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑房间' : '新增房间'" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="房间号" prop="roomNo">
          <el-input v-model="form.roomNo" />
        </el-form-item>
        <el-form-item label="楼号" prop="buildingNo">
          <el-input v-model="form.buildingNo" />
        </el-form-item>
        <el-form-item label="房型" prop="roomType">
          <el-select v-model="form.roomType" style="width:100%">
            <el-option label="单人间" value="单人间" />
            <el-option label="双人间" value="双人间" />
            <el-option label="三人间" value="三人间" />
            <el-option label="四人间" value="四人间" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼层" prop="floor">
          <el-input-number v-model="form.floor" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="床位总数" prop="bedCount">
          <el-input-number v-model="form.bedCount" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="空床数" prop="emptyBed">
          <el-input-number v-model="form.emptyBed" :min="0" style="width:100%" />
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
import { getRoomPage, addRoom, updateRoom, deleteRoom } from '@/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const search = reactive({ keyword: '' })

const form = reactive({ id: null, roomNo: '', buildingNo: '', roomType: '', floor: 1, bedCount: 0, emptyBed: 0 })
const rules = {
  roomNo: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  buildingNo: [{ required: true, message: '请输入楼号', trigger: 'blur' }],
  roomType: [{ required: true, message: '请选择房型', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { pageNum: pagination.pageNum, pageSize: pagination.pageSize }
    if (search.keyword) params.keyword = search.keyword
    const res = await getRoomPage(params)
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
const openEdit = (row) => { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该房间吗？', '提示', { type: 'warning' })
  await deleteRoom(id)
  ElMessage.success('删除成功')
  loadData()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (isEdit.value) { await updateRoom(form); ElMessage.success('修改成功') }
  else { await addRoom(form); ElMessage.success('新增成功') }
  dialogVisible.value = false
  loadData()
}

const resetForm = () => {
  Object.assign(form, { id: null, roomNo: '', buildingNo: '', roomType: '', floor: 1, bedCount: 0, emptyBed: 0 })
  formRef.value?.resetFields()
}

onMounted(loadData)
</script>

<style scoped>
.search-bar { display: flex; gap: 12px; }
.pagination-bar { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
