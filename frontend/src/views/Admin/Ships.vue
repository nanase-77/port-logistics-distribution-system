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
          <span>船舶管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="openAddModal">新增船舶</el-button>
          </div>
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
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openEditModal(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showModal" :title="isEdit ? '编辑船舶' : '新增船舶'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="船舶名称">
          <el-input v-model="form.shipName" placeholder="请输入船舶名称" />
        </el-form-item>
        <el-form-item label="所属公司">
          <el-select v-model="form.companyId" style="width: 100%;">
            <el-option v-for="company in companies" :key="company.id" :label="company.companyName" :value="company.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="容量(TEU)">
          <el-input-number v-model="form.capacity" :min="1" :max="10000" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="当前港口">
          <el-select v-model="form.currentPortId" placeholder="选择当前港口" style="width: 100%;">
            <el-option v-for="port in ports" :key="port.id" :label="port.portName" :value="port.id" />
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
import { getShips, addShip, updateShip, deleteShip } from '@/api/adminShips'
import { getCompanies } from '@/api/adminCompanies'
import { getPorts } from '@/api/ports'

const showModal = ref(false)
const isEdit = ref(false)
const searchShipName = ref('')

const companies = ref([])
const ports = ref([])
const ships = ref([])

const fetchCompanies = async () => {
  try {
    const res = await getCompanies()
    companies.value = res.records || res || []
  } catch { /* ignore */ }
}

const fetchPorts = async () => {
  try {
    const res = await getPorts()
    ports.value = res.records || res || []
  } catch { /* ignore */ }
}

const fetchData = async () => {
  try {
    const res = await getShips()
    ships.value = res.records || res || []
  } catch {
    ElMessage.error('获取船舶列表失败')
  }
}

onMounted(() => {
  fetchCompanies()
  fetchPorts()
  fetchData()
})

const filteredShips = computed(() => {
  if (!searchShipName.value) return ships.value
  return ships.value.filter(item => item.shipName?.includes(searchShipName.value))
})

const handleSearch = () => {
  if (searchShipName.value) {
    const found = ships.value.find(item => item.shipName?.includes(searchShipName.value))
    if (!found) {
      ElMessage.warning('未找到该船舶')
    }
  }
}

const form = reactive({
  id: null,
  shipName: '',
  companyId: 1,
  capacity: 100,
  currentPortId: null
})

const resetForm = () => {
  form.id = null
  form.shipName = ''
  form.companyId = 1
  form.capacity = 100
  form.currentPortId = null
}

const getPortName = (portId) => {
  if (!portId) return '未停靠'
  const port = ports.value.find(p => p.id === portId)
  return port ? port.portName : `港口${portId}`
}

const openAddModal = () => {
  isEdit.value = false
  resetForm()
  showModal.value = true
}

const openEditModal = (row) => {
  isEdit.value = true
  form.id = row.id
  form.shipName = row.shipName
  form.companyId = row.companyId
  form.capacity = row.capacity || 100
  form.currentPortId = row.currentPortId
  showModal.value = true
}

const handleSave = async () => {
  if (!form.shipName) {
    ElMessage.warning('请输入船舶名称')
    return
  }
  try {
    if (isEdit.value) {
      await updateShip({ id: form.id, shipName: form.shipName, companyId: form.companyId, capacity: form.capacity, currentPortId: form.currentPortId })
      ElMessage.success('船舶信息已更新')
    } else {
      await addShip({ shipName: form.shipName, companyId: form.companyId, capacity: form.capacity, currentPortId: form.currentPortId })
      ElMessage.success('船舶已创建')
    }
    showModal.value = false
    fetchData()
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除船舶 "${row.shipName}" 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteShip(row.id)
      ElMessage.success('船舶已删除')
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