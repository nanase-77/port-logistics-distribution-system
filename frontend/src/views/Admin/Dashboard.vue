<template>
  <div>
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon user-icon">
            <el-icon size="30"><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.users }}</div>
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
            <div class="stat-value">{{ stats.orders }}</div>
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
            <div class="stat-value">{{ stats.ports }}</div>
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
            <div class="stat-value">{{ stats.ships }}</div>
            <div class="stat-label">船舶数量</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { User, Document, Location, Van } from '@element-plus/icons-vue'
import {getUserCount} from '@/api/user'
import { getOrderCount } from '@/api/orders'
import { getPorts } from '@/api/ports'
import {getShipCount, getShips} from '@/api/ships'

const stats = reactive({
  users: 0,
  orders: 0,
  ports: 0,
  ships: 0
})

const fetchStats = async () => {
  try {
    const [usersRes, ordersRes, portsRes, shipsRes] = await Promise.all([
      getUserCount(),
      getOrderCount(),
      // getPorts(),
        0,
      getShipCount()
    ])
    console.log("yyy"+shipsRes)
    stats.users = usersRes || 0
     stats.orders = ordersRes || 0
     stats.ports = portsRes || 0
     stats.ships = shipsRes || 0
  } catch { /* ignore */ }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
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
