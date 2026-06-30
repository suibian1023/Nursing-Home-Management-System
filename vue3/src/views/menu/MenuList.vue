<template>
  <div class="page-container">
    <el-card>
      <el-table :data="tableData" border stripe v-loading="loading" row-key="id">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="menuName" label="菜单名称" width="160" />
        <el-table-column prop="menuType" label="类型" width="80">
          <template #default="{ row }">{{ row.menuType === 0 ? '目录' : row.menuType === 1 ? '菜单' : '按钮' }}</template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" width="180" />
        <el-table-column prop="icon" label="图标" width="80" />
        <el-table-column prop="sortOrder" label="排序" width="60" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMenuList, deleteMenu } from '@/api'

const loading = ref(false)
const tableData = ref([])

const loadData = async () => {
  loading.value = true
  try { const res = await getMenuList(); tableData.value = res.data || [] } finally { loading.value = false }
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' })
  await deleteMenu(id); ElMessage.success('删除成功'); loadData()
}

onMounted(loadData)
</script>
