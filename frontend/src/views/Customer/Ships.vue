<template>
  <div>
    <el-card class="search-card">
      <el-input
        v-model="searchShipName"
        placeholder="输入船舶名称查询"
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
          <span>船舶列表</span>
        </div>
      </template>
      <el-table :data="filteredShips" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="shipName" label="船舶名称" width="160" />
        <el-table-column prop="companyName" label="所属公司" width="160" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getShips } from '@/api/customerShip'

const searchShipName = ref('')

const ships = ref([])

const fetchData = async () => {
  try {
    const res = await getShips()
    ships.value = res.records || res || []
  } catch {
    ElMessage.error('获取船舶列表失败')
  }
}

onMounted(() => {
  fetchData()
})

const filteredShips = computed(() => {
  if (!searchShipName.value) return ships.value
  return ships.value.filter(item => item.shipName.includes(searchShipName.value))
})

const handleSearch = () => {
  if (searchShipName.value) {
    const found = ships.value.find(item => item.shipName.includes(searchShipName.value))
    if (!found) {
      ElMessage.warning('未找到该船舶')
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