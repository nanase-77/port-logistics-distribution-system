<template>
  <div>
    <el-card class="search-card">
      <el-input 
        v-model="searchOrderId" 
        placeholder="输入订单ID查询" 
        style="width: 300px;"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>物流跟踪</span>
        </div>
      </template>
      <el-table :data="filteredLogistics" stripe style="width: 100%;">
        <el-table-column label="序号" width="80" type="index" :index="(index) => index + 1" />
        <el-table-column label="订单号">
          <template #default="{ row }">{{ getOrderNumber(row.orderId) }}</template>
        </el-table-column>
        <el-table-column label="起始港口">
          <template #default="{ row }">{{ getPortName(row.startPortId) }}</template>
        </el-table-column>
        <el-table-column label="终点港口">
          <template #default="{ row }">{{ getPortName(row.endPortId) }}</template>
        </el-table-column>
        <el-table-column label="当前位置">
          <template #default="{ row }">{{ getPortName(row.currentPortId) }}</template>
        </el-table-column>
        <el-table-column label="运输船舶">
          <template #default="{ row }">{{ getShipName(row.shipId) }}</template>
        </el-table-column>
        <el-table-column label="运输车辆">
          <template #default="{ row }">{{ getCarName(row.carId) }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getLogistics } from '@/api/customerLogistics'
import { getOrders } from '@/api/customerOrders'
import { getPorts } from '@/api/customerPorts'
import { getShips } from '@/api/customerShips'
import { getVehicles } from '@/api/customerVehicles'

const searchOrderId = ref('')

const orders = ref([])
const ports = ref([])
const ships = ref([])
const vehicles = ref([])
const logisticsList = ref([])

const fetchOrders = async () => {
  try { const res = await getOrders(); orders.value = res || [] } catch { /* ignore */ }
}
const fetchPorts = async () => {
  try { const res = await getPorts(); ports.value = res.records || res || [] } catch { /* ignore */ }
}
const fetchShips = async () => {
  try { const res = await getShips(); ships.value = res.records || res || [] } catch { /* ignore */ }
}
const fetchVehicles = async () => {
  try { const res = await getVehicles(); vehicles.value = res.records || res || [] } catch { /* ignore */ }
}
const fetchData = async () => {
  try {
    const res = await getLogistics()
    logisticsList.value = res || []
  } catch {
    ElMessage.error('获取物流列表失败')
  }
}

onMounted(() => {
  fetchOrders()
  fetchPorts()
  fetchShips()
  fetchVehicles()
  fetchData()
})

const getOrderNumber = (orderId) => {
  const order = orders.value.find(o => o.id === orderId)
  return order ? order.orderNumber : `订单${orderId}`
}

const getPortName = (portId) => {
  if (!portId) return '-'
  const port = ports.value.find(p => p.id === portId)
  return port ? port.portName : `港口${portId}`
}

const getShipName = (shipId) => {
  if (!shipId) return '-'
  const ship = ships.value.find(s => s.id === shipId)
  return ship ? ship.shipName : `船舶${shipId}`
}

const getCarName = (carId) => {
  if (!carId) return '-'
  const car = vehicles.value.find(v => v.id === carId)
  return car ? car.carName : `车辆${carId}`
}

const filteredLogistics = computed(() => {
  if (!searchOrderId.value) return logisticsList.value
  return logisticsList.value.filter(item => String(item.orderId).includes(searchOrderId.value))
})

const handleSearch = () => {
  if (searchOrderId.value) {
    const found = logisticsList.value.find(item => String(item.orderId).includes(searchOrderId.value))
    if (!found) {
      ElMessage.warning('未找到该物流记录')
    }
  }
}
</script>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
