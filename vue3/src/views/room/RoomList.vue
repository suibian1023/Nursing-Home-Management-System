<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-button type="success" @click="openAdd">新增房间</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top:16px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="roomNo" label="房间号" width="120" />
        <el-table-column prop="roomType" label="房型" width="100" />
        <el-table-column prop="floor" label="楼层" width="80" />
        <el-table-column prop="capacity" label="容纳人数" width="80" />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑房间' : '新增房间'" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="房间号" prop="roomNo"><el-input v-model="form.roomNo" /></el-form-item>
        <el-form-item label="房型" prop="roomType"><el-input v-model="form.roomType" /></el-form-item>
        <el-form-item label="楼层" prop="floor"><el-input-number v-model="form.floor" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="容纳人数" prop="capacity"><el-input-number v-model="form.capacity" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="价格(元/月)" prop="price"><el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" /></el-form-item>
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
import { getRoomList, addRoom, updateRoom, deleteRoom } from '@/api'

const loading = ref(false); const tableData = ref([]); const dialogVisible = ref(false); const isEdit = ref(false); const formRef = ref()
const form = reactive({ id: null, roomNo: '', roomType: '', floor: 1, capacity: 1, price: null, remark: '' })
const rules = { roomNo: [{ required: true, message: '请输入房间号', trigger: 'blur' }] }

const loadData = async () => { loading.value = true; try { const res = await getRoomList(); tableData.value = res.data || [] } finally { loading.value = false } }
const openAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }
const openEdit = (row) => { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (id) => { await ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' }); await deleteRoom(id); ElMessage.success('删除成功'); loadData() }
const handleSubmit = async () => { const valid = await formRef.value.validate().catch(() => false); if (!valid) return; if (isEdit.value) { await updateRoom(form); ElMessage.success('修改成功') } else { await addRoom(form); ElMessage.success('新增成功') }; dialogVisible.value = false; loadData() }
const resetForm = () => { Object.assign(form, { id: null, roomNo: '', roomType: '', floor: 1, capacity: 1, price: null, remark: '' }); formRef.value?.resetFields() }
onMounted(loadData)
</script>
<style scoped>.search-bar{display:flex;gap:12px}</style>
