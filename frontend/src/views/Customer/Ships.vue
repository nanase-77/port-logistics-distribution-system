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
      <el-table :data="filteredShips" stripe style="width: 100%;">
        <el-table-column label="序号" width="80" type="index" :index="(index) => index + 1" />
        <el-table-column prop="shipName" label="船舶名称" />
        <el-table-column prop="companyName" label="所属公司" />
        <el-table-column prop="capacity" label="容量(TEU)" />
        <el-table-column prop="currentTeu" label="已承载(TEU)" />
        <el-table-column label="当前停靠港口">
          <template #default="{ row }">{{ getPortName(row.currentPortId) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '在用' : '空闲' }}
            </el-tag>
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
import { getShips } from '@/api/customerShips'
import { getPorts } from '@/api/ports'

const searchShipName = ref('')

const ships = ref([])
const ports = ref([])

const fetchData = async () => {
  try { const res = await getShips(); ships.value = res.records || res || [] } catch { /* ignore */ }
}

const fetchPorts = async () => {
  try {
    const res = await getPorts()
    ports.value = res.records || res || []
  } catch { /* ignore */ }
}

onMounted(() => {
  fetchData()
  fetchPorts()
})

const filteredShips = computed(() => {
  if (!searchShipName.value) return ships.value
  return ships.value.filter(item => item.shipName?.includes(searchShipName.value))
})

const getPortName = (portId) => {
  if (!portId) return '未停靠'
  const port = ports.value.find(p => p.id === portId)
  return port ? port.portName : `港口${portId}`
}

const handleSearch = () => {
  if (searchShipName.value) {
    const found = ships.value.find(item => item.shipName?.includes(searchShipName.value))
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