<template>
  <div>
    <el-card class="search-card">
      <el-input 
        v-model="searchCarName" 
        placeholder="输入拖车编号查询" 
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
          <span>车辆列表</span>
          <el-select v-model="statusFilter" placeholder="筛选状态" style="width: 150px;">
            <el-option label="全部" value="" />
            <el-option label="闲置" value="闲置" />
            <el-option label="在用" value="在用" />
          </el-select>
        </div>
      </template>
      <el-table :data="filteredVehicles" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="carName" label="拖车编号" width="140" />
        <el-table-column prop="portName" label="所在港口" width="140" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '闲置' ? 'success' : 'primary'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getVehicles } from '@/api/customerVehicles'

const statusFilter = ref('')
const searchCarName = ref('')

const vehicles = ref([])

const fetchData = async () => {
  try { const res = await getVehicles(); vehicles.value = res.records || res || [] } catch { /* ignore */ }
}

onMounted(() => {
  fetchData()
})

const filteredVehicles = computed(() => {
  let result = vehicles.value
  if (statusFilter.value) {
    result = result.filter(item => item.status === statusFilter.value)
  }
  if (searchCarName.value) {
    result = result.filter(item => item.carName.includes(searchCarName.value))
  }
  return result
})

const handleSearch = () => {
  if (searchCarName.value) {
    const found = vehicles.value.find(item => item.carName.includes(searchCarName.value))
    if (!found) {
      ElMessage.warning('未找到该车辆')
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
