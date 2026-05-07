<template>
  <div>
    <el-card class="search-card">
      <el-input
        v-model="searchCompany"
        placeholder="输入公司名称查询"
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
          <span>公司列表</span>
        </div>
      </template>
      <el-table :data="filteredCompanies" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="公司名称" width="200" />
        <el-table-column prop="country" label="所在国家" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCompanies } from '@/api/customerCompany'

const searchCompany = ref('')

const companies = ref([])

const fetchData = async () => {
  try {
    const res = await getCompanies()
    companies.value = res.records || res || []
  } catch {
    ElMessage.error('获取公司列表失败')
  }
}

onMounted(() => {
  fetchData()
})

const filteredCompanies = computed(() => {
  if (!searchCompany.value) return companies.value
  return companies.value.filter(item => item.name.includes(searchCompany.value))
})

const handleSearch = () => {
  if (searchCompany.value) {
    const found = companies.value.find(item => item.name.includes(searchCompany.value))
    if (!found) {
      ElMessage.warning('未找到该公司')
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