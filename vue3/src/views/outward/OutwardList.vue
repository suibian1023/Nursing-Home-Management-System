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
        <el-table-column prop="backDate" label="预计返回" width="110" />
        <el-table-column prop="reason" label="外出原因" min-width="160" show-overflow-tooltip />
        <el-table-column prop="accompanyName" label="陪同人" width="100" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'warning' : 'success'">{{ row.status === 0 ? '外出中' : '已返回' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="search.pageNum" v-model:page-size="search.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @change="loadData" style="margin-top:16px;justify-content:flex-end" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑记录' : '新增记录'" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="客户ID" prop="customerId"><el-input-number v-model="form.customerId" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="外出日期" prop="outDate"><el-date-picker v-model="form.outDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="预计返回" prop="backDate"><el-date-picker v-model="form.backDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="外出原因" prop="reason"><el-input v-model="form.reason" type="textarea" /></el-form-item>
        <el-form-item label="陪同人" prop="accompanyName"><el-input v-model="form.accompanyName" /></el-form-item>
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
import { getOutwardPage, addOutward, updateOutward, deleteOutward } from '@/api'

const loading = ref(false); const tableData = ref([]); const total = ref(0); const dialogVisible = ref(false); const isEdit = ref(false); const formRef = ref()
const search = reactive({ pageNum: 1, pageSize: 10, keyword: '' })
const form = reactive({ id: null, customerId: null, outDate: '', backDate: '', reason: '', accompanyName: '' })
const rules = { customerId: [{ required: true, message: '请输入客户ID', trigger: 'blur' }] }

const loadData = async () => { loading.value = true; try { const res = await getOutwardPage(search); tableData.value = res.data?.records || []; total.value = res.data?.total || 0 } finally { loading.value = false } }
const openAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }
const openEdit = (row) => { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (id) => { await ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' }); await deleteOutward(id); ElMessage.success('删除成功'); loadData() }
const handleSubmit = async () => { const valid = await formRef.value.validate().catch(() => false); if (!valid) return; if (isEdit.value) { await updateOutward(form); ElMessage.success('修改成功') } else { await addOutward(form); ElMessage.success('新增成功') }; dialogVisible.value = false; loadData() }
const resetForm = () => { Object.assign(form, { id: null, customerId: null, outDate: '', backDate: '', reason: '', accompanyName: '' }); formRef.value?.resetFields() }
onMounted(loadData)
</script>
<style scoped>.search-bar{display:flex;gap:12px}</style>
