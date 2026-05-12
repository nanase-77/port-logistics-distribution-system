<template>
  <div class="dispatch-container">
    <div class="page-header">
      <h2>智能调度中心</h2>
      <p class="subtitle">基于 Neo4j 图数据库的港口物流路径规划</p>
    </div>

    <el-row :gutter="20">
      <el-col :span="9">
        <el-card class="card-primary">
          <template #header>
            <span class="card-title">路径规划</span>
          </template>

          <el-form :model="pathForm" class="path-form">
            <el-form-item label="起始港口" label-width="80px">
              <el-select
                v-model="pathForm.fromPortId"
                placeholder="选择起始港口"
                style="width: 100%;"
                @change="resetResults"
              >
                <el-option v-for="p in ports" :key="p.id" :label="p.portName" :value="p.id" />
              </el-select>
            </el-form-item>

            <el-form-item label="目的港口" label-width="80px">
              <el-select
                v-model="pathForm.toPortId"
                placeholder="选择目的港口"
                style="width: 100%;"
                @change="resetResults"
              >
                <el-option v-for="p in ports" :key="p.id" :label="p.portName" :value="p.id" />
              </el-select>
            </el-form-item>

            <el-form-item label="货物描述" label-width="80px">
              <el-input
                v-model="pathForm.cargo"
                placeholder="请输入货物名称或描述"
                style="width: 100%;"
              />
            </el-form-item>

            <el-form-item label="货物体积" label-width="80px">
              <el-input
                v-model.number="pathForm.volume"
                type="number"
                placeholder="请输入货物体积 (立方米)"
                style="width: 100%;"
              />
              <span class="volume-hint">提示: 1 TEU ≈ 33.2 立方米 (标准20英尺集装箱)</span>
            </el-form-item>

            <el-form-item label="TEU数量" label-width="80px">
              <el-input
                v-model.number="pathForm.teu"
                type="number"
                placeholder="标准箱数量"
                style="width: 100%;"
                :disabled="autoCalculateTeu"
              />
              <el-switch
                v-model="autoCalculateTeu"
                active-text="自动计算"
                inactive-text="手动输入"
                style="margin-top: 8px;"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handlePathQuery" style="width: 100%;" :loading="isLoading" id="path-query-btn">
                <Search /> 计算最优路径
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="card-secondary" style="margin-top: 16px;">
          <template #header>
            <span class="card-title">系统统计</span>
          </template>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-value">{{ portCount }}</div>
              <div class="stat-label">港口总数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ routeCount }}</div>
              <div class="stat-label">航线数量</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ shipCount }}</div>
              <div class="stat-label">可用船舶</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ containerCount }}</div>
              <div class="stat-label">空集装箱</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="15">
        <el-card class="card-primary" v-if="pathResult">
          <template #header>
            <span class="card-title">最优路径</span>
            <el-button type="info" size="small" @click="handleAllocation" style="float: right;">
              <Box /> 分配资源
            </el-button>
          </template>

          <div class="route-visual">
            <div class="route-path">
              <template v-for="(node, index) in pathResult.nodes" :key="node.id">
                <div class="route-node">
                  <div class="node-dot" :class="{ 'start': index === 0, 'end': index === pathResult.nodes.length - 1 }"></div>
                  <div class="node-info">
                    <div class="node-name">{{ node.name }}</div>
                    <div class="node-country">{{ node.country }}</div>
                  </div>
                </div>
                <ArrowRight v-if="index < pathResult.nodes.length - 1" class="route-arrow" />
              </template>
            </div>

            <div class="route-summary">
              <div class="summary-item">
                <Location class="summary-icon" />
                <div class="summary-content">
                  <div class="summary-label">总距离</div>
                  <div class="summary-value">{{ pathResult.distance }} km</div>
                </div>
              </div>
              <div class="summary-divider"></div>
              <div class="summary-item">
                <Clock class="summary-icon" />
                <div class="summary-content">
                  <div class="summary-label">预计时间</div>
                  <div class="summary-value">{{ pathResult.estimatedTime }} 小时</div>
                </div>
              </div>
              <div class="summary-divider"></div>
              <div class="summary-item">
                <Connection class="summary-icon" />
                <div class="summary-content">
                  <div class="summary-label">途经港口</div>
                  <div class="summary-value">{{ pathResult.pathLength }} 个</div>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="card-primary" v-if="allocationResult" style="margin-top: 16px;">
          <template #header>
            <span class="card-title">资源分配结果</span>
          </template>

          <div class="cargo-info">
            <div class="cargo-item">
              <span class="cargo-label">货物</span>
              <span class="cargo-value">{{ allocationResult.cargo }}</span>
            </div>
            <div class="cargo-item">
              <span class="cargo-label">体积</span>
              <span class="cargo-value">{{ allocationResult.volume }} m³</span>
            </div>
            <div class="cargo-item">
              <span class="cargo-label">TEU</span>
              <span class="cargo-value">{{ allocationResult.teu }}</span>
            </div>
          </div>

          <div class="allocation-grid">
            <div class="allocation-item">
              <div class="allocation-icon ship-icon">
                <Ship />
              </div>
              <div class="allocation-info">
                <div class="allocation-title">运输船舶</div>
                <div class="allocation-value">{{ allocationResult.shipName }}</div>
                <div class="allocation-desc">载重: {{ allocationResult.shipCapacity }} TEU</div>
                <div class="allocation-desc">已承载: {{ allocationResult.shipCurrentTeu }} TEU</div>
              </div>
            </div>

            <div class="allocation-item">
              <div class="allocation-icon container-icon">
                <Box />
              </div>
              <div class="allocation-info">
                <div class="allocation-title">集装箱</div>
                <div class="allocation-value">{{ allocationResult.containerCode }}</div>
                <div class="allocation-desc">容量: {{ allocationResult.containerCapacity }} TEU</div>
              </div>
            </div>

            <div class="allocation-item">
              <div class="allocation-icon car-icon">
                <Van />
              </div>
              <div class="allocation-info">
                <div class="allocation-title">集卡车辆</div>
                <div class="allocation-value">{{ allocationResult.carName }}</div>
                <div class="allocation-desc">所属港口: {{ allocationResult.carPort }}</div>
              </div>
            </div>
          </div>

          <div class="allocation-summary">
            <el-button type="success" @click="generateOrder" style="margin-top: 16px;">
              <Document /> 生成物流单
            </el-button>
          </div>
        </el-card>

        <el-card v-if="!pathResult && !isLoading" class="empty-state">
          <div class="empty-content">
            <Ship class="empty-icon" />
            <p class="empty-title">开始规划您的航线</p>
            <p class="empty-desc">在左侧选择起始港口和目的港口，系统将为您计算最优路径</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Location, Clock, Connection, Ship, Box, Van, ArrowRight, Document } from '@element-plus/icons-vue'
import { schedulingApi } from '@/api/scheduling'
import { getPorts } from '@/api/adminPorts'
import { getShips } from '@/api/adminShips'
import { getContainers } from '@/api/adminContainers'
import { getVehicles } from '@/api/adminVehicles'

const pathForm = reactive({
  fromPortId: '',
  toPortId: '',
  cargo: '',
  volume: null,
  teu: 1
})

const autoCalculateTeu = ref(true)

const ports = ref([])
const ships = ref([])
const containers = ref([])
const cars = ref([])

const isLoading = ref(false)

const pathResult = ref(null)
const allocationResult = ref(null)

const portCount = computed(() => ports.value.length)
const routeCount = computed(() => ports.value.length > 0 ? ports.value.length * (ports.value.length - 1) : 0)
const shipCount = computed(() => {
  if (!pathForm.fromPortId) {
    return 0
  }
  const selectedPortId = Number(pathForm.fromPortId)
  return ships.value.filter(s => s.status === 0 && Number(s.currentPortId) === selectedPortId).length
})
const containerCount = computed(() => containers.value.filter(c => c.status === 0).length)

watch(() => pathForm.volume, (newVolume) => {
  if (autoCalculateTeu.value && newVolume) {
    pathForm.teu = Math.ceil(newVolume / 33.2)
  }
})

watch(autoCalculateTeu, (val) => {
  if (val && pathForm.volume) {
    pathForm.teu = Math.ceil(pathForm.volume / 33.2)
  }
})

const fetchPorts = async () => {
  try {
    const res = await getPorts()
    ports.value = res.records || res || []
  } catch (error) {
    console.error('获取港口失败:', error)
  }
}

const fetchShips = async () => {
  try {
    const res = await getShips()
    ships.value = res.records || res || []
  } catch (error) {
    console.error('获取船舶失败:', error)
  }
}

const fetchContainers = async () => {
  try {
    const res = await getContainers()
    containers.value = res.records || res || []
  } catch (error) {
    console.error('获取集装箱失败:', error)
  }
}

const fetchCars = async () => {
  try {
    const res = await getVehicles()
    cars.value = res.records || res || []
  } catch (error) {
    console.error('获取车辆失败:', error)
  }
}

onMounted(() => {
  fetchPorts()
  fetchShips()
  fetchContainers()
  fetchCars()
})

const resetResults = () => {
  pathResult.value = null
  allocationResult.value = null
}

const handlePathQuery = async () => {
  if (!pathForm.fromPortId || !pathForm.toPortId) {
    ElMessage.warning('请选择起始港口和目的港口')
    return
  }

  if (pathForm.fromPortId === pathForm.toPortId) {
    ElMessage.warning('起始港口和目的港口不能相同')
    return
  }

  if (!pathForm.cargo.trim()) {
    ElMessage.warning('请输入货物描述')
    return
  }

  if (!pathForm.volume || pathForm.volume <= 0) {
    ElMessage.warning('请输入有效的货物体积')
    return
  }

  isLoading.value = true

  try {
    const pathRes = await schedulingApi.findShortestPath(pathForm.fromPortId, pathForm.toPortId)
    pathResult.value = pathRes
    ElMessage.success('路径计算完成')
  } catch (error) {
    ElMessage.error('路径计算失败')
    console.error('Path query error:', error)
  } finally {
    isLoading.value = false
  }
}

const handleAllocation = async () => {
  if (!pathResult.value) return

  isLoading.value = true

  try {
    const request = {
      fromPortId: pathForm.fromPortId,
      toPortId: pathForm.toPortId,
      cargoName: pathForm.cargo,
      volume: pathForm.volume,
      teu: pathForm.teu,
      needCarTransport: true
    }

    const res = await schedulingApi.scheduleCargo(request)

    allocationResult.value = {
      cargoName: pathForm.cargo,
      volume: pathForm.volume,
      teu: pathForm.teu,
      fromPortId: pathForm.fromPortId,
      toPortId: pathForm.toPortId,
      shipId: res.shipId,
      shipName: res.shipName || '未分配',
      shipCapacity: res.shipCapacity || 0,
      shipCurrentTeu: res.shipCurrentTeu || 0,
      containerId: res.containerId,
      containerCode: res.containerCode || '未分配',
      containerCapacity: res.containerCapacity || 0,
      containerContent: res.containerContent || '-',
      carId: res.carId,
      carName: res.carName || '未分配',
      carPort: res.carPort || '-'
    }

    ElMessage.success('资源分配完成')
  } catch (error) {
    ElMessage.error('资源分配失败')
    console.error('Allocation error:', error)
  } finally {
    isLoading.value = false
  }
}

const generateOrder = async () => {
  if (!allocationResult.value) {
    ElMessage.warning('请先进行资源分配')
    return
  }
  
  if (!pathResult.value) {
    ElMessage.warning('请先计算路径')
    return
  }

  try {
    const orderData = {
      ...allocationResult.value,
      ...pathResult.value
    }
    
    console.log('确认订单数据:', orderData)
    
    await schedulingApi.confirmOrder(orderData)
    ElMessage.success('物流单生成成功！资源已分配')
    
    await fetchShips()
    await fetchContainers()
    await fetchCars()
  } catch (error) {
    ElMessage.error('物流单生成失败: ' + (error.message || '未知错误'))
    console.error('Generate order error:', error)
  }
}
</script>

<style scoped>
.dispatch-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.subtitle {
  font-size: 14px;
  color: #6b7280;
  margin-top: 4px;
}

.card-primary {
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border: 1px solid #e5e7eb;
}

.card-secondary {
  background: #ffffff;
  border: 1px solid #e5e7eb;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.path-form {
  padding: 8px 0;
}

.volume-hint {
  display: block;
  font-size: 11px;
  color: #9ca3af;
  margin-top: 4px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  display: block;
  text-align: center;
}

.stat-label {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

.route-visual {
  padding: 16px 0;
}

.route-path {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 8px;
  margin-bottom: 16px;
}

.route-node {
  display: flex;
  align-items: center;
  gap: 10px;
}

.node-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #667eea;
}

.node-dot.start {
  background: #10b981;
}

.node-dot.end {
  background: #ef4444;
}

.node-info {
  display: flex;
  flex-direction: column;
}

.node-name {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.node-country {
  font-size: 12px;
  color: #6b7280;
}

.route-arrow {
  color: #9ca3af;
  font-size: 20px;
  width: 24px;
  height: 24px;
  flex-shrink: 0;
}

.route-summary {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.summary-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #4b5563;
}

.summary-content {
  display: flex;
  flex-direction: column;
}

.summary-label {
  font-size: 12px;
  color: #6b7280;
}

.summary-value {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.summary-divider {
  width: 1px;
  height: 40px;
  background: #e5e7eb;
}

.cargo-info {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
  margin-bottom: 16px;
}

.cargo-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.cargo-label {
  font-size: 12px;
  color: #6b7280;
}

.cargo-value {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.allocation-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.allocation-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
}

.allocation-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.ship-icon {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
}

.container-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.car-icon {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.allocation-info {
  flex: 1;
}

.allocation-title {
  font-size: 12px;
  color: #6b7280;
}

.allocation-value {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-top: 2px;
}

.allocation-desc {
  font-size: 12px;
  color: #6b7280;
  margin-top: 2px;
}

.allocation-summary {
  text-align: center;
  margin-top: 16px;
}

.empty-state {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-content {
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  color: #cbd5e1;
  margin-bottom: 16px;
}

.empty-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.empty-desc {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}
</style>