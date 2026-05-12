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
      <el-table :data="filteredOrders" stripe style="width: 100%;">
        <el-table-column label="序号" width="80" type="index" :index="(index) => index + 1" />
        <el-table-column prop="orderNumber" label="订单号" />
        <el-table-column prop="containerIds" label="集装箱使用情况" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column prop="status" label="状态" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrders } from '@/api/customerOrders'

const statusFilter = ref('')
const searchOrderNumber = ref('')

const orders = ref([])

const fetchData = async () => {
  let res
  try {
    res = await getOrders()

    orders.value = res || []
  } catch {
    console.log(res)
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
    result = result.filter(item => item.orderNumber?.includes(searchOrderNumber.value))
  }
  return result
})

const handleSearch = () => {
  if (searchOrderNumber.value) {
    const found = orders.value.find(item => item.orderNumber?.includes(searchOrderNumber.value))
    if (!found) {
      ElMessage.warning('未找到该订单')
    }
  }
}
</script>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.orders-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
