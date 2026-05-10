<template>
  <div>
    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon port-icon">
            <el-icon><MapLocation /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ graphStats.portCount }}</div>
            <div class="stat-label">港口数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon route-icon">
            <el-icon><Connection /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ graphStats.routeCount }}</div>
            <div class="stat-label">航线数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon ship-icon">
            <el-icon><Ship /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ graphStats.shipCount }}</div>
            <div class="stat-label">可用船舶</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon sync-icon">
            <el-icon><Refresh /></el-icon>
          </div>
          <div class="stat-info">
            <el-button type="primary" size="small" @click="syncPorts">
              同步港口数据
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <!-- 左侧：港口航线拓扑图 -->
      <el-col :span="14">
        <el-card class="section-card">
          <template #header>
            <span>港口航线拓扑图</span>
            <el-button type="info" size="small" @click="fetchGraphData" style="float: right;">
              <el-icon><Refresh /></el-icon>
              刷新数据
            </el-button>
          </template>
          <div class="graph-container">
            <svg :width="graphWidth" :height="graphHeight" class="graph-svg">
              <!-- 连接线 -->
              <g class="edges">
                <line
                  v-for="(edge, idx) in graphEdges"
                  :key="'edge-' + idx"
                  :x1="getNodePosition(edge.fromId).x"
                  :y1="getNodePosition(edge.fromId).y"
                  :x2="getNodePosition(edge.toId).x"
                  :y2="getNodePosition(edge.toId).y"
                  class="edge-line"
                  :class="{ 'edge-selected': isEdgeSelected(edge) }"
                  @click="showRouteDetail(edge)"
                />
                <text
                  v-for="(edge, idx) in graphEdges"
                  :key="'label-' + idx"
                  :x="(getNodePosition(edge.fromId).x + getNodePosition(edge.toId).x) / 2"
                  :y="(getNodePosition(edge.fromId).y + getNodePosition(edge.toId).y) / 2 - 5"
                  class="edge-label"
                >{{ edge.distance }} km</text>
              </g>
              <!-- 港口节点 -->
              <g
                v-for="node in graphNodes"
                :key="node.id"
                class="node-group"
                :transform="`translate(${node.x}, ${node.y})`"
                @click="showPortDetail(node)"
              >
                <circle
                  :r="25"
                  :class="['node-circle node-port', { 'node-selected': selectedPort?.id === node.id }]"
                />
                <text class="node-label" y="35" text-anchor="middle">{{ node.name }}</text>
                <text class="node-country" y="50" text-anchor="middle">{{ node.country }}</text>
              </g>
            </svg>
          </div>
        </el-card>

        <!-- 路径查询结果 -->
        <el-card class="section-card" style="margin-top: 20px;" v-if="pathResult">
          <template #header>
            <span>路径查询结果</span>
          </template>
          <div class="path-detail">
            <div class="path-route">
              <el-icon class="route-arrow"><ArrowRight /></el-icon>
              <span class="route-text">{{ pathResult.fromPort }} → {{ pathResult.toPort }}</span>
            </div>
            <div class="path-metrics">
              <div class="metric-item">
                <span class="metric-label">距离</span>
                <span class="metric-value">{{ pathResult.distance }} km</span>
              </div>
              <div class="metric-item">
                <span class="metric-label">预计时间</span>
                <span class="metric-value">{{ pathResult.estimatedTime }} 小时</span>
              </div>
              <div class="metric-item">
                <span class="metric-label">途经港口</span>
                <span class="metric-value">{{ pathResult.pathLength }} 个</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：路径查询和费用计算 -->
      <el-col :span="10">
        <!-- 路径查询表单 -->
        <el-card class="section-card">
          <template #header>
            <span>路径查询</span>
          </template>
          <el-form :inline="false" :model="pathForm" class="path-form">
            <el-form-item label="起始港口">
              <el-select v-model="pathForm.fromPortId" placeholder="选择起始港口" style="width: 100%;">
                <el-option v-for="p in ports" :key="p.id" :label="p.portName" :value="p.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="目标港口">
              <el-select v-model="pathForm.toPortId" placeholder="选择目标港口" style="width: 100%;">
                <el-option v-for="p in ports" :key="p.id" :label="p.portName" :value="p.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="货物重量(吨)">
              <el-input v-model.number="pathForm.weight" type="number" placeholder="输入重量" style="width: 100%;" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handlePathQuery" style="width: 100%;">查询路径与费用</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 费用计算结果 -->
        <el-card class="section-card" style="margin-top: 16px;" v-if="costResult">
          <template #header>
            <span>费用明细（基于 EagleMap 距离计算）</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="基础运费">¥ {{ costResult.basicFreight.toLocaleString() }}</el-descriptions-item>
            <el-descriptions-item label="燃油附加费">¥ {{ costResult.fuelSurcharge.toLocaleString() }}</el-descriptions-item>
            <el-descriptions-item label="港口服务费">¥ {{ costResult.portFee.toLocaleString() }}</el-descriptions-item>
            <el-descriptions-item label="保险费">¥ {{ costResult.insurance.toLocaleString() }}</el-descriptions-item>
            <el-descriptions-item label="其他费用">¥ {{ costResult.otherFees.toLocaleString() }}</el-descriptions-item>
            <el-descriptions-item label="总计" label-class-name="total-label">
              <span class="total-value">¥ {{ costResult.total.toLocaleString() }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 资源优化建议 -->
        <el-card class="section-card" style="margin-top: 16px;">
          <template #header>
            <span>资源优化建议</span>
            <el-button type="info" size="small" @click="fetchSuggestions" style="float: right;">
              刷新建议
            </el-button>
          </template>
          <div v-if="optimizationTips.length > 0" class="tips-list">
            <div v-for="(tip, idx) in optimizationTips" :key="idx" class="tip-item">
              <el-icon :color="tip.color"><CircleCheck /></el-icon>
              <div class="tip-content">
                <div class="tip-title">{{ tip.title }}</div>
                <div class="tip-desc">{{ tip.description }}</div>
                <div class="tip-benefit">预计收益: {{ tip.expectedBenefit }}%</div>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无优化建议" :image-size="60" />
        </el-card>

        <!-- 异常处理方案 -->
        <el-card class="section-card" style="margin-top: 16px;">
          <template #header>
            <span>异常处理方案</span>
          </template>
          <div class="exception-section">
            <el-select v-model="exceptionType" placeholder="选择异常类型" style="width: 100%; margin-bottom: 12px;">
              <el-option label="设备故障" value="equipment" />
              <el-option label="船舶延误" value="delay" />
              <el-option label="港口拥堵" value="congestion" />
              <el-option label="货物滞留" value="stranded" />
            </el-select>
            <el-button type="warning" @click="handleExceptionQuery" style="width: 100%;">查询替代方案</el-button>
            <div v-if="exceptionPlans.length > 0" class="exception-plans">
              <div v-for="(plan, idx) in exceptionPlans" :key="idx" class="exception-plan-item">
                <div class="plan-header">
                  <span class="plan-name">{{ plan.routeName }}</span>
                  <el-tag :type="'success'" size="small">优先级 {{ plan.priority }}</el-tag>
                </div>
                <div class="plan-info">
                  <span>距离: {{ plan.totalDistance }} km</span>
                  <span>时间: {{ plan.estimatedTime }} 小时</span>
                </div>
                <div class="plan-diff">{{ plan.difference }}</div>
              </div>
            </div>
            <el-empty v-else-if="exceptionType" description="暂无替代方案" :image-size="60" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 港口详情弹窗 -->
    <el-dialog v-if="selectedPort" title="港口详情" :visible.sync="showPortDialog" width="400px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="港口名称">{{ selectedPort.name }}</el-descriptions-item>
        <el-descriptions-item label="所属国家">{{ selectedPort.country }}</el-descriptions-item>
        <el-descriptions-item label="纬度">{{ selectedPort.latitude }}</el-descriptions-item>
        <el-descriptions-item label="经度">{{ selectedPort.longitude }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 航线详情弹窗 -->
    <el-dialog v-if="selectedRoute" title="航线详情" :visible.sync="showRouteDialog" width="400px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="起点港口">{{ selectedRoute.fromName }}</el-descriptions-item>
        <el-descriptions-item label="终点港口">{{ selectedRoute.toName }}</el-descriptions-item>
        <el-descriptions-item label="距离">{{ selectedRoute.distance }} km</el-descriptions-item>
      </el-descriptions>
      <div style="margin-top: 16px;">
        <el-button type="primary" @click="handleRouteCostQuery">计算费用</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { CircleCheck, MapLocation, Ship, Connection, Refresh, ArrowRight } from '@element-plus/icons-vue'
import { schedulingApi } from '@/api/scheduling'
import { getPorts } from '@/api/ports'

// 路径查询表单
const pathForm = reactive({
  fromPortId: '',
  toPortId: '',
  weight: 10
})

const ports = ref([])
const optimizationTips = ref([])
const exceptionType = ref('equipment')
const exceptionPlans = ref([])

// 图数据
const graphNodes = ref([])
const graphEdges = ref([])
const graphStats = ref({
  portCount: 0,
  routeCount: 0,
  shipCount: 0
})
const graphWidth = 650
const graphHeight = 380

// 查询结果
const pathResult = ref(null)
const costResult = ref(null)

// 选中状态
const selectedPort = ref(null)
const showPortDialog = ref(false)
const selectedRoute = ref(null)
const showRouteDialog = ref(false)

const fetchPorts = async () => {
  try {
    const res = await getPorts()
    ports.value = res.records || res || []
  } catch { /* ignore */ }
}

const fetchGraphData = async () => {
  try {
    const res = await schedulingApi.getGraphData()
    // 只保留港口节点
    const nodes = res.nodes || []
    const portNodes = nodes.filter(n => n.type === 'port' || !n.type)
    
    // 布局节点
    graphNodes.value = layoutNodesInGrid(portNodes)
    
    // 转换边数据格式
    const edges = res.edges || []
    graphEdges.value = edges.map(e => ({
      fromId: e.from || e.fromId,
      fromName: e.fromName,
      toId: e.to || e.toId,
      toName: e.toName,
      distance: e.distance || e.label
    }))
    
    graphStats.value = res.stats || { portCount: 0, routeCount: 0, shipCount: 0 }
  } catch (error) {
    console.error('Fetch graph data error:', error)
    // 备用方案：直接从港口数据生成
    await fetchPorts()
    const portNodes = ports.value.map((p, i) => ({
      id: p.id,
      name: p.portName,
      country: p.country,
      latitude: p.latitude,
      longitude: p.longitude,
      x: 80 + (i % 4) * 150,
      y: 80 + Math.floor(i / 4) * 120
    }))
    graphNodes.value = portNodes
    graphStats.value.portCount = portNodes.length
    graphStats.value.routeCount = portNodes.length * (portNodes.length - 1) / 2
  }
}

const layoutNodesInGrid = (nodes) => {
  const cols = 4
  const padding = 80
  const nodeWidth = 150
  
  return nodes.map((node, index) => ({
    ...node,
    x: padding + (index % cols) * nodeWidth,
    y: padding + Math.floor(index / cols) * 120
  }))
}

onMounted(() => {
  fetchPorts()
  fetchSuggestions()
  fetchGraphData()
})

const syncPorts = async () => {
  try {
    await schedulingApi.syncPorts()
    ElMessage.success('港口数据同步成功')
    await fetchGraphData()
  } catch (error) {
    ElMessage.error('同步失败')
    console.error('Sync error:', error)
  }
}

const fetchSuggestions = async () => {
  try {
    const res = await schedulingApi.getSuggestions()
    optimizationTips.value = res.map(item => ({
      title: item.title,
      description: item.description,
      expectedBenefit: item.expectedBenefit,
      color: getSuggestionColor(item.type)
    }))
  } catch (error) {
    optimizationTips.value = [
      { title: '资源状态检查', description: '系统正在获取资源优化建议...', expectedBenefit: 0, color: '#6b7280' }
    ]
  }
}

const getSuggestionColor = (type) => {
  const colorMap = {
    '资源利用率': '#10b981',
    '路径优化': '#3b82f6',
    '负载均衡': '#f59e0b'
  }
  return colorMap[type] || '#6b7280'
}

const getNodePosition = (nodeId) => {
  const node = graphNodes.value.find(n => n.id === nodeId)
  return node ? { x: node.x || 100, y: node.y || 100 } : { x: 100, y: 100 }
}

const isEdgeSelected = (edge) => {
  if (!pathResult.value) return false
  return false
}

const showPortDetail = (node) => {
  selectedPort.value = node
  showPortDialog.value = true
}

const showRouteDetail = (edge) => {
  selectedRoute.value = edge
  showRouteDialog.value = true
}

const handlePathQuery = async () => {
  if (!pathForm.fromPortId || !pathForm.toPortId) {
    ElMessage.warning('请选择起始港口和目标港口')
    return
  }

  try {
    const pathRes = await schedulingApi.findShortestPath(pathForm.fromPortId, pathForm.toPortId)
    pathResult.value = pathRes

    const costRes = await schedulingApi.calculateCost(pathForm.fromPortId, pathForm.toPortId, pathForm.weight)
    costResult.value = costRes

    ElMessage.success('查询成功')
  } catch (error) {
    ElMessage.error('查询失败')
    console.error('Path query error:', error)
  }
}

const handleRouteCostQuery = async () => {
  if (!selectedRoute.value) return

  try {
    const res = await schedulingApi.calculateCost(
      selectedRoute.value.fromId,
      selectedRoute.value.toId,
      pathForm.weight
    )
    costResult.value = res
    showRouteDialog.value = false
    ElMessage.success('费用计算完成')
  } catch (error) {
    ElMessage.error('费用计算失败')
  }
}

const handleExceptionQuery = async () => {
  try {
    const res = await schedulingApi.getAlternatives({
      exceptionType: exceptionType.value
    })
    
    exceptionPlans.value = res.map(item => ({
      routeName: item.routeName,
      totalDistance: item.totalDistance,
      estimatedTime: item.estimatedTime,
      difference: item.difference,
      priority: item.priority
    }))
  } catch (error) {
    ElMessage.error('获取替代方案失败')
    console.error('Exception query error:', error)
  }
}
</script>

<style scoped>
.section-card {
  margin-bottom: 0;
  height: 100%;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.port-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.ship-icon {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: white;
}

.route-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.sync-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
}

.stat-label {
  font-size: 13px;
  color: #6b7280;
}

.path-form {
  padding: 12px 0;
}

.graph-container {
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f8fafc;
  border-radius: 8px;
  padding: 20px;
  min-height: 380px;
}

.graph-svg {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.edge-line {
  stroke: #cbd5e1;
  stroke-width: 2;
  stroke-dasharray: 5, 5;
  cursor: pointer;
  transition: all 0.3s;
}

.edge-line:hover,
.edge-selected {
  stroke: #667eea;
  stroke-width: 3;
}

.edge-label {
  font-size: 11px;
  fill: #64748b;
  background: white;
  padding: 2px 6px;
  border-radius: 4px;
}

.node-group {
  cursor: pointer;
  transition: transform 0.2s;
}

.node-group:hover {
  transform: scale(1.05);
}

.node-circle {
  stroke-width: 3;
}

.node-port {
  fill: #667eea;
  stroke: #5a67d8;
}

.node-selected {
  fill: #f59e0b;
  stroke: #d97706;
}

.node-label {
  font-size: 13px;
  fill: #374151;
  font-weight: 600;
}

.node-country {
  font-size: 11px;
  fill: #6b7280;
}

.path-detail {
  padding: 16px;
}

.path-route {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding: 12px;
  background: #f0f9ff;
  border-radius: 8px;
}

.route-arrow {
  color: #3b82f6;
  font-size: 20px;
}

.route-text {
  font-size: 16px;
  font-weight: 600;
  color: #1e40af;
}

.path-metrics {
  display: flex;
  gap: 24px;
}

.metric-item {
  text-align: center;
}

.metric-label {
  display: block;
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 4px;
}

.metric-value {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.total-label {
  font-weight: 600;
  color: #1f2937;
}

.total-value {
  font-size: 18px;
  font-weight: 700;
  color: #ef4444;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tip-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.tip-content {
  flex: 1;
}

.tip-title {
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 4px;
}

.tip-desc {
  font-size: 13px;
  color: #666;
  line-height: 1.5;
  margin-bottom: 4px;
}

.tip-benefit {
  font-size: 12px;
  color: #10b981;
}

.exception-section {
  padding: 4px 0;
}

.exception-plans {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.exception-plan-item {
  padding: 12px;
  background-color: #fef3c7;
  border-radius: 8px;
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.plan-name {
  font-weight: 600;
}

.plan-info {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
}

.plan-diff {
  font-size: 13px;
  color: #92400e;
}
</style>
