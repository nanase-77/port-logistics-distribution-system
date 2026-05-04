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
          <el-select v-model="statusFilter" placeholder="筛选状态" style="width: 140px;">
            <el-option label="全部" value="" />
            <el-option label="待处理" value="待处理" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
          </el-select>
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
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewDetail(row)">查看详情</el-button>
            <el-button type="success" size="small" v-if="row.status === '待处理'" @click="handleConfirm(row)">确认订单</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const statusFilter = ref('')
const searchOrderNumber = ref('')

const orders = ref([])

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

const handleViewDetail = (row) => {
  ElMessage.info(`查看订单详情: ${row.orderNumber}`)
}

const handleConfirm = (row) => {
  row.status = '进行中'
  ElMessage.success('订单已确认')
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
