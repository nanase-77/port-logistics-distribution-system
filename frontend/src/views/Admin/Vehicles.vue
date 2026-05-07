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
        <el-table-column prop="portName" label="所在港口" width="140" />
        <el-table-column label="车辆图片" width="100">
          <template #default="{ row }">
            <img v-if="row.imageUrl" :src="row.imageUrl" class="car-image" @click="previewImage(row.imageUrl)" />
            <span v-else class="no-image">无图片</span>
          </template>
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
            <el-option v-for="port in ports" :key="port.id" :label="port.portName" :value="port.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="车辆图片">
          <el-upload
            class="upload-demo"
            action="/api/admin/car/upload"
            :file-list="imageFileList"
            :on-success="handleImageUploadSuccess"
            :before-upload="beforeImageUpload"
            :limit="1"
            accept="image/*"
          >
            <el-button type="primary">点击上传</el-button>
          </el-upload>
          <img v-if="form.imageUrl" :src="form.imageUrl" class="preview-image" />
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

    <el-dialog v-model="showImagePreview" title="图片预览">
      <img :src="previewImageUrl" class="preview-modal-image" />
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
const showImagePreview = ref(false)
const previewImageUrl = ref('')
const imageFileList = ref([])

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
  portId: null,
  status: '闲置',
  imageUrl: ''
})

const resetForm = () => {
  form.id = null
  form.carName = ''
  form.portId = null
  form.status = '闲置'
  form.imageUrl = ''
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
  form.portId = row.portId
  form.status = row.status
  form.imageUrl = row.imageUrl
  imageFileList.value = row.imageUrl ? [{ url: row.imageUrl }] : []
  showModal.value = true
}

const handleImageUploadSuccess = (response) => {
  form.imageUrl = response.data
  imageFileList.value = [{ url: response.data }]
}

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('请上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

const previewImage = (url) => {
  previewImageUrl.value = url
  showImagePreview.value = true
}

const handleSave = async () => {
  if (!form.carName) {
    ElMessage.warning('请输入拖车编号')
    return
  }
  if (!form.portId) {
    ElMessage.warning('请选择所在港口')
    return
  }
  try {
    const data = {
      id: form.id,
      carName: form.carName,
      portId: form.portId,
      status: form.status,
      imageUrl: form.imageUrl
    }
    if (isEdit.value) {
      await updateVehicle(data)
      ElMessage.success('车辆信息已更新')
    } else {
      await addVehicle(data)
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

.car-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  cursor: pointer;
  border-radius: 4px;
}

.no-image {
  color: #999;
  font-size: 12px;
}

.preview-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  margin-top: 10px;
  border-radius: 4px;
}

.preview-modal-image {
  max-width: 100%;
  max-height: 500px;
}
</style>