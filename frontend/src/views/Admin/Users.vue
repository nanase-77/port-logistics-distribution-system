<template>
  <div class="dashboard-container">
    <el-container>
      <el-aside width="200px" class="sidebar">
        <div class="sidebar-title">管理员后台</div>
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
          @select="handleMenuSelect"
        >
          <el-menu-item index="dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据概览</span>
          </el-menu-item>
          <el-menu-item index="users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="orders">
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="ports">
            <el-icon><Location /></el-icon>
            <span>港口管理</span>
          </el-menu-item>
          <el-menu-item index="ships">
            <el-icon><Van /></el-icon>
            <span>船舶管理</span>
          </el-menu-item>
          <el-menu-item index="report">
            <el-icon><PieChart /></el-icon>
            <span>数据报表</span>
          </el-menu-item>
          <el-menu-item index="ai">
            <el-icon><Message /></el-icon>
            <span>AI助手</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header class="header">
          <div></div>
          <div class="user-info">
            <span>欢迎，{{ userStore.username }} (管理员)</span>
            <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
          </div>
        </el-header>
        <el-main class="main-content">
          <el-card class="users-card">
            <template #header>
              <div class="card-header">
                <span>用户管理</span>
                <el-button type="primary" size="small" @click="openAddModal">新增用户</el-button>
              </div>
            </template>
            <el-table :data="users" stripe>
              <el-table-column prop="id" label="ID" />
              <el-table-column prop="username" label="用户名" />
              <el-table-column prop="email" label="邮箱" />
              <el-table-column prop="role" label="角色">
                <template #default="{ row }">
                  <el-tag :type="row.role === 'admin' ? 'danger' : 'primary'">{{ row.role === 'admin' ? '管理员' : '普通用户' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'active' ? 'success' : 'warning'">{{ row.status === 'active' ? '活跃' : '禁用' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="openEditModal(row)">编辑</el-button>
                  <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-main>
      </el-container>
    </el-container>

    <el-dialog :title="isEdit ? '编辑用户' : '新增用户'" :visible.sync="showModal">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="form.password" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role">
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="活跃" value="active" />
            <el-option label="禁用" value="disabled" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showModal = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { DataAnalysis, User, Document, Location, Van, PieChart, Message } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const activeMenu = ref('users')
const showModal = ref(false)
const isEdit = ref(false)

const users = ref([])

const form = reactive({
  id: null,
  username: '',
  email: '',
  password: '',
  role: 'user',
  status: 'active'
})

const handleMenuSelect = (index) => {
  activeMenu.value = index
  const routeMap = {
    'dashboard': '/admin/dashboard',
    'users': '/admin/users',
    'orders': '/admin/orders',
    'ports': '/admin/ports',
    'ships': '/admin/ships',
    'report': '/admin/report',
    'ai': '/ai/chat'
  }
  if (routeMap[index]) {
    const targetPath = routeMap[index]
    if (router.currentRoute.value.path !== targetPath) {
      router.push(targetPath).catch(() => {})
    }
  }
}

const openAddModal = () => {
  isEdit.value = false
  form.id = null
  form.username = ''
  form.email = ''
  form.password = ''
  form.role = 'user'
  form.status = 'active'
  showModal.value = true
}

const openEditModal = (row) => {
  isEdit.value = true
  form.id = row.id
  form.username = row.username
  form.email = row.email
  form.password = ''
  form.role = row.role
  form.status = row.status
  showModal.value = true
}

const handleSave = () => {
  if (!form.username || !form.email) {
    ElMessage.warning('请填写用户名和邮箱')
    return
  }
  if (!isEdit.value && !form.password) {
    ElMessage.warning('请设置密码')
    return
  }
  if (isEdit.value) {
    const index = users.value.findIndex(u => u.id === form.id)
    if (index !== -1) {
      users.value[index] = { ...users.value[index], ...form }
    }
    ElMessage.success('用户信息已更新')
  } else {
    const newUser = {
      id: Date.now(),
      ...form
    }
    users.value.unshift(newUser)
    ElMessage.success('用户添加成功')
  }
  showModal.value = false
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', { type: 'warning' })
    users.value = users.value.filter(u => u.id !== row.id)
    ElMessage.success('删除成功')
  } catch {
    ElMessage.info('已取消删除')
  }
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.dashboard-container { height: 100vh; }
.sidebar { background-color: #304156; }
.sidebar-title {
  height: 60px; line-height: 60px; text-align: center;
  color: white; font-size: 18px; font-weight: bold;
  background-color: #263445;
}
.sidebar-menu { border: none; }
.header {
  display: flex; justify-content: space-between; align-items: center;
  background-color: white; border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
}
.user-info { display: flex; align-items: center; gap: 15px; }
.main-content { background-color: #f5f7fa; padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.users-card { margin-bottom: 20px; }
</style>