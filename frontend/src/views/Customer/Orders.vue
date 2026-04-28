<template>
  <div class="dashboard-container">
    <el-container>
      <el-aside width="200px" class="sidebar">
        <div class="sidebar-title">客户中心</div>
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
          @select="handleMenuSelect"
        >
          <el-menu-item index="dashboard">
            <el-icon><HomeFilled /></el-icon>
            <span>我的主页</span>
          </el-menu-item>
          <el-menu-item index="myOrders">
            <el-icon><Document /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="logistics">
            <el-icon><Van /></el-icon>
            <span>物流跟踪</span>
          </el-menu-item>
          <el-menu-item index="containers">
            <el-icon><Box /></el-icon>
            <span>集装箱管理</span>
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
            <span>欢迎，{{ userStore.username }} (普通客户)</span>
            <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
          </div>
        </el-header>
        <el-main class="main-content">
          <el-card class="orders-card">
            <template #header>
              <div class="card-header">
                <span>我的订单</span>
                <el-button v-if="userStore.role === 'admin'" type="primary" size="small" @click="handleAddOrder">新建订单</el-button>
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
              <el-table-column label="操作">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="handleViewDetail(row)">查看详情</el-button>
                  <el-button type="success" size="small" v-if="row.status === '待处理'" @click="handleConfirm(row)">确认订单</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { HomeFilled, Document, Van, Box, Message } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const activeMenu = ref('myOrders')

const orders = ref([])

const getStatusType = (status) => {
  const statusMap = {
    '待处理': 'warning',
    '进行中': 'primary',
    '已完成': 'success'
  }
  return statusMap[status] || 'info'
}

const handleMenuSelect = (index) => {
  activeMenu.value = index
  const routeMap = {
    'dashboard': '/customer/dashboard',
    'myOrders': '/customer/orders',
    'logistics': '/customer/logistics',
    'containers': '/customer/containers',
    'ai': '/ai/chat'
  }
  if (routeMap[index]) {
    const targetPath = routeMap[index]
    if (router.currentRoute.value.path !== targetPath) {
      router.push(targetPath).catch(() => {})
    }
  }
}

const handleAddOrder = () => {
  ElMessage.info('新建订单功能开发中')
}

const handleViewDetail = (row) => {
  ElMessage.info(`查看订单详情: ${row.orderNumber}`)
}

const handleConfirm = (row) => {
  row.status = '进行中'
  ElMessage.success('订单已确认')
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.dashboard-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
}

.sidebar-title {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: white;
  font-size: 18px;
  font-weight: bold;
  background-color: #263445;
}

.sidebar-menu {
  border: none;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: white;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.main-content {
  background-color: #f5f7fa;
  padding: 20px;
}

.orders-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>