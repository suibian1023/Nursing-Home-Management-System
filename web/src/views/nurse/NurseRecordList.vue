<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-input v-model="search.customerName" placeholder="搜索客户姓名" style="width:200px" clearable @clear="loadData" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openAdd">新增记录</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top:16px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="customerName" label="客户" width="100" />
        <el-table-column prop="contentName" label="护理项目" width="160" />
        <el-table-column label="护理日期" width="110">
          <template #default="{ row }">{{ formatDate(row.recordDate) }}</template>
        </el-table-column>
        <el-table-column prop="nurseTime" label="护理时段" width="100" />
        <el-table-column prop="staffName" label="护理人员" width="100" />
        <el-table-column prop="description" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="search.current" v-model:page-size="search.size" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @current-change="loadData" @size-change="loadData" style="margin-top:16px;justify-content:flex-end" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑记录' : '新增记录'" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="客户" prop="customerId">
          <el-select v-model="form.customerId" filterable placeholder="输入姓名搜索老人" style="width:100%">
            <el-option v-for="c in customerList" :key="c.id" :label="c.name" :value="c.id">
              <span>{{ c.name }}</span>
              <span style="color:#909399;font-size:12px;margin-left:8px">{{ c.age }}岁 · {{ c.roomNo || '未分配' }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="护理项目" prop="nurseContentId">
          <el-select v-model="form.nurseContentId" filterable placeholder="请选择护理项目" style="width:100%">
            <el-option v-for="n in nurseContentList" :key="n.id" :label="n.contentName" :value="n.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="护理日期" prop="recordDate">
          <el-date-picker v-model="form.recordDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="护理时段" prop="nurseTime">
          <el-select v-model="form.nurseTime" filterable allow-create placeholder="请选择或输入时段" style="width:100%">
            <el-option label="上午" value="上午" />
            <el-option label="下午" value="下午" />
            <el-option label="晚间" value="晚间" />
            <el-option label="全天" value="全天" />
          </el-select>
        </el-form-item>
        <el-form-item label="护理人员" prop="staffName">
          <el-input v-model="form.staffName" placeholder="请输入护理人员姓名" />
        </el-form-item>
        <el-form-item label="备注" prop="description">
          <el-input v-model="form.description" type="textarea" />
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
import { getNurseRecordPage, addNurseRecord, updateNurseRecord, deleteNurseRecord, getCustomerList, getNurseContentList } from '@/api'

const loading = ref(false); const tableData = ref([]); const total = ref(0); const dialogVisible = ref(false); const isEdit = ref(false); const formRef = ref()
const customerList = ref([]); const nurseContentList = ref([])

const search = reactive({ current: 1, size: 10, customerName: '' })
const form = reactive({ id: null, customerId: null, nurseContentId: null, recordDate: '', nurseTime: '', staffName: '', description: '' })
const rules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  nurseContentId: [{ required: true, message: '请选择护理项目', trigger: 'change' }]
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const loadData = async () => { loading.value = true; try { const res = await getNurseRecordPage(search); tableData.value = res.data?.records || []; total.value = res.data?.total || 0 } finally { loading.value = false } }
const loadCustomerList = async () => { try { const res = await getCustomerList(); customerList.value = res.data || [] } catch (e) { console.error(e) } }
const loadNurseContentList = async () => { try { const res = await getNurseContentList(); nurseContentList.value = res.data || [] } catch (e) { console.error(e) } }

const openAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }
const openEdit = (row) => { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }
const handleDelete = async (id) => { await ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' }); await deleteNurseRecord(id); ElMessage.success('删除成功'); loadData() }
const handleSubmit = async () => { const valid = await formRef.value.validate().catch(() => false); if (!valid) return; if (isEdit.value) { await updateNurseRecord(form); ElMessage.success('修改成功') } else { await addNurseRecord(form); ElMessage.success('新增成功') }; dialogVisible.value = false; loadData() }
const resetForm = () => { Object.assign(form, { id: null, customerId: null, nurseContentId: null, recordDate: '', nurseTime: '', staffName: '', description: '' }); formRef.value?.resetFields() }

onMounted(() => { loadData(); loadCustomerList(); loadNurseContentList() })
</script>
<style scoped>
.search-bar { display: flex; gap: 12px; }

@media screen and (max-width: 768px) {
  .el-dialog .el-form-item {
    margin-bottom: 12px;
  }
}
</style>
