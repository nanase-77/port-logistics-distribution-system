<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <span>港口物流分配系统</span>
        </div>
      </template>
      
      <!-- 登录类型切换 -->
      <div class="login-type-tabs">
        <div 
          :class="['tab-item', { active: loginType === 'admin' }]" 
          @click="loginType = 'admin'"
        >
          管理员登录
        </div>
        <div 
          :class="['tab-item', { active: loginType === 'user' }]" 
          @click="loginType = 'user'"
        >
          用户登录
        </div>
      </div>
      
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" :loading="loading" @click="handleLogin">
            登录
          </el-button>
        </el-form-item>
        <div class="link-container">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const loading = ref(false)
const loginType = ref('admin') // 'admin' 或 'user'

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const result = await login(loginForm)
        
        // 检查选择的登录类型与实际用户角色是否匹配
        if (loginType.value === 'admin' && result.role !== 'admin') {
          ElMessage.error('请使用管理员账号登录')
          return
        }
        
        if (loginType.value === 'user' && result.role === 'admin') {
          ElMessage.error('请使用普通用户账号登录')
          return
        }
        
        userStore.login(result)
        ElMessage.success('登录成功')
        if (result.role === 'admin') {
          router.push('/admin/dashboard')
        } else {
          router.push('/customer/dashboard')
        }
      } catch (error) {
        console.error('登录失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-image: url('./hsh.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.login-card {
  width: 400px;
}

.card-header {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

/* 登录类型切换标签 */
.login-type-tabs {
  display: flex;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 20px;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 12px 0;
  font-size: 16px;
  color: #606266;
  cursor: pointer;
  position: relative;
  transition: color 0.2s;
}

.tab-item:hover {
  color: #409eff;
}

.tab-item.active {
  color: #409eff;
  font-weight: bold;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 20%;
  right: 20%;
  height: 2px;
  background-color: #409eff;
}

.link-container {
  text-align: center;
  margin-top: 10px;
}

.link-container a {
  color: #409eff;
  text-decoration: none;
}
</style>
