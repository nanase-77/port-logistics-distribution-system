<template>
  <div>
    <el-card class="search-card">
      <el-input 
        v-model="searchCompanyName" 
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
          <span>公司管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="openAddModal">新增公司</el-button>
          </div>
        </div>
      </template>
      <el-table :data="filteredCompanies" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="公司名称" width="200" />
        <el-table-column prop="country" label="所在国家" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openEditModal(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showModal" :title="isEdit ? '编辑公司' : '新增公司'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="公司名称">
          <el-input v-model="form.name" placeholder="请输入公司名称" />
        </el-form-item>
        <el-form-item label="所在国家">
          <el-input v-model="form.country" placeholder="请输入所在国家" />
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
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const showModal = ref(false)
const isEdit = ref(false)
const searchCompanyName = ref('')

const companies = ref([])

const filteredCompanies = computed(() => {
  if (!searchCompanyName.value) return companies.value
  return companies.value.filter(item => item.name.includes(searchCompanyName.value))
})

const handleSearch = () => {
  if (searchCompanyName.value) {
    const found = companies.value.find(item => item.name.includes(searchCompanyName.value))
    if (!found) {
      ElMessage.warning('未找到该公司')
    }
  }
}

const form = reactive({
  id: null,
  name: '',
  country: ''
})

const resetForm = () => {
  form.id = null
  form.name = ''
  form.country = ''
}

const openAddModal = () => {
  isEdit.value = false
  resetForm()
  showModal.value = true
}

const openEditModal = (row) => {
  isEdit.value = true
  form.id = row.id
  form.name = row.name
  form.country = row.country
  showModal.value = true
}

const handleSave = () => {
  if (!form.name || !form.country) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (isEdit.value) {
    const idx = companies.value.findIndex(c => c.id === form.id)
    if (idx !== -1) {
      companies.value[idx] = {
        ...companies.value[idx],
        name: form.name,
        country: form.country
      }
    }
    ElMessage.success('公司信息已更新')
  } else {
    companies.value.push({
      id: companies.value.length > 0 ? Math.max(...companies.value.map(c => c.id)) + 1 : 1,
      name: form.name,
      country: form.country,
      createTime: new Date().toISOString().replace('T', ' ').substring(0, 19)
    })
    ElMessage.success('公司已创建')
  }
  showModal.value = false
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除公司 "${row.name}" 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    companies.value = companies.value.filter(c => c.id !== row.id)
    ElMessage.success('公司已删除')
  }).catch(() => {})
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

.header-actions {
  display: flex;
  align-items: center;
}
</style>
