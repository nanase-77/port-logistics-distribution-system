<template>
  <div>
    <el-card class="search-card">
      <el-input
        v-model="searchOrderNumber"
        placeholder="输入订单号查询"
        style="width: 300px;"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </el-card>

    <el-card class="orders-card">
      <template #header>
        <div class="card-header">
          <span>我的订单</span>
          <el-button type="primary" @click="openAddModal">新增订单</el-button>
        </div>
      </template>
      <el-table :data="filteredOrders" stripe>
        <el-table-column prop="orderNumber" label="订单号" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startPort" label="起始港口" />
        <el-table-column prop="endPort" label="目的港口" />
        <el-table-column prop="cargoType" label="货物类型" />
        <el-table-column prop="weight" label="重量(吨)" />
      </el-table>
    </el-card>

    <el-dialog v-model="showModal" title="新增订单" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="订单号">
          <el-input v-model="form.orderNumber" placeholder="请输入订单号" />
        </el-form-item>
        <el-form-item label="起始港口">
          <el-input v-model="form.startPort" placeholder="请输入起始港口" />
        </el-form-item>
        <el-form-item label="目的港口">
          <el-input v-model="form.endPort" placeholder="请输入目的港口" />
        </el-form-item>
        <el-form-item label="货物类型">
          <el-input v-model="form.cargoType" placeholder="请输入货物类型" />
        </el-form-item>
        <el-form-item label="重量(吨)">
          <el-input v-model="form.weight" placeholder="请输入重量" type="number" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showModal = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyOrders, addOrder } from '@/api/customerOrder'

const showModal = ref(false)
const statusFilter = ref('')
const searchOrderNumber = ref('')

const orders = ref([])

const fetchData = async () => {
  try {
    const res = await getMyOrders()
    orders.value = res.records || res || []
  } catch {
    ElMessage.error('获取订单列表失败')
  }
}

onMounted(() => {
  fetchData()
})

const filteredOrders = computed(() => {
  let result = orders.value
  if (statusFilter.value) {
    result = result.filter(item => item.status === statusFilter.value)
  }
  if (searchOrderNumber.value) {
    result = result.filter(item => item.orderNumber.includes(searchOrderNumber.value))
  }
  return result
})

const getStatusType = (status) => {
  const statusMap = {
    '待处理': 'warning',
    '进行中': 'primary',
    '已完成': 'success'
  }
  return statusMap[status] || 'info'
}

const handleSearch = () => {
  if (searchOrderNumber.value) {
    const found = orders.value.find(item => item.orderNumber.includes(searchOrderNumber.value))
    if (!found) {
      ElMessage.warning('未找到该订单')
    }
  }
}

const form = ref({
  orderNumber: '',
  startPort: '',
  endPort: '',
  cargoType: '',
  weight: null
})

const resetForm = () => {
  form.value.orderNumber = ''
  form.value.startPort = ''
  form.value.endPort = ''
  form.value.cargoType = ''
  form.value.weight = null
}

const openAddModal = () => {
  resetForm()
  showModal.value = true
}

const handleSave = async () => {
  if (!form.value.orderNumber) {
    ElMessage.warning('请输入订单号')
    return
  }
  try {
    await addOrder(form.value)
    ElMessage.success('订单已创建')
    showModal.value = false
    fetchData()
  } catch {
    ElMessage.error('操作失败')
  }
}
</script>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.orders-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>