<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-input v-model="search.keyword" placeholder="按姓氏搜索老人" style="width:200px" clearable @clear="loadData" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openAdd">新增退住记录</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top:16px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="customerName" label="老人姓名" width="100" />
        <el-table-column prop="roomNo" label="房间号" width="100" />
        <el-table-column prop="bedNo" label="床位号" width="100" />
        <el-table-column prop="backDateStr" label="退住日期" width="110" />
        <el-table-column prop="reason" label="退住原因" min-width="180" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="search.pageNum" v-model:page-size="search.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @current-change="loadData" @size-change="loadData" style="margin-top:16px;justify-content:flex-end" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑退住记录' : '新增退住记录'" width="550px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="选择老人" prop="customerId">
          <el-select v-model="form.customerId" style="width:100%" filterable placeholder="搜索并选择老人" @change="onCustomerChange">
            <el-option v-for="c in customerList" :key="c.id" :label="c.name" :value="c.id">
              <span>{{ c.name }}</span>
              <span style="color:#5f5f5d;font-size:12px;margin-left:8px">{{ c.roomNo || '未分配房间' }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="selectedCustomer" label="当前信息">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="姓名">{{ selectedCustomer.name }}</el-descriptions-item>
            <el-descriptions-item label="房间">{{ selectedCustomer.roomNo || '-' }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ selectedCustomer.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="床位">{{ selectedCustomerBedNo || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-form-item>
        <el-form-item label="退住日期" prop="backDate">
          <el-date-picker v-model="form.backDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="退住原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBackdownPage, addBackdown, updateBackdown, deleteBackdown, getCustomerList, getBedList } from '@/api'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const search = reactive({ pageNum: 1, pageSize: 10, keyword: '' })
const form = reactive({ id: null, customerId: null, customerName: '', backDate: '', reason: '', remark: '' })
const rules = {
  customerId: [{ required: true, message: '请选择老人', trigger: 'change' }],
  backDate: [{ required: true, message: '请选择退住日期', trigger: 'change' }]
}

const customerList = ref([])
const allBeds = ref([])

const selectedCustomer = computed(() => {
  if (!form.customerId) return null
  return customerList.value.find(c => c.id === form.customerId) || null
})

const selectedCustomerBedNo = computed(() => {
  const c = selectedCustomer.value
  if (!c || !c.bedId) return null
  const bed = allBeds.value.find(b => b.id === c.bedId)
  return bed ? bed.bedNo : null
})

const onCustomerChange = (customerId) => {
  const c = customerList.value.find(item => item.id === customerId)
  if (c) {
    form.customerName = c.name
  }
}

const loadCustomerData = async () => {
  try {
    const [custRes, bedRes] = await Promise.all([getCustomerList(), getBedList()])
    customerList.value = custRes.data || []
    allBeds.value = bedRes.data || []
  } catch (e) {
    console.error('加载客户数据失败:', e)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getBackdownPage(search)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

const openAdd = async () => {
  isEdit.value = false
  resetForm()
  await loadCustomerData()
  dialogVisible.value = true
}

const openEdit = async (row) => {
  isEdit.value = true
  await loadCustomerData()
  Object.assign(form, {
    id: row.id,
    customerId: row.customerId,
    customerName: row.customerName,
    backDate: row.backDateStr || row.backDate,
    reason: row.reason,
    remark: row.remark
  })
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该退住记录吗？', '提示', { type: 'warning' })
  await deleteBackdown(id)
  ElMessage.success('删除成功')
  loadData()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (isEdit.value) {
    await updateBackdown(form)
    ElMessage.success('修改成功')
  } else {
    await addBackdown(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const resetForm = () => {
  Object.assign(form, { id: null, customerId: null, customerName: '', backDate: '', reason: '', remark: '' })
  formRef.value?.resetFields()
}

onMounted(loadData)
</script>

<style scoped>
.search-bar { display: flex; gap: 12px; }
</style>
