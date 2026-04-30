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
          <el-card class="ports-card">
            <template #header>
              <div class="card-header">
                <span>港口资源管理</span>
                <el-button type="primary" size="small" @click="openAddModal">新增港口</el-button>
              </div>
            </template>
            <el-table :data="ports" stripe>
              <el-table-column prop="id" label="ID" />
              <el-table-column prop="name" label="港口名称" />
              <el-table-column prop="location" label="地理位置" />
              <el-table-column prop="berthCount" label="泊位数" />
              <el-table-column prop="maxWaterDepth" label="最大水深(m)" />
              <el-table-column prop="yardCapacity" label="堆场容量(万TEU)" />
              <el-table-column prop="status" label="状态">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'active' ? 'success' : 'warning'">{{ row.status === 'active' ? '运营中' : '维护中' }}</el-tag>
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

    <el-dialog :title="isEdit ? '编辑港口' : '新增港口'" :visible.sync="showModal">
      <el-form :model="form" label-width="120px">
        <el-form-item label="港口名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="地理位置">
          <el-input v-model="form.location" />
        </el-form-item>
        <el-form-item label="泊位数">
          <el-input type="number" v-model="form.berthCount" />
        </el-form-item>
        <el-form-item label="最大水深(m)">
          <el-input type="number" v-model="form.maxWaterDepth" />
        </el-form-item>
        <el-form-item label="堆场容量(万TEU)">
          <el-input type="number" v-model="form.yardCapacity" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="运营中" value="active" />
            <el-option label="维护中" value="maintenance" />
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
const activeMenu = ref('ports')
const showModal = ref(false)
const isEdit = ref(false)

const ports = ref([])

const form = reactive({
  id: null,
  name: '',
  location: '',
  berthCount: 0,
  maxWaterDepth: 0,
  yardCapacity: 0,
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
  form.name = ''
  form.location = ''
  form.berthCount = 0
  form.maxWaterDepth = 0
  form.yardCapacity = 0
  form.status = 'active'
  showModal.value = true
}

const openEditModal = (row) => {
  isEdit.value = true
  form.id = row.id
  form.name = row.name
  form.location = row.location
  form.berthCount = row.berthCount
  form.maxWaterDepth = row.maxWaterDepth
  form.yardCapacity = row.yardCapacity
  form.status = row.status
  showModal.value = true
}

const handleSave = () => {
  if (!form.name || !form.location) {
    ElMessage.warning('请填写港口名称和位置')
    return
  }
  if (isEdit.value) {
    const index = ports.value.findIndex(p => p.id === form.id)
    if (index !== -1) {
      ports.value[index] = { ...ports.value[index], ...form }
    }
    ElMessage.success('港口信息已更新')
  } else {
    const newPort = { id: Date.now(), ...form }
    ports.value.unshift(newPort)
    ElMessage.success('港口添加成功')
  }
  showModal.value = false
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该港口吗？', '提示', { type: 'warning' })
    ports.value = ports.value.filter(p => p.id !== row.id)
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
.ports-card { margin-bottom: 20px; }
</style>