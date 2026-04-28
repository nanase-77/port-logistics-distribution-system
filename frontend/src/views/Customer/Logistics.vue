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
          <el-card class="search-card">
            <el-input 
              v-model="searchOrderNumber" 
              placeholder="输入订单号查询物流信息" 
              style="width: 300px;"
              @keyup.enter="handleSearch"
            >
              <template #append>
                <el-button @click="handleSearch">搜索</el-button>
              </template>
            </el-input>
          </el-card>

          <el-card v-if="selectedLogistics" class="logistics-detail">
            <template #header>
              <span>物流详情 - {{ selectedLogistics.orderNumber }}</span>
              <el-button size="small" @click="selectedLogistics = null">返回列表</el-button>
            </template>
            <el-timeline>
              <el-timeline-item 
                v-for="(event, index) in selectedLogistics.tracking" 
                :key="index"
                :timestamp="event.time"
                :type="event.type"
              >
                <template #icon>
                  <component :is="getIcon(event.type)" />
                </template>
                {{ event.description }}
              </el-timeline-item>
            </el-timeline>
          </el-card>

          <el-card v-else class="logistics-list">
            <template #header>
              <span>物流跟踪列表</span>
            </template>
            <el-table :data="logisticsList" stripe @row-click="handleSelectLogistics">
              <el-table-column prop="orderNumber" label="订单号" />
              <el-table-column prop="currentLocation" label="当前位置" />
              <el-table-column prop="status" label="状态">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="estimatedArrival" label="预计到达时间" />
              <el-table-column prop="progress" label="进度">
                <template #default="{ row }">
                  <el-progress :percentage="row.progress" :color="getProgressColor(row.progress)" />
                </template>
              </el-table-column>
              <el-table-column label="操作">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="handleSelectLogistics(row)">查看详情</el-button>
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
import { ref, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { HomeFilled, Document, Van, Box, Compass, ArrowRight, Ship, CircleCheck, Message } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const activeMenu = ref('logistics')
const searchOrderNumber = ref('')
const selectedLogistics = ref(null)

const logisticsList = ref([])
const logisticsDetails = ref({})

const getStatusType = (status) => {
  const statusMap = {
    '待处理': 'warning',
    '运输中': 'primary',
    '已到达': 'success'
  }
  return statusMap[status] || 'info'
}

const getProgressColor = (progress) => {
  if (progress >= 100) return '#10b981'
  if (progress >= 50) return '#3b82f6'
  return '#f59e0b'
}

const getIcon = (type) => {
  const iconMap = {
    'success': markRaw(CircleCheck),
    'primary': markRaw(ArrowRight),
    'warning': markRaw(Compass),
    'info': markRaw(Ship)
  }
  return iconMap[type] || markRaw(Compass)
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

const handleSearch = () => {
  if (searchOrderNumber.value) {
    const found = logisticsList.value.find(item => item.orderNumber === searchOrderNumber.value)
    if (found) {
      handleSelectLogistics(found)
    } else {
      ElMessage.warning('未找到该订单')
    }
  } else {
    selectedLogistics.value = null
  }
}

const handleSelectLogistics = (row) => {
  selectedLogistics.value = logisticsDetails[row.orderNumber] || {
    orderNumber: row.orderNumber,
    tracking: [
      { time: '暂无详细跟踪信息', type: 'info', description: '正在获取物流信息...' }
    ]
  }
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

.search-card {
  margin-bottom: 20px;
}

.logistics-detail {
  margin-bottom: 20px;
}

.logistics-list {
  margin-bottom: 20px;
}
</style>