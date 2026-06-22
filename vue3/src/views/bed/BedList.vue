<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-button type="success" @click="openAdd">新增床位</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top:16px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="bedNo" label="床位号" width="120" />
        <el-table-column prop="roomNo" label="房间号" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : row.status === 1 ? 'warning' : 'info'">
              {{ row.status === 0 ? '空闲' : row.status === 1 ? '已占用' : '维护中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格(元/月)" width="120" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑床位' : '新增床位'" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="床位号" prop="bedNo"><el-input v-model="form.bedNo" /></el-form-item>
        <el-form-item label="房间号" prop="roomNo"><el-input v-model="form.roomNo" /></el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="空闲" :value="0" />
            <el-option label="已占用" :value="1" />
            <el-option label="维护中" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格(元/月)" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea" /></el-form-item>
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
import { getBedList, addBed, updateBed, deleteBed, getBedById } from '@/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const form = reactive({ id: null, bedNo: '', roomNo: '', status: 0, price: null, remark: '' })
const rules = {
  bedNo: [{ required: true, message: '请输入床位号', trigger: 'blur' }],
  roomNo: [{ required: true, message: '请输入房间号', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getBedList()
    tableData.value = res.data || []
  } finally { loading.value = false }
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
  Object.assign(form, { id: null, bedNo: '', roomNo: '', status: 0, price: null, remark: '' })
  formRef.value?.resetFields()
}

onMounted(loadData)
</script>

<style scoped>
.search-bar { display: flex; gap: 12px; }
</style>
