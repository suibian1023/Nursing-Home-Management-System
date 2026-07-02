<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-input v-model="search.keyword" placeholder="搜索客户姓名" style="width:200px" clearable @clear="loadData" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openAdd">新增记录</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top:16px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="customerName" label="客户" width="100" />
        <el-table-column prop="outDate" label="外出日期" width="110" />
        <el-table-column prop="expectBackDate" label="预计返回" width="110" />
        <el-table-column prop="backDate" label="返回日期" width="110" />
        <el-table-column prop="reason" label="外出原因" min-width="160" show-overflow-tooltip />
        <el-table-column prop="accompanyName" label="陪同人" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">外出中</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">已返回</el-tag>
            <el-tag v-else-if="row.status === 2" type="warning">待审批</el-tag>
            <el-tag v-else-if="row.status === 3" type="danger">已驳回</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="search.pageNum" v-model:page-size="search.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @current-change="loadData" @size-change="loadData" style="margin-top:16px;justify-content:flex-end" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑记录' : '新增记录'" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="客户姓名" prop="customerId">
          <el-select
            v-model="form.customerId"
            filterable
            remote
            reserve-keyword
            placeholder="请输入姓名搜索"
            :remote-method="searchCustomers"
            :loading="customerLoading"
            style="width:100%"
            @change="handleCustomerChange"
          >
            <el-option
              v-for="item in customerOptions"
              :key="item.id"
              :label="item.name + ' - ' + (item.roomNo || '未分配房间')"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="外出日期" prop="outDate"><el-date-picker v-model="form.outDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="预计返回" prop="expectBackDate"><el-date-picker v-model="form.expectBackDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="外出原因" prop="reason"><el-input v-model="form.reason" type="textarea" /></el-form-item>
        <el-form-item label="陪同人" prop="accompanyName"><el-input v-model="form.accompanyName" /></el-form-item>
        <div v-if="!isAdmin" style="color:#e6a23c;font-size:13px;padding:0 0 12px 80px">
          提交后需要超级管理员审批才能生效
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOutwardPage, addOutward, updateOutward, deleteOutward, getCustomerPage } from '@/api'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const search = reactive({ pageNum: 1, pageSize: 10, keyword: '' })
const formDefault = { id: null, customerId: null, customerName: '', outDate: '', expectBackDate: '', reason: '', accompanyName: '' }
const form = reactive({ ...formDefault })
const validateExpectBackDate = (_rule, value, callback) => {
  if (value && form.outDate && value < form.outDate) {
    callback(new Error('预计返回日期不能早于外出日期'))
  } else {
    callback()
  }
}
const validateOutDate = (_rule, value, callback) => {
  if (value && form.expectBackDate && value > form.expectBackDate) {
    callback(new Error('外出日期不能晚于预计返回日期'))
  } else {
    callback()
  }
}
const rules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  outDate: [{ validator: validateOutDate, trigger: 'change' }],
  expectBackDate: [{ validator: validateExpectBackDate, trigger: 'change' }]
}
const customerOptions = ref([])
const customerLoading = ref(false)

const isAdmin = computed(() => userStore.userInfo?.roleId === 1)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOutwardPage(search)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

const searchCustomers = async (keyword) => {
  if (!keyword) { customerOptions.value = []; return }
  customerLoading.value = true
  try {
    const res = await getCustomerPage({ pageNum: 1, pageSize: 50, keyword })
    customerOptions.value = res.data?.records || []
  } finally { customerLoading.value = false }
}

const handleCustomerChange = (customerId) => {
  const customer = customerOptions.value.find(c => c.id === customerId)
  if (customer) {
    form.customerName = customer.name
  }
}

const openAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const openEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  if (row.customerId) {
    customerOptions.value = [{ id: row.customerId, name: row.customerName, roomNo: '' }]
  }
  dialogVisible.value = true
}

const resetForm = () => {
  Object.assign(form, formDefault)
  customerOptions.value = []
  formRef.value?.resetFields()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' })
  await deleteOutward(id)
  ElMessage.success('删除成功')
  loadData()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (form.outDate && form.expectBackDate && form.expectBackDate < form.outDate) {
    ElMessage.warning('预计返回日期不能早于外出日期')
    return
  }
  if (isEdit.value) {
    await updateOutward(form)
    ElMessage.success('修改成功')
  } else {
    await addOutward({ ...form, status: undefined })
    ElMessage.success(isAdmin.value ? '新增成功' : '已提交，等待审批')
  }
  dialogVisible.value = false
  loadData()
}
onMounted(loadData)
</script>
<style scoped>
.search-bar { display: flex; gap: 12px; align-items: center }

@media screen and (max-width: 768px) {
  .el-dialog .el-form-item {
    margin-bottom: 12px;
  }
}
</style>
