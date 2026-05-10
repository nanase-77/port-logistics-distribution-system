<template>
  <div>
    <el-card class="search-card">
      <el-input 
        v-model="searchPortName" 
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
          <span>港口资源管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="openAddModal">新增港口</el-button>
          </div>
        </div>
      </template>
      <el-table :data="filteredPorts" stripe style="width: 100%;">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="portName" label="港口名称" width="140" />
        <el-table-column prop="longitude" label="经度" width="120" />
        <el-table-column prop="latitude" label="纬度" width="120" />
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

    <el-dialog v-model="showModal" :title="isEdit ? '编辑港口' : '新增港口'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="港口名称">
          <el-input v-model="form.name" placeholder="请输入港口名称" />
        </el-form-item>
        <el-form-item label="经度">
          <el-input-number v-model="form.longitude" :precision="6" :step="0.01" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="纬度">
          <el-input-number v-model="form.latitude" :precision="6" :step="0.01" style="width: 100%;" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPorts, addPort, updatePort, deletePort } from '@/api/ports'

const showModal = ref(false)
const isEdit = ref(false)
const searchPortName = ref('')

const ports = ref([])

const fetchData = async () => {
  try {
    const res = await getPorts()
    ports.value = res.records || res || []
  } catch {
    ElMessage.error('获取港口列表失败')
  }
}

onMounted(() => {
  fetchData()
})

const filteredPorts = computed(() => {
  if (!searchPortName.value) return ports.value
  return ports.value.filter(item => item.portName?.includes(searchPortName.value))
})

const handleSearch = () => {
  if (searchPortName.value) {
    const found = ports.value.find(item => item.portName?.includes(searchPortName.value))
    if (!found) {
      ElMessage.warning('未找到该港口')
    }
  }
}

const form = reactive({
  id: null,
  name: '',
  longitude: 0,
  latitude: 0,
  country: ''
})

const resetForm = () => {
  form.id = null
  form.name = ''
  form.longitude = 0
  form.latitude = 0
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
  form.name = row.portName
  form.longitude = row.longitude
  form.latitude = row.latitude
  form.country = row.country
  showModal.value = true
}

const handleSave = async () => {
  if (!form.name || !form.country) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    if (isEdit.value) {
      await updatePort({ id: form.id, portName: form.name, longitude: form.longitude, latitude: form.latitude })
      ElMessage.success('港口信息已更新')
    } else {
      await addPort({ portName: form.name, longitude: form.longitude, latitude: form.latitude })
      ElMessage.success('港口已创建')
    }
    showModal.value = false
    fetchData()
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除港口 "${row.name}" 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deletePort(row.id)
      ElMessage.success('港口已删除')
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
