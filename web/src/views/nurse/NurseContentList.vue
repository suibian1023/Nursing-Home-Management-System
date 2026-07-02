<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-input v-model="search.keyword" placeholder="搜索护理项目" style="width:200px" clearable @clear="loadData" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openAdd">新增护理内容</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top:16px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="contentName" label="护理项目" width="200" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="所属护理等级" min-width="220">
          <template #default="{ row }">
            <el-tag
              v-for="name in row.levelNames"
              :key="name"
              size="small"
              type="success"
              style="margin-right:6px;margin-bottom:4px"
            >{{ name }}</el-tag>
            <span v-if="!row.levelNames || row.levelNames.length === 0" style="color:#999">未关联</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="warning" @click="openLevelDialog(row)">关联等级</el-button>
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑护理内容' : '新增护理内容'" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="护理项目" prop="contentName">
          <el-input v-model="form.contentName" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="所属等级">
          <el-select v-model="form.levelIds" multiple placeholder="选择护理等级（可多选）" style="width:100%">
            <el-option
              v-for="lv in allLevels"
              :key="lv.id"
              :label="lv.levelName"
              :value="lv.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="levelDialogVisible" title="关联护理等级" width="560px">
      <div style="margin-bottom:12px;color:#666">
        当前护理内容：<strong>{{ currentContent.contentName }}</strong>
      </div>
      <el-checkbox-group v-model="selectedLevelIds">
        <el-checkbox
          v-for="lv in allLevels"
          :key="lv.id"
          :label="lv.id"
          style="display:block;margin-bottom:8px"
        >
          {{ lv.levelName }}
          <span v-if="lv.description" style="color:#999;font-size:12px;margin-left:8px">
            - {{ lv.description }}
          </span>
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="levelDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveLevels">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getNurseContentList, addNurseContent, updateNurseContent, deleteNurseContent,
  getNurseLevelList, getNurseContentLevels, setNurseContentLevels
} from '@/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const search = reactive({ keyword: '' })

const form = reactive({ id: null, contentName: '', description: '', levelIds: [] })
const rules = {
  contentName: [{ required: true, message: '请输入护理项目', trigger: 'blur' }]
}

// 关联等级对话框
const levelDialogVisible = ref(false)
const currentContent = reactive({ id: null, contentName: '' })
const allLevels = ref([])
const selectedLevelIds = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const [contentRes, levelRes] = await Promise.all([
      getNurseContentList(),
      getNurseLevelList()
    ])
    const contents = contentRes.data || []
    const levels = levelRes.data || []
    allLevels.value = levels

    const idList = contents.map(c => c.id)
    const assocRes = await Promise.all(idList.map(id => getNurseContentLevels(id)))
    const levelMap = new Map(levels.map(l => [l.id, l.levelName]))

    tableData.value = contents.map((content, idx) => {
      const ids = (assocRes[idx].data) || []
      return {
        ...content,
        levelIds: ids,
        levelNames: ids.map(lid => levelMap.get(lid)).filter(Boolean)
      }
    })

    if (search.keyword) {
      tableData.value = tableData.value.filter(item => item.contentName && item.contentName.includes(search.keyword))
    }
  } finally { loading.value = false }
}

const openAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }
const openEdit = async (row) => {
  isEdit.value = true
  Object.assign(form, { id: row.id, contentName: row.contentName, description: row.description })
  try {
    const res = await getNurseContentLevels(row.id)
    form.levelIds = res.data || []
  } catch {
    form.levelIds = []
  }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该护理内容吗？', '提示', { type: 'warning' })
  await deleteNurseContent(id)
  ElMessage.success('删除成功')
  loadData()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  const levelIds = [...(form.levelIds || [])]
  if (isEdit.value) {
    await updateNurseContent({ id: form.id, contentName: form.contentName, description: form.description })
    await setNurseContentLevels(form.id, levelIds)
    ElMessage.success('修改成功')
  } else {
    // 新增：先保存内容，再查最新ID来关联等级
    const addRes = await addNurseContent({ contentName: form.contentName, description: form.description })
    let newId = null
    if (addRes && addRes.data && typeof addRes.data === 'object') {
      newId = addRes.data.id
    }
    if (!newId && levelIds.length > 0) {
      // 回退方案：重新拉取列表，取ID最大的（最新插入）
      const listRes = await getNurseContentList()
      const list = listRes.data || []
      if (list.length > 0) {
        newId = Math.max(...list.map(i => i.id))
      }
    }
    if (newId && levelIds.length > 0) {
      await setNurseContentLevels(newId, levelIds)
    }
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const resetForm = () => {
  Object.assign(form, { id: null, contentName: '', description: '', levelIds: [] })
  formRef.value?.resetFields()
}

const openLevelDialog = async (row) => {
  currentContent.id = row.id
  currentContent.contentName = row.contentName
  try {
    const res = await getNurseContentLevels(row.id)
    selectedLevelIds.value = res.data || []
  } catch {
    selectedLevelIds.value = []
  }
  levelDialogVisible.value = true
}

const handleSaveLevels = async () => {
  try {
    await setNurseContentLevels(currentContent.id, selectedLevelIds.value)
    ElMessage.success('关联保存成功')
    levelDialogVisible.value = false
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
