<template>
  <div class="page-container">
    <div class="search-bar">
      <el-input 
        v-model="searchPort" 
        placeholder="输入港口名称查询" 
        style="width: 300px;"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
      <el-button type="primary" @click="toggleFullscreen" class="fullscreen-btn">
        {{ isFullscreen ? '退出全屏' : '全屏地图' }}
      </el-button>
    </div>

    <div class="main-content" :class="{ 'fullscreen-mode': isFullscreen }">
      <div v-if="!isFullscreen" class="sidebar">
        <el-card class="table-card">
          <template #header>
            <div class="card-header">
              <span>港口列表</span>
            </div>
          </template>
          <el-table :data="filteredPorts" stripe>
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="portName" label="港口名称" width="120" />
            <el-table-column prop="longitude" label="经度" width="110" />
            <el-table-column prop="latitude" label="纬度" width="110" />
            <el-table-column prop="country" label="所在国家" width="120" />
          </el-table>
        </el-card>
      </div>
      
      <div class="map-wrapper">
        <div ref="mapContainer" id="map" class="map-container"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getPorts } from '@/api/customerPorts'

const searchPort = ref('')
const ports = ref([])
const mapContainer = ref(null)
const isFullscreen = ref(false)

let map = null
let markers = []

const fetchData = async () => {
  try { 
    const res = await getPorts(); 
    ports.value = res.records || res || []
    await nextTick()
    initMap()
  } catch (e) { 
    console.error('Failed to fetch ports:', e)
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

const toggleFullscreen = () => {
  isFullscreen.value = !isFullscreen.value
}

const filteredPorts = computed(() => {
  if (!searchPort.value) return ports.value
  return ports.value.filter(item => item.portName?.includes(searchPort.value))
})

const handleSearch = () => {
  if (searchPort.value) {
    const found = ports.value.find(item => item.portName?.includes(searchPort.value))
    if (!found) {
      ElMessage.warning('未找到该港口')
    }
  }
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

.fullscreen-btn {
  margin-left: auto;
}

.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.sidebar {
  width: 400px;
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
