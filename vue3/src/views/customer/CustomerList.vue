<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-input v-model="search.keyword" placeholder="搜索姓名/手机号" style="width:200px" clearable @clear="loadData" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openAdd">新增客户</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading" style="margin-top:16px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="70">
          <template #default="{ row }">{{ row.gender === 1 ? '男' : row.gender === 0 ? '女' : '' }}</template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="70" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="relativePhone" label="亲属手机号" width="130" />
        <el-table-column prop="nurseLevelName" label="护理等级" width="100" />
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="checkinDate" label="入住日期" width="110" />
        <el-table-column prop="bedNo" label="床位号" width="100" />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑客户' : '新增客户'" width="700px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name"><el-input v-model="form.name" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" style="width:100%">
                <el-option label="男" :value="1" />
                <el-option label="女" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄" prop="age"><el-input-number v-model="form.age" :min="0" :max="150" style="width:100%" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone"><el-input v-model="form.phone" maxlength="11" @input="form.phone = form.phone.replace(/\D/g, '')" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="亲属手机号" prop="relativePhone"><el-input v-model="form.relativePhone" maxlength="11" @input="form.relativePhone = form.relativePhone.replace(/\D/g, '')" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard"><el-input v-model="form.idCard" maxlength="18" @input="form.idCard = form.idCard.replace(/[^\dXx]/g, '')" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入住日期" prop="checkinDate">
              <el-date-picker v-model="form.checkinDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="护理等级" prop="nurseLevelId">
              <el-select v-model="form.nurseLevelId" style="width:100%" placeholder="请选择护理等级" clearable>
                <el-option v-for="nl in nurseLevelList" :key="nl.id" :label="nl.levelName" :value="nl.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="地址" prop="address"><el-input v-model="form.address" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea" /></el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">床位分配</el-divider>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="楼层" prop="selectedFloor">
              <el-select v-model="selectedFloor" style="width:100%" @change="onFloorChange">
                <el-option v-for="f in floors" :key="f" :label="f" :value="f" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="房间" prop="roomNo">
              <el-select v-model="form.roomNo" style="width:100%" :disabled="!selectedFloor" @change="onRoomChange" filterable>
                <el-option v-for="r in floorRooms" :key="r.roomNo" :label="r.roomNo" :value="r.roomNo" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="床位" prop="bedId">
              <el-select v-model="form.bedId" style="width:100%" :disabled="!form.roomNo" placeholder="仅显示空闲床位">
                <el-option v-for="b in availableBeds" :key="b.id" :label="b.bedNo" :value="b.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
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
import { getCustomerPage, addCustomer, updateCustomer, deleteCustomer, getCustomerById, getRoomList, getBedList, getNurseLevelList } from '@/api'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const search = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const form = reactive({
  id: null, name: '', gender: 1, age: null, phone: '', relativePhone: '', idCard: '', checkinDate: '', address: '', remark: '',
  roomNo: '', bedId: null, nurseLevelId: null
})

const validatePhone = (rule, value, callback) => {
  if (value && !/^\d{11}$/.test(value)) {
    ElMessage.warning('手机号必须为11位数字')
    callback(new Error('手机号必须为11位数字'))
  } else {
    callback()
  }
}

const validateRelativePhone = (rule, value, callback) => {
  if (value && !/^\d{11}$/.test(value)) {
    ElMessage.warning('亲属手机号必须为11位数字')
    callback(new Error('亲属手机号必须为11位数字'))
  } else {
    callback()
  }
}

const validateIdCard = (rule, value, callback) => {
  if (value && !/^\d{17}[\dXx]$/.test(value)) {
    ElMessage.warning('身份证号必须为18位数字（最后一位可为X）')
    callback(new Error('身份证号必须为18位数字（最后一位可为X）'))
  } else {
    callback()
  }
}

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }, { validator: validatePhone, trigger: 'blur' }],
  relativePhone: [{ validator: validateRelativePhone, trigger: 'blur' }],
  idCard: [{ validator: validateIdCard, trigger: 'blur' }]
}

// 楼层/房间/床位级联
const floors = ['一楼', '二楼', '三楼']
const floorMap = { '一楼': '1层', '二楼': '2层', '三楼': '3层' }
const selectedFloor = ref('')
const allRooms = ref([])
const allBeds = ref([])
const nurseLevelList = ref([])

const floorRooms = computed(() => {
  if (!selectedFloor.value) return []
  const dbFloor = floorMap[selectedFloor.value]
  return allRooms.value.filter(r => r.floor === dbFloor)
})

const availableBeds = computed(() => {
  if (!form.roomNo) return []
  return allBeds.value.filter(b => b.roomNo === form.roomNo && b.isUsed === 0)
})

const onFloorChange = () => {
  form.roomNo = ''
  form.bedId = null
}

const onRoomChange = () => {
  form.bedId = null
}

const loadRoomBedData = async () => {
  try {
    const [roomRes, bedRes, nurseRes] = await Promise.all([getRoomList(), getBedList(), getNurseLevelList()])
    allRooms.value = roomRes.data || []
    allBeds.value = bedRes.data || []
    nurseLevelList.value = nurseRes.data || []
  } catch (e) {
    console.error('加载房间床位数据失败:', e)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCustomerPage(search)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

const openAdd = () => {
  isEdit.value = false
  resetForm()
  loadRoomBedData()
  dialogVisible.value = true
}

const openEdit = async (row) => {
  isEdit.value = true
  await loadRoomBedData()
  const res = await getCustomerById(row.id)
  Object.assign(form, res.data)
  // 回显楼层
  if (form.roomNo) {
    const room = allRooms.value.find(r => r.roomNo === form.roomNo)
    if (room) {
      selectedFloor.value = Object.keys(floorMap).find(k => floorMap[k] === room.floor) || ''
    }
  }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该客户吗？', '提示', { type: 'warning' })
  await deleteCustomer(id)
  ElMessage.success('删除成功')
  loadData()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (isEdit.value) { await updateCustomer(form); ElMessage.success('修改成功') }
  else { await addCustomer(form); ElMessage.success('新增成功') }
  dialogVisible.value = false
  loadData()
}

const resetForm = () => {
  Object.assign(form, { id: null, name: '', gender: 1, age: null, phone: '', relativePhone: '', idCard: '', checkinDate: '', address: '', remark: '', roomNo: '', bedId: null, nurseLevelId: null })
  selectedFloor.value = ''
  formRef.value?.resetFields()
}

onMounted(loadData)
</script>

<style scoped>
.search-bar { display: flex; gap: 12px; }
</style>
