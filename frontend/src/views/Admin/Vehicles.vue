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
          <span>车辆管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="openAddModal">新增车辆</el-button>
          </div>
        </div>
      </template>
      <el-table :data="filteredVehicles" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="carName" label="拖车编号" width="140" />
        <el-table-column label="所在港口" width="140">
          <template #default="{ row }">{{ getPortName(row.portId) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '闲置' ? 'success' : 'primary'">{{ row.status }}</el-tag>
          </template>
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

    <el-dialog v-model="showModal" :title="isEdit ? '编辑车辆' : '新增车辆'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="拖车编号">
          <el-input v-model="form.carName" placeholder="请输入拖车编号" />
        </el-form-item>
        <el-form-item label="所在港口">
          <el-select v-model="form.portId" style="width: 100%;">
            <el-option v-for="port in ports" :key="port.id" :label="port.name" :value="port.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%;">
            <el-option label="闲置" value="闲置" />
            <el-option label="在用" value="在用" />
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
import { getVehicles, addVehicle, updateVehicle, deleteVehicle } from '@/api/vehicles'
import { getPorts } from '@/api/ports'

const showModal = ref(false)
const isEdit = ref(false)
const searchCarName = ref('')

const ports = ref([])
const vehicles = ref([])

const fetchPorts = async () => {
  try {
    const res = await getPorts()
    ports.value = res.records || res || []
  } catch { /* ignore */ }
}

const fetchData = async () => {
  try {
    const res = await getVehicles()
    vehicles.value = res.records || res || []
  } catch {
    ElMessage.error('获取车辆列表失败')
  }
}

onMounted(() => {
  fetchPorts()
  fetchData()
})

const getPortName = (portId) => {
  const port = ports.value.find(p => p.id === portId)
  return port ? port.name : `港口${portId}`
}

const filteredVehicles = computed(() => {
  if (!searchCarName.value) return vehicles.value
  return vehicles.value.filter(item => item.carName.includes(searchCarName.value))
})

const handleSearch = () => {
  if (searchCarName.value) {
    const found = vehicles.value.find(item => item.carName.includes(searchCarName.value))
    if (!found) {
      ElMessage.warning('未找到该车辆')
    }
  }
}

const form = reactive({
  id: null,
  carName: '',
  portId: 1,
  status: '闲置'
})

const resetForm = () => {
  form.id = null
  form.carName = ''
  form.portId = 1
  form.status = '闲置'
}

const openAddModal = () => {
  isEdit.value = false
  resetForm()
  showModal.value = true
}

const openEditModal = (row) => {
  isEdit.value = true
  form.id = row.id
  form.carName = row.carName
  form.portId = row.portId
  form.status = row.status
  showModal.value = true
}

const handleSave = async () => {
  if (!form.carName) {
    ElMessage.warning('请输入拖车编号')
    return
  }
  try {
    if (isEdit.value) {
      await updateVehicle({ id: form.id, carName: form.carName, portId: form.portId })
      ElMessage.success('车辆信息已更新')
    } else {
      await addVehicle({ carName: form.carName, portId: form.portId })
      ElMessage.success('车辆已创建')
    }
    showModal.value = false
    fetchData()
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除车辆 "${row.carName}" 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteVehicle(row.id)
      ElMessage.success('车辆已删除')
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
