<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-select v-model="filter.type" style="width:120px" clearable placeholder="分类筛选" @change="applyFilter">
          <el-option v-for="t in foodTypes" :key="t" :label="t" :value="t" />
        </el-select>
        <el-input v-model="filter.keyword" placeholder="搜索菜品名称" style="width:200px" clearable @clear="applyFilter" @keyup.enter="applyFilter" />
        <el-button type="primary" @click="applyFilter">查询</el-button>
        <el-button type="success" @click="openAdd">新增菜品</el-button>
      </div>

      <div class="food-grid" v-loading="loading">
        <div class="food-card" v-for="item in filteredData" :key="item.id">
          <div class="food-img-wrap">
            <el-image :src="item.imageUrl" fit="cover" class="food-img" :preview-src-list="[item.imageUrl]" />
          </div>
          <div class="food-info">
            <div class="food-name">{{ item.foodName }}</div>
            <div class="food-meta">
              <el-tag size="small" :type="typeTagColor(item.foodType)">{{ item.foodType }}</el-tag>
              <span class="food-price">&yen;{{ item.price }}</span>
            </div>
            <div class="food-desc">{{ item.description || '暂无描述' }}</div>
            <div class="food-actions">
              <el-button size="small" @click="openEdit(item)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(item.id)">删除</el-button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="filteredData.length === 0 && !loading" class="empty-tip">
        <el-empty description="暂无菜品数据" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑菜品' : '新增菜品'" width="550px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="菜品名称" prop="foodName"><el-input v-model="form.foodName" /></el-form-item>
        <el-form-item label="分类" prop="foodType">
          <el-select v-model="form.foodType" style="width:100%" allow-create filterable>
            <el-option v-for="t in foodTypes" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格(元)" prop="price"><el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="图片地址" prop="imageUrl"><el-input v-model="form.imageUrl" placeholder="/images/food/分类/名称.jpg" /></el-form-item>
        <el-form-item label="描述" prop="description"><el-input v-model="form.description" type="textarea" /></el-form-item>
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
import { getFoodList, addFood, updateFood, deleteFood } from '@/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const filter = reactive({ type: '', keyword: '' })
const foodTypes = ['主食', '荤菜', '素菜', '汤类', '水果', '点心']

const typeTagColor = (type) => {
  const map = { '主食': '', '荤菜': 'danger', '素菜': 'success', '汤类': 'info', '水果': 'warning', '点心': '' }
  return map[type] || ''
}

const filteredData = computed(() => {
  let data = tableData.value
  if (filter.type) data = data.filter(d => d.foodType === filter.type)
  if (filter.keyword) data = data.filter(d => d.foodName && d.foodName.includes(filter.keyword))
  return data
})

const form = reactive({ id: null, foodName: '', foodType: '', price: null, imageUrl: '', description: '' })
const rules = { foodName: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }] }

const applyFilter = () => {}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getFoodList()
    tableData.value = res.data || []
  } finally { loading.value = false }
}

const openAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }
const openEdit = (row) => { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' })
  await deleteFood(id)
  ElMessage.success('删除成功')
  loadData()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (isEdit.value) { await updateFood(form); ElMessage.success('修改成功') }
  else { await addFood(form); ElMessage.success('新增成功') }
  dialogVisible.value = false
  loadData()
}

const resetForm = () => {
  Object.assign(form, { id: null, foodName: '', foodType: '', price: null, imageUrl: '', description: '' })
  formRef.value?.resetFields()
}

onMounted(loadData)
</script>

<style scoped>
.search-bar { display: flex; gap: 12px; margin-bottom: 16px; }

.food-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.food-card {
  border: 1px solid #eceae4;
  border-radius: 12px;
  overflow: hidden;
  transition: box-shadow 0.3s;
  background: #fcfbf8;
}
.food-card:hover { box-shadow: rgba(0,0,0,0.06) 0px 2px 8px; }

.food-img-wrap {
  width: 100%;
  height: 150px;
  overflow: hidden;
  background: #faf8f3;
}
.food-img { width: 100%; height: 100%; display: block; }

.food-info { padding: 10px 12px; }
.food-name { font-size: 15px; font-weight: 600; color: #1c1c1c; margin-bottom: 6px; letter-spacing: -0.3px; }
.food-meta { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.food-price { color: #5f5f5d; font-weight: 500; font-size: 14px; }
.food-desc { font-size: 12px; color: #5f5f5d; margin-bottom: 8px; min-height: 18px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.food-actions { display: flex; gap: 8px; }

.empty-tip { padding: 40px 0; }
</style>
