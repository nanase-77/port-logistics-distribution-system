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
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyLogistics } from '@/api/customerLogistic'
import { getMyOrders } from '@/api/customerOrder'
import { getPorts } from '@/api/customerPort'

const searchOrderId = ref('')
const showDetailModal = ref(false)
const selectedLogistics = ref(null)

const orders = ref([])
const ports = ref([])
const logisticsList = ref([])

const fetchOrders = async () => {
  try {
    const res = await getMyOrders()
    orders.value = res.records || res || []
  } catch { /* ignore */ }
}

const fetchPorts = async () => {
  try {
    const res = await getPorts()
    ports.value = res.records || res || []
  } catch { /* ignore */ }
}

const fetchData = async () => {
  try {
    const res = await getMyLogistics()
    logisticsList.value = res.records || res || []
  } catch {
    ElMessage.error('获取物流列表失败')
  }
}

onMounted(() => {
  fetchOrders()
  fetchPorts()
  fetchData()
})

const getOrderNumber = (orderId) => {
  const order = orders.value.find(o => o.id === orderId)
  return order ? order.orderNumber : `订单${orderId}`
}

const getPortName = (portId) => {
  if (!portId) return '-'
  const port = ports.value.find(p => p.id === portId)
  return port ? port.name : `港口${portId}`
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

const handleViewDetail = (row) => {
  ElMessage.info(`查看物流详情: 订单${row.orderId}`)
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