<template>
  <div class="page-container">
    <div class="search-bar">
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
      <div class="header-actions">
        <el-button type="primary" @click="openAddModal">新增港口</el-button>
        <el-button @click="toggleFullscreen">{{ isFullscreen ? '退出全屏' : '全屏地图' }}</el-button>
      </div>
    </div>

    <div class="main-content" :class="{ 'fullscreen-mode': isFullscreen }">
      <div v-if="!isFullscreen" class="sidebar">
        <el-card class="table-card">
          <template #header>
            <span>港口资源管理</span>
          </template>
          <el-table :data="filteredPorts" stripe style="width: 100%;">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="portName" label="港口名称" width="120" />
            <el-table-column prop="longitude" label="经度" width="110" />
            <el-table-column prop="latitude" label="纬度" width="110" />
            <el-table-column prop="country" label="所在国家" width="120" />
            <el-table-column label="操作" width="140">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="openEditModal(row)">编辑</el-button>
                <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
      
      <div class="map-wrapper">
        <div ref="mapContainer" id="map" class="map-container"></div>
      </div>
    </div>

    <el-dialog v-model="showModal" :title="isEdit ? '编辑港口' : '新增港口'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="港口名称">
          <el-input v-model="form.portName" placeholder="请输入港口名称" />
        </el-form-item>
        <el-form-item label="所在国家">
          <el-input v-model="form.country" placeholder="请输入所在国家" />
        </el-form-item>
        <el-form-item label="经度">
          <el-input-number v-model="form.longitude" :precision="6" :step="0.000001" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="纬度">
          <el-input-number v-model="form.latitude" :precision="6" :step="0.000001" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="地图选点">
          <el-button type="success" @click="openMapPicker">在地图上选点</el-button>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showModal = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showMapPicker" title="在地图上选择港口位置" width="800px" height="600px">
      <div ref="pickerMapContainer" class="picker-map-container"></div>
      <template #footer>
        <el-button @click="showMapPicker = false">取消</el-button>
        <el-button type="primary" @click="confirmMapPick">确认选点</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPorts, addPort, updatePort, deletePort } from '@/api/adminPorts'

const showModal = ref(false)
const showMapPicker = ref(false)
const isEdit = ref(false)
const searchPortName = ref('')
const isFullscreen = ref(false)

const ports = ref([])
const mapContainer = ref(null)
const pickerMapContainer = ref(null)

let map = null
let pickerMap = null
let markers = []
let pickerMarker = null
let selectedPosition = null

const form = reactive({
  id: null,
  portName: '',
  country: '',
  longitude: 116.404,
  latitude: 39.915
})

const fetchData = async () => {
  try {
    const res = await getPorts()
    ports.value = res.records || res || []
    await nextTick()
    initMap()
  } catch {
    ElMessage.error('获取港口列表失败')
  }
}

onMounted(() => {
  loadBMapScript(() => {
    fetchData()
  })
})

watch(ports, () => {
  if (map) {
    addMarkers()
  }
})

watch(isFullscreen, () => {
  if (map) {
    setTimeout(() => {
      map.resize()
    }, 100)
  }
})

const loadBMapScript = (callback) => {
  if (window.BMap) {
    callback()
    return
  }
  window.initBaiduMap = function() {
    console.log('百度地图脚本加载成功')
    console.log('window.BMap:', typeof window.BMap)
    callback()
  }
  const script = document.createElement('script')
  script.src = 'https://api.map.baidu.com/api?v=3.0&ak=90oxFkrJtnC8mH0vlApN6eoD29ObjGzd&callback=initBaiduMap'
  script.type = 'text/javascript'
  script.charset = 'utf-8'
  script.onerror = function(error) {
    console.error('百度地图脚本加载失败:', error)
    ElMessage.error('地图加载失败，请检查网络连接')
  }
  document.head.appendChild(script)
}

const initMap = () => {
  if (!window.BMap) {
    console.error('BMap not loaded')
    return
  }
  
  const container = document.getElementById('map')
  if (!container) {
    console.error('Map container not found')
    return
  }
  
  console.log('Map container found, size:', container.offsetWidth, 'x', container.offsetHeight)
  
  map = new window.BMap.Map('map')
  map.centerAndZoom(new window.BMap.Point(0, 30), 2)
  map.enableScrollWheelZoom(true)
  
  addMarkers()
}

const addMarkers = () => {
  if (!map || !window.BMap) return
  
  markers.forEach(marker => {
    map.removeOverlay(marker)
  })
  markers = []
  
  ports.value.forEach(port => {
    if (port.longitude && port.latitude) {
      const point = new window.BMap.Point(port.longitude, port.latitude)
      const marker = new window.BMap.Marker(point)
      const label = new window.BMap.Label(port.portName, { offset: new window.BMap.Size(20, 0) })
      marker.setLabel(label)
      map.addOverlay(marker)
      markers.push(marker)
      
      marker.addEventListener('click', () => {
        ElMessage.info(`港口: ${port.portName}\n经度: ${port.longitude}\n纬度: ${port.latitude}`)
      })
    }
  })
}

const openMapPicker = () => {
  showMapPicker.value = true
  nextTick(() => {
    initPickerMap()
  })
}

const initPickerMap = () => {
  if (!pickerMapContainer.value || !window.BMap) return
  
  if (pickerMap) {
    pickerMap.clearOverlays()
  }
  
  pickerMap = new window.BMap.Map(pickerMapContainer.value)
  pickerMap.centerAndZoom(new window.BMap.Point(form.longitude || 116.404, form.latitude || 39.915), 3)
  pickerMap.enableScrollWheelZoom(true)
  
  if (form.longitude && form.latitude) {
    const point = new window.BMap.Point(form.longitude, form.latitude)
    pickerMarker = new window.BMap.Marker(point)
    pickerMap.addOverlay(pickerMarker)
    selectedPosition = point
  }
  
  pickerMap.addEventListener('click', (e) => {
    const point = e.point
    if (pickerMarker) {
      pickerMap.removeOverlay(pickerMarker)
    }
    pickerMarker = new window.BMap.Marker(point)
    pickerMap.addOverlay(pickerMarker)
    selectedPosition = point
    ElMessage.info(`已选择: 经度 ${point.lng.toFixed(6)}, 纬度 ${point.lat.toFixed(6)}`)
  })
}

const confirmMapPick = () => {
  if (selectedPosition) {
    form.longitude = selectedPosition.lng
    form.latitude = selectedPosition.lat
    showMapPicker.value = false
    ElMessage.success('位置已选择')
  } else {
    ElMessage.warning('请在地图上点击选择位置')
  }
}

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

const toggleFullscreen = () => {
  isFullscreen.value = !isFullscreen.value
}

const resetForm = () => {
  form.id = null
  form.portName = ''
  form.longitude = 116.404
  form.latitude = 39.915
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
  form.portName = row.portName
  form.longitude = row.longitude
  form.latitude = row.latitude
  form.country = row.country
  showModal.value = true
}

const handleSave = async () => {
  if (!form.portName || !form.country) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    const data = { 
      portName: form.portName, 
      longitude: form.longitude, 
      latitude: form.latitude,
      country: form.country
    }
    if (isEdit.value) {
      data.id = form.id
      await updatePort(data)
      ElMessage.success('港口信息已更新')
    } else {
      await addPort(data)
      ElMessage.success('港口已创建')
    }
    showModal.value = false
    fetchData()
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除港口 "${row.portName}" 吗？`, '确认删除', {
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
.page-container {
  width: 100%;
  height: calc(100vh - 100px);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background: #fff;
  border-bottom: 1px solid #eee;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.sidebar {
  width: 550px;
  overflow-y: auto;
  padding: 10px;
  border-right: 1px solid #eee;
}

.map-wrapper {
  flex: 1;
  position: relative;
}

.map-container {
  width: 100%;
  height: 100%;
}

.picker-map-container {
  width: 100%;
  height: 450px;
}

.fullscreen-mode {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  background: #fff;
}

.fullscreen-mode .sidebar {
  display: none;
}

.fullscreen-mode .search-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1010;
}

.fullscreen-mode .map-wrapper {
  height: 100%;
}

.table-card {
  margin-bottom: 0;
}
</style>
