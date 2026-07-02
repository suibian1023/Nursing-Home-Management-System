<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-input v-model="search.keyword" placeholder="搜索用户名/姓名" style="width:200px" clearable @clear="loadData" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openAdd">新增用户</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top:16px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="姓名" width="120" />
        <el-table-column prop="roleName" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.roleId === 1 ? 'danger' : 'success'">{{ row.roleName || '未知' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phoneNumber" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" :prop="isEdit ? '' : 'password'">
          <el-input v-model="form.password" type="password" :placeholder="isEdit ? '留空不修改' : '请输入密码'" />
        </el-form-item>
        <el-form-item label="姓名" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="form.roleId" style="width:100%">
            <el-option label="超级管理员" :value="1" />
            <el-option label="健康管家" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phoneNumber">
          <el-input v-model="form.phoneNumber" maxlength="11" @input="form.phoneNumber = form.phoneNumber.replace(/\D/g, '')" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
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
import { getUserPage, addUser, updateUser, deleteUser, getUserById } from '@/api'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const search = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const form = reactive({
  id: null, username: '', password: '', nickname: '', roleId: 2, phoneNumber: '', email: ''
})

const validatePhoneNumber = (rule, value, callback) => {
  if (value && !/^\d{11}$/.test(value)) {
    ElMessage.warning('手机号必须为11位数字')
    callback(new Error('手机号必须为11位数字'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }],
  phoneNumber: [{ validator: validatePhoneNumber, trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserPage(search)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const openAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const openEdit = async (row) => {
  isEdit.value = true
  const res = await getUserById(row.id)
  Object.assign(form, res.data)
  form.password = ''
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该用户吗？', '提示', { type: 'warning' })
  await deleteUser(id)
  ElMessage.success('删除成功')
  loadData()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  const data = { ...form }
  if (isEdit.value && !data.password) delete data.password
  if (isEdit.value) {
    await updateUser(data)
    ElMessage.success('修改成功')
  } else {
    await addUser(data)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const resetForm = () => {
  Object.assign(form, { id: null, username: '', password: '', nickname: '', roleId: 2, phoneNumber: '', email: '' })
  formRef.value?.resetFields()
}

onMounted(loadData)
</script>

<style scoped>
.search-bar { display: flex; gap: 12px; }

@media screen and (max-width: 768px) {
  .el-dialog .el-form-item {
    margin-bottom: 12px;
  }
}
</style>
