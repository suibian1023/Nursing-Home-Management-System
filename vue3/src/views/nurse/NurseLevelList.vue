<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-input v-model="search.keyword" placeholder="搜索等级名称" style="width:200px" clearable @clear="loadData" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openAdd">新增护理等级</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top:16px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="levelName" label="等级名称" width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="price" label="费用(元/月)" width="120" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑护理等级' : '新增护理等级'" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="等级名称" prop="levelName">
          <el-input v-model="form.levelName" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="费用(元/月)" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" />
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
import { getNurseLevelList, addNurseLevel, updateNurseLevel, deleteNurseLevel } from '@/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const search = reactive({ keyword: '' })

const form = reactive({ id: null, levelName: '', description: '', price: null })
const rules = {
  levelName: [{ required: true, message: '请输入等级名称', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getNurseLevelList()
    let data = res.data || []
    if (search.keyword) {
      data = data.filter(item => item.levelName && item.levelName.includes(search.keyword))
    }
    tableData.value = data
  } finally { loading.value = false }
}

const openAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }
const openEdit = (row) => { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该护理等级吗？', '提示', { type: 'warning' })
  await deleteNurseLevel(id)
  ElMessage.success('删除成功')
  loadData()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (isEdit.value) { await updateNurseLevel(form); ElMessage.success('修改成功') }
  else { await addNurseLevel(form); ElMessage.success('新增成功') }
  dialogVisible.value = false
  loadData()
}

const resetForm = () => {
  Object.assign(form, { id: null, levelName: '', description: '', price: null })
  formRef.value?.resetFields()
}

onMounted(loadData)
</script>

<style scoped>
.search-bar { display: flex; gap: 12px; }
</style>
