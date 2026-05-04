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
      <el-table :data="filteredLogistics" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="订单号" width="160">
          <template #default="{ row }">{{ getOrderNumber(row.orderId) }}</template>
        </el-table-column>
        <el-table-column label="起始港口" width="140">
          <template #default="{ row }">{{ getPortName(row.startPortId) }}</template>
        </el-table-column>
        <el-table-column label="终点港口" width="140">
          <template #default="{ row }">{{ getPortName(row.endPortId) }}</template>
        </el-table-column>
        <el-table-column label="当前位置" width="140">
          <template #default="{ row }">{{ getPortName(row.currentPortId) }}</template>
        </el-table-column>
        <el-table-column label="运输船舶" width="120">
          <template #default="{ row }">{{ getShipName(row.shipId) }}</template>
        </el-table-column>
        <el-table-column label="运输车辆" width="120">
          <template #default="{ row }">{{ getCarName(row.carId) }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showDetailModal" :title="'物流详情 - ' + selectedLogistics?.orderNumber" width="500px">
      <el-timeline>
        <el-timeline-item 
          v-for="(event, index) in selectedLogistics?.tracking" 
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
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, markRaw, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Compass, ArrowRight, Ship, CircleCheck } from '@element-plus/icons-vue'

const searchOrderId = ref('')
const showDetailModal = ref(false)
const selectedLogistics = ref(null)

const orders = ref([])

const ports = ref([])

const ships = ref([])

const vehicles = ref([])

const logisticsList = ref([])

const logisticsDetails = {}

const getOrderNumber = (orderId) => {
  const order = orders.value.find(o => o.id === orderId)
  return order ? order.orderNumber : `订单${orderId}`
}

const getPortName = (portId) => {
  if (!portId) return '-'
  const port = ports.value.find(p => p.id === portId)
  return port ? port.name : `港口${portId}`
}

const getShipName = (shipId) => {
  if (!shipId) return '-'
  const ship = ships.value.find(s => s.id === shipId)
  return ship ? ship.name : `船舶${shipId}`
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

const getIcon = (type) => {
  const iconMap = {
    'success': markRaw(CircleCheck),
    'primary': markRaw(ArrowRight),
    'warning': markRaw(Compass),
    'info': markRaw(Ship)
  }
  return iconMap[type] || markRaw(Compass)
}

const handleViewDetail = (row) => {
  selectedLogistics.value = logisticsDetails[row.id] || {
    orderNumber: getOrderNumber(row.orderId),
    tracking: [
      { time: '暂无详细跟踪信息', type: 'info', description: '正在获取物流信息...' }
    ]
  }
  showDetailModal.value = true
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
