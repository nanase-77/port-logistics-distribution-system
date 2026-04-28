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
          <el-row :gutter="20" class="stats-row">
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-value">{{ containers.length }}</div>
                  <div class="stat-label">总集装箱数</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-value">{{ occupiedCount }}</div>
                  <div class="stat-label">使用中</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-value">{{ emptyCount }}</div>
                  <div class="stat-label">空闲</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-value">{{ transitCount }}</div>
                  <div class="stat-label">运输中</div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-card class="containers-card">
            <template #header>
              <div class="card-header">
                <span>集装箱列表</span>
                <el-select v-model="statusFilter" placeholder="筛选状态" style="width: 150px;">
                  <el-option label="全部" value="" />
                  <el-option label="空闲" value="空闲" />
                  <el-option label="使用中" value="使用中" />
                  <el-option label="运输中" value="运输中" />
                </el-select>
              </div>
            </template>
            <el-table :data="filteredContainers" stripe>
              <el-table-column prop="containerNumber" label="集装箱编号" />
              <el-table-column prop="type" label="类型" />
              <el-table-column prop="size" label="尺寸" />
              <el-table-column prop="weight" label="载重(吨)" />
              <el-table-column prop="currentPort" label="当前位置" />
              <el-table-column prop="status" label="状态">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="orderNumber" label="关联订单" />
              <el-table-column label="操作">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="handleViewDetail(row)">详情</el-button>
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
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { HomeFilled, Document, Van, Box, Message } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const activeMenu = ref('containers')
const statusFilter = ref('')

const containers = ref([])

const filteredContainers = computed(() => {
  if (!statusFilter.value) return containers.value
  return containers.value.filter(item => item.status === statusFilter.value)
})

const occupiedCount = computed(() => containers.value.filter(c => c.status === '使用中').length)
const emptyCount = computed(() => containers.value.filter(c => c.status === '空闲').length)
const transitCount = computed(() => containers.value.filter(c => c.status === '运输中').length)

const getStatusType = (status) => {
  const statusMap = {
    '空闲': 'success',
    '使用中': 'primary',
    '运输中': 'warning'
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

const handleViewDetail = (row) => {
  ElMessage.info(`查看集装箱详情: ${row.containerNumber}`)
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

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-content {
  padding: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.containers-card {
  margin-bottom: 20px;
}
</style>