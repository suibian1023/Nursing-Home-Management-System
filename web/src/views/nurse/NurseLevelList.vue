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
        <el-table-column label="关联护理内容" min-width="260">
          <template #default="{ row }">
            <el-tag
              v-for="name in row.contentNames"
              :key="name"
              size="small"
              style="margin-right:6px;margin-bottom:4px"
            >{{ name }}</el-tag>
            <span v-if="!row.contentNames || row.contentNames.length === 0" style="color:#999">未关联</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="warning" @click="openContentDialog(row)">关联内容</el-button>
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

    <el-dialog v-model="contentDialogVisible" title="关联护理内容" width="560px">
      <div style="margin-bottom:12px;color:#666">
        当前护理等级：<strong>{{ currentLevel.levelName }}</strong>
      </div>
      <el-checkbox-group v-model="selectedContentIds">
        <el-checkbox
          v-for="item in allContents"
          :key="item.id"
          :label="item.id"
          style="display:block;margin-bottom:8px"
        >
          {{ item.contentName }}
          <span v-if="item.description" style="color:#999;font-size:12px;margin-left:8px">
            - {{ item.description }}
          </span>
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="contentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveContents">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getNurseLevelList, addNurseLevel, updateNurseLevel, deleteNurseLevel,
  getNurseContentList, getNurseLevelContents, setNurseLevelContents
} from '@/api'

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

// 关联内容对话框
const contentDialogVisible = ref(false)
const currentLevel = reactive({ id: null, levelName: '' })
const allContents = ref([])
const selectedContentIds = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const [levelRes, contentRes] = await Promise.all([
      getNurseLevelList(),
      getNurseContentList()
    ])
    const levels = levelRes.data || []
    const contents = contentRes.data || []
    allContents.value = contents

    const idList = levels.map(l => l.id)
    const assocRes = await Promise.all(idList.map(id => getNurseLevelContents(id)))
    const contentMap = new Map(contents.map(c => [c.id, c.contentName]))

    tableData.value = levels.map((level, idx) => {
      const ids = (assocRes[idx].data) || []
      return {
        ...level,
        contentIds: ids,
        contentNames: ids.map(cid => contentMap.get(cid)).filter(Boolean)
      }
    })

    if (search.keyword) {
      tableData.value = tableData.value.filter(item => item.levelName && item.levelName.includes(search.keyword))
    }
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

const openContentDialog = async (row) => {
  currentLevel.id = row.id
  currentLevel.levelName = row.levelName
  try {
    const res = await getNurseLevelContents(row.id)
    selectedContentIds.value = res.data || []
  } catch {
    selectedContentIds.value = []
  }
  contentDialogVisible.value = true
}

const handleSaveContents = async () => {
  try {
    await setNurseLevelContents(currentLevel.id, selectedContentIds.value)
    ElMessage.success('关联保存成功')
    contentDialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

onMounted(loadData)
</script>

<style scoped>
.search-bar { display: flex; gap: 12px; }

@media screen and (max-width: 768px) {
  .el-dialog .el-form-item {
    margin-bottom: 12px;
  }
  .el-checkbox-group {
    max-height: 200px;
    overflow-y: auto;
  }
}
</style>
