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
          <el-row :gutter="20" class="stat-row">
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-icon user-icon">
                  <el-icon size="30"><User /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">156</div>
                  <div class="stat-label">用户总数</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-icon order-icon">
                  <el-icon size="30"><Document /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">328</div>
                  <div class="stat-label">订单总数</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-icon port-icon">
                  <el-icon size="30"><Location /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">42</div>
                  <div class="stat-label">港口数量</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-icon ship-icon">
                  <el-icon size="30"><Van /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">86</div>
                  <div class="stat-label">船舶数量</div>
                </div>
              </el-card>
            </el-col>
          </el-row>
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
import { DataAnalysis, User, Document, Location, Van } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const activeMenu = ref('dashboard')

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

.user-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.order-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.port-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.ship-icon {
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
</style>
