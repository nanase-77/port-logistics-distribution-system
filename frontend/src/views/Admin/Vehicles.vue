<template>
  <div>
    <el-card class="search-card">
      <el-input 
        v-model="searchCarName" 
        placeholder="输入车辆编号查询" 
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
      <el-table :data="filteredVehicles" stripe style="width: 100%;">
        <el-table-column label="序号" width="80" type="index" :index="(index) => index + 1" />
        <el-table-column label="车辆图片" width="120">
          <template #default="{ row }">
            <el-image 
              v-if="row.imageUrl" 
              :src="row.imageUrl" 
              style="width: 80px; height: 60px;"
              fit="cover"
              :preview-src-list="[row.imageUrl]"
            />
            <span v-else>无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="carName" label="车辆编号" />
        <el-table-column label="所在港口">
          <template #default="{ row }">{{ getPortName(row.portId) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '在用' ? 'success' : 'warning'">{{ row.status }}</el-tag>
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

    <el-dialog v-model="showModal" :title="isEdit ? '编辑车辆' : '新增车辆'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="车辆编号">
          <el-input v-model="form.carName" placeholder="请输入车辆编号" />
        </el-form-item>
        <el-form-item label="车辆图片">
          <el-upload
            class="upload-demo"
            action="/api/file/upload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :file-list="imageFileList"
            accept="image/*"
            :limit="1"
          >
            <el-button size="small" type="primary">点击上传</el-button>
          </el-upload>
          <div v-if="form.imageUrl" style="margin-top: 10px;">
            <el-image :src="form.imageUrl" style="width: 100px; height: 80px;" fit="cover" />
          </div>
        </el-form-item>
        <el-form-item label="所在港口">
          <el-select v-model="form.portName" style="width: 100%;" placeholder="选择">
            <el-option v-for="port in ports" :key="port.id" :label="port.name" :value="port.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%;" placeholder="选择">
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
const imageFileList = ref([])

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
  return port ? port.portName : `港口${portId}`
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

const handleUploadSuccess = (response) => {
  if (response.success) {
    form.imageUrl = response.url
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}

const handleUploadError = () => {
  ElMessage.error('图片上传失败')
}

const form = reactive({
  id: null,
  carName: '',
  imageUrl: '',
  portId: 1,
  status: '闲置'
})

const resetForm = () => {
  form.id = null
  form.carName = ''
  form.imageUrl = ''
  form.portId = 1
  form.status = '闲置'
  imageFileList.value = []
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
  form.imageUrl = row.imageUrl || ''
  form.portId = row.portId
  form.status = row.status
  showModal.value = true
}

const handleSave = async () => {
  if (!form.carName) {
    ElMessage.warning('请输入车辆编号')
    return
  }
  try {
    if (isEdit.value) {
      await updateVehicle({ id: form.id, carName: form.carName, imageUrl: form.imageUrl, portId: form.portId })
      ElMessage.success('车辆信息已更新')
    } else {
      await addVehicle({ carName: form.carName, imageUrl: form.imageUrl, portId: form.portId })
      ElMessage.success('车辆已创建')
    }
    showModal.value = false
    imageFileList.value = []
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
