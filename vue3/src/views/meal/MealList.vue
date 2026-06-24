<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-input v-model="search.keyword" placeholder="搜索套餐名称" style="width:200px" clearable @clear="loadData" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openAdd">新增套餐</el-button>
      </div>

      <div class="meal-grid" v-loading="loading">
        <div class="meal-card" v-for="item in tableData" :key="item.id">
          <div class="meal-header">
            <div class="meal-name">{{ item.mealName }}</div>
            <el-tag size="small" :type="mealTypeColor(item.mealType)">{{ item.mealType }}</el-tag>
          </div>
          <div class="meal-price">&yen;{{ item.price }}</div>
          <div class="meal-foods">
            <el-tooltip v-for="f in getMealFoods(item)" :key="f.id" :content="f.foodName" placement="top">
              <el-image :src="f.imageUrl" fit="cover" class="meal-food-thumb" />
            </el-tooltip>
            <span v-if="getMealFoods(item).length === 0" class="no-food">未添加菜品</span>
          </div>
          <div class="meal-food-names">
            <span v-for="(f, i) in getMealFoods(item)" :key="f.id">
              {{ f.foodName }}<span v-if="i < getMealFoods(item).length - 1">、</span>
            </span>
          </div>
          <div class="meal-actions">
            <el-button size="small" @click="openEdit(item)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(item.id)">删除</el-button>
          </div>
        </div>
      </div>

      <div v-if="tableData.length === 0 && !loading" class="empty-tip">
        <el-empty description="暂无套餐" />
      </div>

      <el-pagination
        v-model:current-page="search.pageNum"
        v-model:page-size="search.pageSize"
        :total="total"
        :page-sizes="[10,20,50]"
        layout="total,sizes,prev,pager,next"
        @current-change="loadData" @size-change="loadData"
        style="margin-top:16px;justify-content:flex-end"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑套餐' : '新增套餐'" width="700px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="套餐名称" prop="mealName"><el-input v-model="form.mealName" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="类型" prop="mealType">
              <el-select v-model="form.mealType" style="width:100%">
                <el-option label="早餐" value="早餐" />
                <el-option label="午餐" value="午餐" />
                <el-option label="晚餐" value="晚餐" />
                <el-option label="加餐" value="加餐" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="价格(元)" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div class="food-select-section">
        <div class="section-title">选择菜品</div>
        <div class="food-type-tabs">
          <el-radio-group v-model="foodFilter" size="small">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button v-for="t in foodTypes" :key="t" :label="t">{{ t }}</el-radio-button>
          </el-radio-group>
        </div>
        <div class="food-select-grid">
          <div
            v-for="f in filteredFoods"
            :key="f.id"
            class="food-select-item"
            :class="{ selected: selectedFoodIds.includes(f.id) }"
            @click="toggleFood(f.id)"
          >
            <el-image :src="f.imageUrl" fit="cover" class="food-select-img" />
            <div class="food-select-name">{{ f.foodName }}</div>
            <div class="food-select-check" v-if="selectedFoodIds.includes(f.id)">&#10003;</div>
          </div>
        </div>
      </div>

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
import { getMealPage, addMeal, updateMeal, deleteMeal, getFoodList } from '@/api'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const search = reactive({ pageNum: 1, pageSize: 10, keyword: '' })
const allFoods = ref([])
const foodFilter = ref('')
const selectedFoodIds = ref([])
const foodTypes = ['主食', '荤菜', '素菜', '汤类', '水果', '点心']

const form = reactive({ id: null, mealName: '', mealType: '早餐', price: null })
const rules = {
  mealName: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
  mealType: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

const filteredFoods = computed(() => {
  if (!foodFilter.value) return allFoods.value
  return allFoods.value.filter(f => f.foodType === foodFilter.value)
})

const getMealFoods = (item) => {
  if (!item.foodIds) return []
  const ids = item.foodIds.split(',').map(Number)
  return allFoods.value.filter(f => ids.includes(f.id))
}

const toggleFood = (id) => {
  const idx = selectedFoodIds.value.indexOf(id)
  if (idx >= 0) selectedFoodIds.value.splice(idx, 1)
  else selectedFoodIds.value.push(id)
}

const mealTypeColor = (type) => {
  const map = { '早餐': '', '午餐': 'success', '晚餐': 'info', '加餐': 'warning' }
  return map[type] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const [mealRes, foodRes] = await Promise.all([getMealPage(search), getFoodList()])
    tableData.value = mealRes.data?.records || []
    total.value = mealRes.data?.total || 0
    allFoods.value = foodRes.data || []
  } finally { loading.value = false }
}

const openAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const openEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { id: row.id, mealName: row.mealName, mealType: row.mealType, price: row.price })
  selectedFoodIds.value = row.foodIds ? row.foodIds.split(',').map(Number) : []
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该套餐吗？', '提示', { type: 'warning' })
  await deleteMeal(id)
  ElMessage.success('删除成功')
  loadData()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  const data = { ...form, foodIds: selectedFoodIds.value.join(',') }
  if (isEdit.value) { await updateMeal(data); ElMessage.success('修改成功') }
  else { await addMeal(data); ElMessage.success('新增成功') }
  dialogVisible.value = false
  loadData()
}

const resetForm = () => {
  Object.assign(form, { id: null, mealName: '', mealType: '早餐', price: null })
  selectedFoodIds.value = []
  foodFilter.value = ''
  formRef.value?.resetFields()
}

onMounted(loadData)
</script>

<style scoped>
.search-bar { display: flex; gap: 12px; margin-bottom: 16px; }

.meal-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.meal-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 14px;
  background: #fff;
  transition: box-shadow 0.3s;
}
.meal-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.08); }

.meal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.meal-name { font-size: 16px; font-weight: 600; color: #303133; }
.meal-price { color: #e6a23c; font-weight: 600; font-size: 18px; margin-bottom: 10px; }

.meal-foods {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 8px;
  min-height: 48px;
}
.meal-food-thumb { width: 44px; height: 44px; border-radius: 6px; border: 1px solid #ebeef5; cursor: pointer; }
.no-food { font-size: 12px; color: #c0c4cc; line-height: 44px; }

.meal-food-names { font-size: 12px; color: #909399; margin-bottom: 10px; line-height: 1.5; }

.meal-actions { display: flex; gap: 8px; }

/* Dialog food selection */
.food-select-section { margin-top: 16px; border-top: 1px solid #ebeef5; padding-top: 12px; }
.section-title { font-size: 14px; font-weight: 600; color: #303133; margin-bottom: 10px; }
.food-type-tabs { margin-bottom: 12px; }

.food-select-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(90px, 1fr));
  gap: 8px;
  max-height: 300px;
  overflow-y: auto;
}

.food-select-item {
  position: relative;
  border: 2px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: border-color 0.2s;
  text-align: center;
}
.food-select-item:hover { border-color: #409eff; }
.food-select-item.selected { border-color: #67c23a; }

.food-select-img { width: 100%; height: 64px; display: block; }
.food-select-name { font-size: 11px; color: #606266; padding: 4px 2px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.food-select-check {
  position: absolute;
  top: 2px;
  right: 4px;
  background: #67c23a;
  color: #fff;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  font-size: 12px;
  line-height: 18px;
  text-align: center;
}

.empty-tip { padding: 40px 0; }
</style>
