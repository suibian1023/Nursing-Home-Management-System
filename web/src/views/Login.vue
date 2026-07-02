<template>
  <div class="login-container">
    <div class="login-card">
      <h2>东软颐养中心</h2>
      <p class="subtitle">养老管理系统</p>
      <el-form :model="form" :rules="rules" ref="formRef" size="large">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin" style="width:100%">登 录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  // username: 'admin',
  // password: '123456'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功')
    router.push('/')
  } catch {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f7f4ed;
}
.login-card {
  width: 400px;
  max-width: 92vw;
  padding: 48px 40px;
  background: #fcfbf8;
  border: 1px solid #eceae4;
  border-radius: 16px;
  box-shadow: rgba(0, 0, 0, 0.08) 0px 8px 32px;
}
.login-card h2 {
  text-align: center;
  color: #1c1c1c;
  font-weight: 600;
  letter-spacing: -1px;
  font-size: 28px;
  margin-bottom: 4px;
}
.subtitle {
  text-align: center;
  color: #5f5f5d;
  margin-bottom: 32px;
  font-size: 14px;
}

@media screen and (max-width: 768px) {
  .login-card {
    width: 92vw;
    padding: 32px 20px;
    border-radius: 12px;
  }
  .login-card h2 {
    font-size: 22px;
  }
  .subtitle {
    font-size: 13px;
    margin-bottom: 24px;
  }
}
</style>
