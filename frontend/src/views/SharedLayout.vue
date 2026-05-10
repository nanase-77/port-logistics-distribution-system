<template>
  <div class="dashboard-container">
    <el-container>
      <el-aside width="200px" class="sidebar">
        <div class="sidebar-title">{{ role === 'admin' ? '管理员后台' : '客户中心' }}</div>
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
          @select="handleMenuSelect"
        >
          <el-menu-item v-for="item in menuItems" :key="item.index" :index="item.index">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header class="header">
          <div></div>
          <div class="user-info">
            <span>欢迎，{{ userStore.username }} ({{ role === 'admin' ? '管理员' : '普通客户' }})</span>
            <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
          </div>
        </el-header>
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed, markRaw } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import {
  DataAnalysis, User, Document, Location, Van, Connection, PieChart,
  OfficeBuilding, Box, Message, HomeFilled
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const role = computed(() => {
  if (route.path.startsWith('/admin')) return 'admin'
  if (route.path.startsWith('/customer')) return 'user'
  return 'user'
})

const activeMenu = computed(() => {
  const path = route.path
  const segments = path.split('/')
  return segments[segments.length - 1] || 'dashboard'
})

const adminMenuItems = [
  { index: 'dashboard', icon: markRaw(DataAnalysis), label: '数据概览' },
  { index: 'users', icon: markRaw(User), label: '用户管理' },
  { index: 'orders', icon: markRaw(Document), label: '订单管理' },
  { index: 'ships', icon: markRaw(Van), label: '船舶管理' },
  { index: 'vehicles', icon: markRaw(Van), label: '车辆管理' },
  { index: 'companies', icon: markRaw(OfficeBuilding), label: '公司管理' },
  { index: 'logistics', icon: markRaw(Van), label: '物流跟踪' },
  { index: 'ports', icon: markRaw(Location), label: '港口管理' },
  { index: 'containers', icon: markRaw(Box), label: '集装箱管理' },
  { index: 'dispatch', icon: markRaw(Connection), label: '智能调度' },
  { index: 'report', icon: markRaw(PieChart), label: '数据报表' },
  { index: 'ai', icon: markRaw(Message), label: 'AI助手' },
]

const userMenuItems = [
  { index: 'dashboard', icon: markRaw(HomeFilled), label: '我的主页' },
  { index: 'orders', icon: markRaw(Document), label: '我的订单' },
  { index: 'ships', icon: markRaw(Van), label: '船舶查询' },
  { index: 'vehicles', icon: markRaw(Van), label: '车辆查询' },
  { index: 'companies', icon: markRaw(OfficeBuilding), label: '公司查询' },
  { index: 'logistics', icon: markRaw(Van), label: '物流跟踪' },
  { index: 'ports', icon: markRaw(Location), label: '港口查询' },
  { index: 'containers', icon: markRaw(Box), label: '集装箱管理' },
  { index: 'ai', icon: markRaw(Message), label: 'AI助手' },
]

const menuItems = computed(() => role.value === 'admin' ? adminMenuItems : userMenuItems)

const routePrefix = computed(() => role.value === 'admin' ? '/admin' : '/customer')

const handleMenuSelect = (index) => {
  const targetPath = `${routePrefix.value}/${index}`
  if (route.path !== targetPath) {
    router.push(targetPath).catch(() => {})
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
  display: flex;
  overflow: hidden;
}

:deep(.el-container) {
  height: 100%;
  display: flex;
}

:deep(.el-aside) {
  position: fixed !important;
  left: 0;
  top: 0;
  bottom: 0;
  width: 200px !important;
  z-index: 999;
  overflow-y: auto;
}

.sidebar {
  background-color: #304156;
  height: 100%;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: 200px;
}

.sidebar-title {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: white;
  font-size: 18px;
  font-weight: bold;
  background-color: #263445;
  flex-shrink: 0;
}

.sidebar-menu {
  border: none;
  flex: 1;
  overflow-y: auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: white;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.main-content {
  background-color: #f5f7fa;
  margin-left: 200px;
  min-height: 100vh;
  padding: 20px;
  overflow-y: auto;
}
</style>
