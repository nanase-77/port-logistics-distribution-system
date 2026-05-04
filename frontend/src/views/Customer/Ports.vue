<template>
  <div>
    <el-card class="search-card">
      <el-input 
        v-model="searchPort" 
        placeholder="输入港口名称查询" 
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
          <span>港口列表</span>
        </div>
      </template>
      <el-table :data="filteredPorts" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="港口名称" width="140" />
        <el-table-column prop="longitude" label="经度" width="120" />
        <el-table-column prop="latitude" label="纬度" width="120" />
        <el-table-column prop="country" label="所在国家" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const searchPort = ref('')

const ports = ref([])

const filteredPorts = computed(() => {
  if (!searchPort.value) return ports.value
  return ports.value.filter(item => item.name.includes(searchPort.value))
})

const handleSearch = () => {
  if (searchPort.value) {
    const found = ports.value.find(item => item.name.includes(searchPort.value))
    if (!found) {
      ElMessage.warning('未找到该港口')
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
