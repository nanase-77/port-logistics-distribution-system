<template>
  <div>
    <el-card class="search-card">
      <el-input 
        v-model="searchContent" 
        placeholder="输入货物描述查询" 
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
          <span>集装箱管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="openAddModal">新增集装箱</el-button>
          </div>
        </div>
      </template>
      <el-table :data="filteredContainers" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="content" label="货物描述" width="200" />
        <el-table-column prop="size" label="尺寸" width="100" />
        <el-table-column label="所属公司" width="160">
          <template #default="{ row }">{{ getCompanyName(row.companyId) }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openEditModal(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showModal" :title="isEdit ? '编辑集装箱' : '新增集装箱'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="货物描述">
          <el-input v-model="form.content" placeholder="请输入货物描述" />
        </el-form-item>
        <el-form-item label="尺寸">
          <el-select v-model="form.size" style="width: 100%;">
            <el-option label="20英尺" value="20英尺" />
            <el-option label="40英尺" value="40英尺" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属公司">
          <el-select v-model="form.companyId" style="width: 100%;">
            <el-option v-for="company in companies" :key="company.id" :label="company.name" :value="company.id" />
          </el-select>
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
const searchContent = ref('')

const companies = ref([])

const containers = ref([])

const getCompanyName = (companyId) => {
  const company = companies.value.find(c => c.id === companyId)
  return company ? company.name : `公司${companyId}`
}

const filteredContainers = computed(() => {
  if (!searchContent.value) return containers.value
  return containers.value.filter(item => item.content.includes(searchContent.value))
})

const handleSearch = () => {
  if (searchContent.value) {
    const found = containers.value.find(item => item.content.includes(searchContent.value))
    if (!found) {
      ElMessage.warning('未找到该集装箱')
    }
  }
}

const form = reactive({
  id: null,
  content: '',
  size: '20英尺',
  companyId: 1
})

const resetForm = () => {
  form.id = null
  form.content = ''
  form.size = '20英尺'
  form.companyId = 1
}

const openAddModal = () => {
  isEdit.value = false
  resetForm()
  showModal.value = true
}

const openEditModal = (row) => {
  isEdit.value = true
  form.id = row.id
  form.content = row.content
  form.size = row.size
  form.companyId = row.companyId
  showModal.value = true
}

const handleSave = () => {
  if (!form.content) {
    ElMessage.warning('请输入货物描述')
    return
  }
  if (isEdit.value) {
    const idx = containers.value.findIndex(c => c.id === form.id)
    if (idx !== -1) {
      containers.value[idx] = {
        ...containers.value[idx],
        content: form.content,
        size: form.size,
        companyId: form.companyId
      }
    }
    ElMessage.success('集装箱信息已更新')
  } else {
    containers.value.push({
      id: containers.value.length > 0 ? Math.max(...containers.value.map(c => c.id)) + 1 : 1,
      content: form.content,
      size: form.size,
      companyId: form.companyId,
      createTime: new Date().toISOString().replace('T', ' ').substring(0, 19)
    })
    ElMessage.success('集装箱已创建')
  }
  showModal.value = false
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除集装箱 "${row.content}" 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    containers.value = containers.value.filter(c => c.id !== row.id)
    ElMessage.success('集装箱已删除')
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
