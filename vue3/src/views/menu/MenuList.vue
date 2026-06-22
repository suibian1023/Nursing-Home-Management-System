<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-button type="success" @click="openAdd">新增菜单</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading" row-key="id" style="margin-top:16px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="menuName" label="菜单名称" width="160" />
        <el-table-column prop="menuType" label="类型" width="80">
          <template #default="{ row }">{{ row.menuType === 0 ? '目录' : row.menuType === 1 ? '菜单' : '按钮' }}</template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" width="180" />
        <el-table-column prop="icon" label="图标" width="80" />
        <el-table-column prop="sortOrder" label="排序" width="60" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑菜单' : '新增菜单'" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="父级ID" prop="parentId"><el-input-number v-model="form.parentId" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="菜单名称" prop="menuName"><el-input v-model="form.menuName" /></el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-select v-model="form.menuType" style="width:100%">
            <el-option label="目录" :value="0" /><el-option label="菜单" :value="1" /><el-option label="按钮" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="路由路径" prop="path"><el-input v-model="form.path" /></el-form-item>
        <el-form-item label="图标" prop="icon"><el-input v-model="form.icon" /></el-form-item>
        <el-form-item label="排序" prop="sortOrder"><el-input-number v-model="form.sortOrder" :min="0" style="width:100%" /></el-form-item>
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
import { getMenuList, addMenu, updateMenu, deleteMenu } from '@/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const form = reactive({ id: null, parentId: 0, menuName: '', menuType: 1, path: '', icon: '', sortOrder: 0 })
const rules = { menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }], path: [{ required: true, message: '请输入路径', trigger: 'blur' }] }

const loadData = async () => {
  loading.value = true
  try { const res = await getMenuList(); tableData.value = res.data || [] } finally { loading.value = false }
}

const openAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }
const openEdit = (row) => { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' })
  await deleteMenu(id); ElMessage.success('删除成功'); loadData()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (isEdit.value) { await updateMenu(form); ElMessage.success('修改成功') }
  else { await addMenu(form); ElMessage.success('新增成功') }
  dialogVisible.value = false; loadData()
}

const resetForm = () => {
  Object.assign(form, { id: null, parentId: 0, menuName: '', menuType: 1, path: '', icon: '', sortOrder: 0 })
  formRef.value?.resetFields()
}

onMounted(loadData)
</script>
<style scoped>.search-bar{display:flex;gap:12px}</style>
