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
          <el-card class="report-card">
            <template #header>
              <span>数据统计报表</span>
              <el-button type="primary" size="small" @click="exportReport">导出报表</el-button>
            </template>
            
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="stat-item">
                  <div class="stat-value">1,258</div>
                  <div class="stat-label">本月吞吐量(TEU)</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stat-item">
                  <div class="stat-value">78.5%</div>
                  <div class="stat-label">订单完成率</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stat-item">
                  <div class="stat-value">92.3%</div>
                  <div class="stat-label">设备利用率</div>
                </div>
              </el-col>
            </el-row>

            <el-row :gutter="20" style="margin-top: 20px;">
              <el-col :span="12">
                <el-card title="月度吞吐量趋势">
                  <el-table :data="monthlyData" stripe size="small">
                    <el-table-column prop="month" label="月份" />
                    <el-table-column prop="throughput" label="吞吐量(TEU)" />
                    <el-table-column prop="growth" label="环比增长">
                      <template #default="{ row }">
                        <el-tag :type="row.growth >= 0 ? 'success' : 'danger'">
                          {{ row.growth >= 0 ? '+' : '' }}{{ row.growth }}%
                        </el-tag>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card title="订单完成情况">
                  <div class="progress-section">
                    <div class="progress-item">
                      <span class="progress-label">已完成</span>
                      <el-progress :percentage="78.5" color="#10b981" />
                      <span class="progress-value">785 单</span>
                    </div>
                    <div class="progress-item">
                      <span class="progress-label">进行中</span>
                      <el-progress :percentage="21.5" color="#3b82f6" />
                      <span class="progress-value">215 单</span>
                    </div>
                    <div class="progress-item">
                      <span class="progress-label">待处理</span>
                      <el-progress :percentage="5.2" color="#f59e0b" />
                      <span class="progress-value">52 单</span>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>

            <el-row :gutter="20" style="margin-top: 20px;">
              <el-col :span="12">
                <el-card title="港口货物周转率">
                  <el-table :data="portTurnover" stripe size="small">
                    <el-table-column prop="port" label="港口" />
                    <el-table-column prop="turnover" label="周转率(次/月)" />
                    <el-table-column prop="rank" label="排名">
                      <template #default="{ row }">
                        <el-tag :type="row.rank <= 3 ? 'primary' : 'info'">第{{ row.rank }}名</el-tag>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card title="设备利用率统计">
                  <el-table :data="equipmentUsage" stripe size="small">
                    <el-table-column prop="type" label="设备类型" />
                    <el-table-column prop="usage" label="利用率">
                      <template #default="{ row }">
                        <el-progress :percentage="row.usage" :color="getUsageColor(row.usage)" :show-text="false" />
                      </template>
                    </el-table-column>
                    <el-table-column prop="count" label="设备数量" />
                  </el-table>
                </el-card>
              </el-col>
            </el-row>
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
import { DataAnalysis, User, Document, Location, Van, PieChart, Message } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const activeMenu = ref('report')

const monthlyData = ref([])
const portTurnover = ref([])

const equipmentUsage = ref([])

const getUsageColor = (usage) => {
  if (usage >= 90) return '#10b981'
  if (usage >= 70) return '#3b82f6'
  return '#f59e0b'
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

const exportReport = () => {
  ElMessage.success('报表导出功能开发中')
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
.report-card { margin-bottom: 20px; }
.stat-item {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px; padding: 20px; text-align: center;
  color: white;
}
.stat-value { font-size: 28px; font-weight: bold; }
.stat-label { font-size: 14px; opacity: 0.9; margin-top: 5px; }
.progress-section { padding: 10px 0; }
.progress-item { margin-bottom: 15px; }
.progress-label { display: inline-block; width: 60px; font-size: 14px; }
.progress-value { float: right; font-size: 14px; font-weight: bold; }
</style>