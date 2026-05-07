<template>
  <div>
    <el-card class="search-card">
      <el-input 
        v-model="searchContainerNumber" 
        placeholder="输入集装箱编号查询" 
        style="width: 300px;"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </el-card>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ containers.length }}</div>
            <div class="stat-label">总集装箱数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ occupiedCount }}</div>
            <div class="stat-label">使用中</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ emptyCount }}</div>
            <div class="stat-label">空闲</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ transitCount }}</div>
            <div class="stat-label">运输中</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="containers-card">
      <template #header>
        <div class="card-header">
          <span>集装箱列表</span>
          <el-select v-model="statusFilter" placeholder="筛选状态" style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="空闲" value="空闲" />
            <el-option label="使用中" value="使用中" />
            <el-option label="运输中" value="运输中" />
          </el-select>
        </div>
      </template>
      <el-table :data="filteredContainers" stripe>
        <el-table-column prop="containerNumber" label="集装箱编号" />
        <el-table-column prop="type" label="类型" />
        <el-table-column prop="size" label="尺寸" />
        <el-table-column prop="weight" label="载重(吨)" />
        <el-table-column prop="currentPort" label="当前位置" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderNumber" label="关联订单" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getContainers } from '@/api/containers'

const statusFilter = ref('')
const searchContainerNumber = ref('')

const containers = ref([])

const fetchData = async () => {
  try { const res = await getContainers(); containers.value = res.records || res || [] } catch { /* ignore */ }
}

onMounted(() => {
  fetchData()
})

const filteredContainers = computed(() => {
  let result = containers.value
  if (statusFilter.value) {
    result = result.filter(item => item.status === statusFilter.value)
  }
  if (searchContainerNumber.value) {
    result = result.filter(item => item.containerNumber.includes(searchContainerNumber.value))
  }
  return result
})

const occupiedCount = computed(() => containers.value.filter(c => c.status === '使用中').length)
const emptyCount = computed(() => containers.value.filter(c => c.status === '空闲').length)
const transitCount = computed(() => containers.value.filter(c => c.status === '运输中').length)

const getStatusType = (status) => {
  const statusMap = {
    '空闲': 'success',
    '使用中': 'primary',
    '运输中': 'warning'
  }
  return statusMap[status] || 'info'
}

const handleSearch = () => {
  if (searchContainerNumber.value) {
    const found = containers.value.find(item => item.containerNumber.includes(searchContainerNumber.value))
    if (!found) {
      ElMessage.warning('未找到该集装箱')
    }
  }
}

const handleViewDetail = (row) => {
  ElMessage.info(`查看集装箱详情: ${row.containerNumber}`)
}
</script>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  padding: 10px;
}

.stat-content {
  padding: 10px 0;
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

.containers-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
