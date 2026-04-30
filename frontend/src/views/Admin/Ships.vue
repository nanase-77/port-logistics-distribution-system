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
          <el-card class="ships-card">
            <template #header>
              <div class="card-header">
                <span>运输工具管理</span>
                <el-button type="primary" size="small" @click="openAddModal">新增船舶</el-button>
              </div>
            </template>
            <el-table :data="ships" stripe>
              <el-table-column prop="id" label="ID" />
              <el-table-column prop="name" label="船舶名称" />
              <el-table-column prop="type" label="船舶类型" />
              <el-table-column prop="tonnage" label="载重吨位(吨)" />
              <el-table-column prop="currentPort" label="当前位置" />
              <el-table-column prop="status" label="状态">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="nextOrder" label="关联订单" />
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

    <el-dialog :title="isEdit ? '编辑船舶' : '新增船舶'" :visible.sync="showModal">
      <el-form :model="form" label-width="120px">
        <el-form-item label="船舶名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="船舶类型">
          <el-select v-model="form.type">
            <el-option label="集装箱船" value="container" />
            <el-option label="散货船" value="bulk" />
            <el-option label="油轮" value="tanker" />
            <el-option label="杂货船" value="general" />
          </el-select>
        </el-form-item>
        <el-form-item label="载重吨位(吨)">
          <el-input type="number" v-model="form.tonnage" />
        </el-form-item>
        <el-form-item label="当前位置">
          <el-input v-model="form.currentPort" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="停泊中" value="docked" />
            <el-option label="航行中" value="sailing" />
            <el-option label="维修中" value="repair" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联订单">
          <el-input v-model="form.nextOrder" />
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
const activeMenu = ref('ships')
const showModal = ref(false)
const isEdit = ref(false)

const ships = ref([])

const form = reactive({
  id: null,
  name: '',
  type: 'container',
  tonnage: 0,
  currentPort: '',
  status: 'docked',
  nextOrder: '-'
})

const getStatusType = (status) => {
  const statusMap = { 'docked': 'success', 'sailing': 'primary', 'repair': 'warning' }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const statusMap = { 'docked': '停泊中', 'sailing': '航行中', 'repair': '维修中' }
  return statusMap[status] || status
}

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
  form.type = 'container'
  form.tonnage = 0
  form.currentPort = ''
  form.status = 'docked'
  form.nextOrder = '-'
  showModal.value = true
}

const openEditModal = (row) => {
  isEdit.value = true
  form.id = row.id
  form.name = row.name
  form.type = row.type
  form.tonnage = row.tonnage
  form.currentPort = row.currentPort
  form.status = row.status
  form.nextOrder = row.nextOrder
  showModal.value = true
}

const handleSave = () => {
  if (!form.name) {
    ElMessage.warning('请填写船舶名称')
    return
  }
  if (isEdit.value) {
    const index = ships.value.findIndex(s => s.id === form.id)
    if (index !== -1) {
      ships.value[index] = { ...ships.value[index], ...form }
    }
    ElMessage.success('船舶信息已更新')
  } else {
    const newShip = { id: Date.now(), ...form }
    ships.value.unshift(newShip)
    ElMessage.success('船舶添加成功')
  }
  showModal.value = false
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该船舶吗？', '提示', { type: 'warning' })
    ships.value = ships.value.filter(s => s.id !== row.id)
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
.ships-card { margin-bottom: 20px; }
</style>