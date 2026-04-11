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
        >
          <el-menu-item index="dashboard">
            <el-icon><Home /></el-icon>
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
          <el-row :gutter="20" class="stat-row">
            <el-col :span="8">
              <el-card class="stat-card">
                <div class="stat-icon pending-icon">
                  <el-icon size="30"><Clock /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">5</div>
                  <div class="stat-label">待处理订单</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card class="stat-card">
                <div class="stat-icon progress-icon">
                  <el-icon size="30"><Loading /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">8</div>
                  <div class="stat-label">进行中订单</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card class="stat-card">
                <div class="stat-icon success-icon">
                  <el-icon size="30"><SuccessFilled /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">23</div>
                  <div class="stat-label">已完成订单</div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-card class="recent-orders">
            <template #header>
              <span>最近订单</span>
            </template>
            <el-table :data="recentOrders" stripe>
              <el-table-column prop="orderNumber" label="订单号" />
              <el-table-column prop="createTime" label="创建时间" />
              <el-table-column prop="status" label="状态">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="startPort" label="起始港口" />
              <el-table-column prop="endPort" label="目的港口" />
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
import { Home, Document, Van, Box, Clock, Loading, SuccessFilled } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const activeMenu = ref('dashboard')

const recentOrders = ref([
  { orderNumber: 'ORD202604001', createTime: '2026-04-10 14:30', status: '待处理', startPort: '上海港', endPort: '新加坡港' },
  { orderNumber: 'ORD202604002', createTime: '2026-04-09 10:15', status: '进行中', startPort: '深圳港', endPort: '洛杉矶港' },
  { orderNumber: 'ORD202604003', createTime: '2026-04-08 16:45', status: '已完成', startPort: '宁波港', endPort: '汉堡港' },
  { orderNumber: 'ORD202604004', createTime: '2026-04-07 09:20', status: '进行中', startPort: '青岛港', endPort: '釜山港' },
  { orderNumber: 'ORD202604005', createTime: '2026-04-06 11:50', status: '已完成', startPort: '天津港', endPort: '东京港' }
])

const getStatusType = (status) => {
  const statusMap = {
    '待处理': 'warning',
    '进行中': 'primary',
    '已完成': 'success'
  }
  return statusMap[status] || 'info'
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
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  color: white;
}

.pending-icon {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.progress-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.success-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.recent-orders {
  margin-top: 20px;
}
</style>
