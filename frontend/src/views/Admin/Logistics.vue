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
          <span>物流跟踪管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="openAddModal">新增物流记录</el-button>
          </div>
        </div>
      </template>
      <el-table :data="filteredLogistics" stripe style="width: 100%;">
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
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewDetail(row)">查看详情</el-button>
            <el-button type="success" size="small" @click="openEditModal(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showModal" :title="isEdit ? '编辑物流' : '新增物流'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="订单">
          <el-select v-model="form.orderId" style="width: 100%;">
            <el-option v-for="order in orders" :key="order.id" :label="order.orderNumber" :value="order.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="起始港口">
          <el-select v-model="form.startPortId" style="width: 100%;">
            <el-option v-for="port in ports" :key="port.id" :label="port.portName" :value="port.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="终点港口">
          <el-select v-model="form.endPortId" style="width: 100%;">
            <el-option v-for="port in ports" :key="port.id" :label="port.portName" :value="port.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="当前港口">
          <el-select v-model="form.currentPortId" style="width: 100%;" placeholder="请选择">
            <el-option :value="null" label="-" />
            <el-option v-for="port in ports" :key="port.id" :label="port.portName" :value="port.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="运输船舶">
          <el-select v-model="form.shipId" style="width: 100%;" placeholder="请选择">
            <el-option :value="null" label="-" />
            <el-option v-for="ship in ships" :key="ship.id" :label="ship.shipName" :value="ship.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="运输车辆">
          <el-select v-model="form.carId" style="width: 100%;" placeholder="请选择">
            <el-option :value="null" label="-" />
            <el-option v-for="car in vehicles" :key="car.id" :label="car.carName" :value="car.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showModal = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

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
import { ref, reactive, computed, markRaw, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { CircleCheck, ArrowRight, Compass, Ship } from '@element-plus/icons-vue'
import { getLogistics, addLogistics, updateLogistics, deleteLogistics } from '@/api/adminLogistics'
import { getOrders } from '@/api/adminOrders'
import { getPorts } from '@/api/adminPorts'
import { getShips } from '@/api/adminShips'
import { getVehicles } from '@/api/adminVehicles'

const showModal = ref(false)
const isEdit = ref(false)
const searchOrderId = ref('')
const showDetailModal = ref(false)
const selectedLogistics = ref(null)

const orders = ref([])
const ports = ref([])
const ships = ref([])
const vehicles = ref([])
const logisticsList = ref([])

const fetchOrders = async () => {
  try {
    const res = await getOrders()
    orders.value = res.records || res || []
  } catch { /* ignore */ }
}

const fetchPorts = async () => {
  try {
    const res = await getPorts()
    ports.value = res.records || res || []
  } catch { /* ignore */ }
}

const fetchShips = async () => {
  try {
    const res = await getShips()
    ships.value = res.records || res || []
  } catch { /* ignore */ }
}

const fetchVehicles = async () => {
  try {
    const res = await getVehicles()
    vehicles.value = res.records || res || []
  } catch { /* ignore */ }
}

const fetchData = async () => {
  try {
    const res = await getLogistics()
    logisticsList.value = res.records || res || []
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

const logisticsDetails = {}

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

const form = reactive({
  id: null,
  orderId: 1,
  startPortId: 1,
  endPortId: 1,
  currentPortId: null,
  shipId: null,
  carId: null
})

const resetForm = () => {
  form.id = null
  form.orderId = 1
  form.startPortId = 1
  form.endPortId = 1
  form.currentPortId = null
  form.shipId = null
  form.carId = null
}

const openAddModal = () => {
  isEdit.value = false
  resetForm()
  showModal.value = true
}

const openEditModal = (row) => {
  isEdit.value = true
  form.id = row.id
  form.orderId = row.orderId
  form.startPortId = row.startPortId
  form.endPortId = row.endPortId
  form.currentPortId = row.currentPortId
  form.shipId = row.shipId
  form.carId = row.carId
  showModal.value = true
}

const handleSave = async () => {
  if (!form.orderId || !form.startPortId || !form.endPortId) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    if (isEdit.value) {
      await updateLogistics({ id: form.id, orderId: form.orderId, startPortId: form.startPortId, endPortId: form.endPortId, currentPortId: form.currentPortId, shipId: form.shipId, carId: form.carId })
      ElMessage.success('物流记录已更新')
    } else {
      await addLogistics({ orderId: form.orderId, startPortId: form.startPortId, endPortId: form.endPortId, currentPortId: form.currentPortId, shipId: form.shipId, carId: form.carId })
      ElMessage.success('物流记录已创建')
    }
    showModal.value = false
    fetchData()
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除物流记录 ID="${row.id}" 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteLogistics(row.id)
      ElMessage.success('物流记录已删除')
      fetchData()
    } catch {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
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

.header-actions {
  display: flex;
  align-items: center;
}
</style>
