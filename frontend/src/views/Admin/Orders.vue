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
          <el-card class="orders-card">
            <template #header>
              <div class="card-header">
                <span>订单管理</span>
                <el-button type="primary" size="small" @click="openAddModal">新建订单</el-button>
              </div>
            </template>
            <el-table :data="orders" stripe>
              <el-table-column prop="orderNumber" label="订单号" />
              <el-table-column prop="createTime" label="创建时间" />
              <el-table-column prop="status" label="状态">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="startPort" label="起始港口" />
              <el-table-column prop="endPort" label="目的港口" />
              <el-table-column prop="cargoType" label="货物类型" />
              <el-table-column prop="weight" label="重量(吨)" />
              <el-table-column prop="owner" label="货主" />
              <el-table-column label="操作">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="openEditModal(row)">编辑</el-button>
                  <el-button v-if="row.status === '待处理'" type="success" size="small" @click="handleApprove(row)">审核通过</el-button>
                  <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-main>
      </el-container>
    </el-container>

    <el-dialog :title="isEdit ? '编辑订单' : '新建订单'" :visible.sync="showModal">
      <el-form :model="form" label-width="80px">
        <el-form-item label="订单号">
          <el-input v-model="form.orderNumber" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="起始港口">
          <el-input v-model="form.startPort" />
        </el-form-item>
        <el-form-item label="目的港口">
          <el-input v-model="form.endPort" />
        </el-form-item>
        <el-form-item label="货物类型">
          <el-input v-model="form.cargoType" />
        </el-form-item>
        <el-form-item label="重量(吨)">
          <el-input type="number" v-model="form.weight" />
        </el-form-item>
        <el-form-item label="货主">
          <el-input v-model="form.owner" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="待处理" value="待处理" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
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
const activeMenu = ref('orders')
const showModal = ref(false)
const isEdit = ref(false)

const orders = ref([])

const form = reactive({
  orderNumber: '',
  createTime: '',
  status: '待处理',
  startPort: '',
  endPort: '',
  cargoType: '',
  weight: 0,
  owner: ''
})

const getStatusType = (status) => {
  const statusMap = { '待处理': 'warning', '进行中': 'primary', '已完成': 'success' }
  return statusMap[status] || 'info'
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
  form.orderNumber = 'ORD' + Date.now().toString().slice(-9)
  form.createTime = new Date().toLocaleString('zh-CN')
  form.status = '待处理'
  form.startPort = ''
  form.endPort = ''
  form.cargoType = ''
  form.weight = 0
  form.owner = ''
  showModal.value = true
}

const openEditModal = (row) => {
  isEdit.value = true
  form.orderNumber = row.orderNumber
  form.createTime = row.createTime
  form.status = row.status
  form.startPort = row.startPort
  form.endPort = row.endPort
  form.cargoType = row.cargoType
  form.weight = row.weight
  form.owner = row.owner
  showModal.value = true
}

const handleSave = () => {
  if (!form.startPort || !form.endPort || !form.cargoType || !form.owner) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (isEdit.value) {
    const index = orders.value.findIndex(o => o.orderNumber === form.orderNumber)
    if (index !== -1) {
      orders.value[index] = { ...orders.value[index], ...form }
    }
    ElMessage.success('订单信息已更新')
  } else {
    orders.value.unshift({ ...form })
    ElMessage.success('订单创建成功')
  }
  showModal.value = false
}

const handleApprove = (row) => {
  row.status = '进行中'
  ElMessage.success('订单已审核通过')
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该订单吗？', '提示', { type: 'warning' })
    orders.value = orders.value.filter(o => o.orderNumber !== row.orderNumber)
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
.orders-card { margin-bottom: 20px; }
</style>