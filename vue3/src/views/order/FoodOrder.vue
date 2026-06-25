<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span style="font-size:18px;font-weight:bold">点餐服务</span>
          <el-button type="primary" @click="showCart" :badge="cartCount">
            查看订单 <el-badge :value="cartCount" :hidden="cartCount===0" style="margin-left:6px" />
          </el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="单点菜品" name="food">
          <div class="type-filter">
            <el-radio-group v-model="foodType" size="small">
              <el-radio-button label="">全部</el-radio-button>
              <el-radio-button v-for="t in foodTypes" :key="t" :label="t">{{ t }}</el-radio-button>
            </el-radio-group>
          </div>
          <div class="food-grid">
            <div v-for="food in filteredFoods" :key="food.id" class="food-card" :class="{ selected: isInCart(food.id) }" @click="toggleCart(food)">
              <div class="food-img" v-if="food.imageUrl">
                <img :src="food.imageUrl" :alt="food.foodName" />
              </div>
              <div class="food-img placeholder" v-else>
                <el-icon :size="40" color="#ccc"><Food /></el-icon>
              </div>
              <div class="food-info">
                <div class="food-name">{{ food.foodName }}</div>
                <div class="food-meta">
                  <el-tag size="small" type="info">{{ food.foodType }}</el-tag>
                  <span class="price">¥{{ food.price }}</span>
                </div>
              </div>
              <div class="check-mark" v-if="isInCart(food.id)">
                <el-icon color="#fff"><Check /></el-icon>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="套餐" name="meal">
          <div class="food-grid">
            <div v-for="meal in meals" :key="meal.id" class="food-card" :class="{ selected: isInCartMeal(meal.id) }" @click="toggleCartMeal(meal)">
              <div class="food-info" style="padding:20px">
                <div class="food-name">{{ meal.mealName }}</div>
                <div class="food-meta">
                  <el-tag size="small">{{ meal.mealType }}</el-tag>
                  <span class="price">¥{{ meal.price }}</span>
                </div>
                <div class="food-desc" style="color:#909399;font-size:12px;margin-top:8px">{{ getFoodNames(meal.foodIds) }}</div>
              </div>
              <div class="check-mark" v-if="isInCartMeal(meal.id)">
                <el-icon color="#fff"><Check /></el-icon>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="cartVisible" title="确认订单" width="550px">
      <div v-if="cartItems.length === 0 && cartMeals.length === 0" style="text-align:center;color:#909399;padding:20px">
        还没有选择任何菜品或套餐
      </div>
      <div v-else>
        <h4 v-if="cartItems.length > 0">单点菜品</h4>
        <el-table v-if="cartItems.length > 0" :data="cartItems" border size="small" style="margin-bottom:16px">
          <el-table-column prop="foodName" label="菜品" />
          <el-table-column prop="foodType" label="类型" width="80" />
          <el-table-column prop="price" label="价格" width="80">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column label="操作" width="60">
            <template #default="{ row }">
              <el-button size="small" type="danger" text @click="removeFromCart(row.id)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <h4 v-if="cartMeals.length > 0">套餐</h4>
        <el-table v-if="cartMeals.length > 0" :data="cartMeals" border size="small" style="margin-bottom:16px">
          <el-table-column prop="mealName" label="套餐" />
          <el-table-column prop="mealType" label="类型" width="80" />
          <el-table-column prop="price" label="价格" width="80">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column label="操作" width="60">
            <template #default="{ row }">
              <el-button size="small" type="danger" text @click="removeFromCartMeal(row.id)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div style="text-align:right;font-size:18px;font-weight:bold;color:#1c1c1c">
          合计：¥{{ totalPrice }}
        </div>
        <el-form :model="orderForm" label-width="80px" style="margin-top:20px">
          <el-form-item label="房间号">
            <el-input v-model="orderForm.roomNo" placeholder="请输入您的房间号" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="orderForm.remark" type="textarea" placeholder="如有特殊要求请在此备注" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="cartVisible = false">取消</el-button>
        <el-button type="primary" @click="submitOrder" :disabled="cartItems.length === 0 && cartMeals.length === 0">提交订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Food, Check } from '@element-plus/icons-vue'
import { getFoodList, getMealPage, addFoodOrder } from '@/api'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const activeTab = ref('food')
const foodType = ref('')
const allFoods = ref([])
const allMeals = ref([])
const foodTypes = ['主食', '荤菜', '素菜', '汤类', '水果', '点心']
const cartItems = ref([])
const cartMeals = ref([])
const cartVisible = ref(false)
const orderForm = reactive({ roomNo: '', remark: '' })

const filteredFoods = computed(() => {
  if (!foodType.value) return allFoods.value
  return allFoods.value.filter(f => f.foodType === foodType.value)
})

const cartCount = computed(() => cartItems.value.length + cartMeals.value.length)
const totalPrice = computed(() => {
  let t = 0
  cartItems.value.forEach(i => t += Number(i.price) || 0)
  cartMeals.value.forEach(i => t += Number(i.price) || 0)
  return t.toFixed(2)
})

const isInCart = (id) => cartItems.value.some(i => i.id === id)
const isInCartMeal = (id) => cartMeals.value.some(i => i.id === id)

const toggleCart = (food) => {
  if (isInCart(food.id)) {
    cartItems.value = cartItems.value.filter(i => i.id !== food.id)
  } else {
    cartItems.value.push({ ...food })
  }
}

const toggleCartMeal = (meal) => {
  if (isInCartMeal(meal.id)) {
    cartMeals.value = cartMeals.value.filter(i => i.id !== meal.id)
  } else {
    cartMeals.value.push({ ...meal })
  }
}

const removeFromCart = (id) => { cartItems.value = cartItems.value.filter(i => i.id !== id) }
const removeFromCartMeal = (id) => { cartMeals.value = cartMeals.value.filter(i => i.id !== id) }

const showCart = () => { cartVisible.value = true }

const getFoodNames = (foodIds) => {
  if (!foodIds) return ''
  return foodIds.split(',').map(id => {
    const f = allFoods.value.find(item => item.id === Number(id))
    return f ? f.foodName : ''
  }).filter(Boolean).join('、')
}

const submitOrder = async () => {
  if (!orderForm.roomNo) {
    ElMessage.warning('请输入房间号')
    return
  }
  const items = []
  cartItems.value.forEach(i => items.push({ type: 'food', name: i.foodName, price: i.price }))
  cartMeals.value.forEach(i => items.push({ type: 'meal', name: i.mealName, price: i.price }))

  const data = {
    customerId: userStore.userInfo?.customerId || userStore.userInfo?.userId,
    roomNo: orderForm.roomNo,
    items: JSON.stringify(items),
    remark: orderForm.remark,
    status: 0
  }
  try {
    await addFoodOrder(data)
    ElMessage.success('下单成功！')
    cartItems.value = []
    cartMeals.value = []
    orderForm.roomNo = ''
    orderForm.remark = ''
    cartVisible.value = false
  } catch (e) {
    console.error('下单失败:', e)
  }
}

const loadData = async () => {
  try {
    const [foodRes, mealRes] = await Promise.all([getFoodList(), getMealPage({ pageNum: 1, pageSize: 100 })])
    allFoods.value = foodRes.data || []
    allMeals.value = mealRes.data?.records || []
  } catch (e) {
    console.error('加载数据失败:', e)
  }
}

onMounted(loadData)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.type-filter { margin-bottom: 16px; }
.food-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 16px; }
.food-card { border: 1px solid #eceae4; border-radius: 12px; overflow: hidden; cursor: pointer; transition: all 0.15s ease; position: relative; background: #fcfbf8; }
.food-card:hover { box-shadow: rgba(0, 0, 0, 0.06) 0px 2px 8px; border-color: rgba(28, 28, 28, 0.4); }
.food-card.selected { border-color: #1c1c1c; box-shadow: rgba(0, 0, 0, 0.1) 0px 4px 12px; }
.food-img { width: 100%; height: 120px; overflow: hidden; display: flex; align-items: center; justify-content: center; background: #faf8f3; }
.food-img img { width: 100%; height: 100%; object-fit: cover; }
.food-img.placeholder { background: #faf8f3; }
.food-info { padding: 12px 16px; }
.food-name { font-size: 14px; font-weight: 600; color: #1c1c1c; margin-bottom: 6px; letter-spacing: -0.3px; }
.food-meta { display: flex; justify-content: space-between; align-items: center; }
.price { color: #5f5f5d; font-weight: 600; }
.check-mark { position: absolute; top: 0; right: 0; width: 28px; height: 28px; background: #1c1c1c; border-radius: 0 10px 0 10px; display: flex; align-items: center; justify-content: center; }
</style>
