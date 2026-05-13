<template>
  <div>
    <el-card class="search-card">
      <el-input 
        v-model="searchCarName" 
        placeholder="输入车辆编号查询" 
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
        </div>
      </template>
      <el-table :data="filteredVehicles" stripe style="width: 100%;">
        <el-table-column label="序号" width="80" type="index" :index="(index) => index + 1" />
        <el-table-column label="车辆图片" width="120">
          <template #default="{ row }">
            <el-image 
              v-if="row.imageUrl" 
              :src="row.imageUrl" 
              style="width: 80px; height: 60px;"
              fit="cover"
              :preview-src-list="[row.imageUrl]"
            />
            <span v-else>无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="carName" label="车辆编号" />
        <el-table-column prop="portName" label="所在港口" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '在用' ? 'success' : 'warning'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
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
