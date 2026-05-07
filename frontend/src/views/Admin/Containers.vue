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
        <el-table-column prop="companyName" label="所属公司" width="160" />
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
            <el-option v-for="company in companies" :key="company.id" :label="company.companyName" :value="company.id" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getContainers, addContainer, updateContainer, deleteContainer } from '@/api/containers'
import { getCompanies } from '@/api/companies'

const showModal = ref(false)
const isEdit = ref(false)
const searchContent = ref('')

const companies = ref([])
const containers = ref([])

const fetchCompanies = async () => {
  try {
    const res = await getCompanies()
    companies.value = res.records || res || []
  } catch { /* ignore */ }
}

const fetchData = async () => {
  try {
    const res = await getContainers()
    containers.value = res.records || res || []
  } catch {
    ElMessage.error('获取集装箱列表失败')
  }
}

onMounted(() => {
  fetchCompanies()
  fetchData()
})

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
  companyId: null
})

const resetForm = () => {
  form.id = null
  form.content = ''
  form.size = '20英尺'
  form.companyId = null
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

const handleSave = async () => {
  if (!form.content) {
    ElMessage.warning('请输入货物描述')
    return
  }
  if (!form.companyId) {
    ElMessage.warning('请选择所属公司')
    return
  }
  try {
    const data = {
      id: form.id,
      content: form.content,
      size: form.size,
      companyId: form.companyId
    }
    if (isEdit.value) {
      await updateContainer(data)
      ElMessage.success('集装箱信息已更新')
    } else {
      await addContainer(data)
      ElMessage.success('集装箱已创建')
    }
    showModal.value = false
    fetchData()
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除集装箱 "${row.content}" 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteContainer(row.id)
      ElMessage.success('集装箱已删除')
      fetchData()
    } catch {
      ElMessage.error('删除失败')
    }
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