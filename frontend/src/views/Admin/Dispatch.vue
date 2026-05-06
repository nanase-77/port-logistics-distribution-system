<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="14">
        <el-card class="section-card">
          <template #header>
            <span>货物调度分配</span>
          </template>
          <el-form :inline="true" :model="dispatchForm" class="dispatch-form">
            <el-form-item label="待调度订单">
              <el-select v-model="dispatchForm.orderNumber" placeholder="选择订单" style="width: 200px;">
                <el-option v-for="o in pendingOrders" :key="o.orderNumber" :label="o.orderNumber" :value="o.orderNumber" />
              </el-select>
            </el-form-item>
            <el-form-item label="调度策略">
              <el-select v-model="dispatchForm.strategy" style="width: 160px;">
                <el-option label="最短路径优先" value="shortest" />
                <el-option label="最低成本优先" value="lowestCost" />
                <el-option label="最快时效优先" value="fastest" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleDispatch">执行调度</el-button>
            </el-form-item>
          </el-form>

          <el-table v-if="dispatchResults.length > 0" :data="dispatchResults" stripe size="small" style="margin-top: 16px;">
            <el-table-column prop="orderNumber" label="订单号" width="150" />
            <el-table-column prop="assignedShip" label="分配船舶" width="120" />
            <el-table-column prop="route" label="规划路线" />
            <el-table-column prop="estimatedTime" label="预计耗时" width="100" />
            <el-table-column prop="cost" label="预估成本" width="100" />
          </el-table>
          <el-empty v-else description="暂无调度结果，请选择订单执行调度" :image-size="80" />
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card class="section-card">
          <template #header>
            <span>资源优化建议</span>
          </template>
          <div v-if="optimizationTips.length > 0" class="tips-list">
            <div v-for="(tip, idx) in optimizationTips" :key="idx" class="tip-item">
              <el-icon :color="tip.color"><CircleCheck /></el-icon>
              <span>{{ tip.content }}</span>
            </div>
          </div>
          <el-empty v-else description="暂无优化建议" :image-size="60" />
        </el-card>

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
            <div v-if="exceptionPlan" class="exception-plan">
              <el-alert :title="exceptionPlan.title" :type="exceptionPlan.type" :description="exceptionPlan.description" show-icon :closable="false" />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { CircleCheck } from '@element-plus/icons-vue'
import { getOrders } from '@/api/orders'

const dispatchForm = reactive({
  orderNumber: '',
  strategy: 'shortest'
})

const pendingOrders = ref([])

const fetchPendingOrders = async () => {
  try {
    const res = await getOrders()
    const allOrders = res.records || res || []
    pendingOrders.value = allOrders.filter(o => o.status === '待处理')
  } catch { /* ignore */ }
}

onMounted(() => {
  fetchPendingOrders()
})
const dispatchResults = ref([])
const optimizationTips = ref([])
const exceptionType = ref('equipment')
const exceptionPlan = ref(null)

const handleDispatch = () => {
  if (!dispatchForm.orderNumber) {
    ElMessage.warning('请选择待调度订单')
    return
  }

  const selectedOrder = pendingOrders.value.find(o => o.orderNumber === dispatchForm.orderNumber)
  if (!selectedOrder) {
    ElMessage.warning('未找到该订单')
    return
  }

  const ships = ['远洋号', '海天号', '长城号', '巨龙号']
  const assignedShip = ships[Math.floor(Math.random() * ships.length)]

  const routeMap = {
    'shortest': `${selectedOrder.startPort} → 直航 → ${selectedOrder.endPort}`,
    'lowestCost': `${selectedOrder.startPort} → 中转港 → ${selectedOrder.endPort}`,
    'fastest': `${selectedOrder.startPort} → 快速航线 → ${selectedOrder.endPort}`
  }

  const timeMap = { 'shortest': '5天', 'lowestCost': '8天', 'fastest': '3天' }
  const costMap = { 'shortest': '¥12,000', 'lowestCost': '¥8,500', 'fastest': '¥18,000' }

  dispatchResults.value.push({
    orderNumber: dispatchForm.orderNumber,
    assignedShip,
    route: routeMap[dispatchForm.strategy],
    estimatedTime: timeMap[dispatchForm.strategy],
    cost: costMap[dispatchForm.strategy]
  })

  optimizationTips.value = [
    { content: `建议将订单 ${dispatchForm.orderNumber} 的货物优先堆放于${selectedOrder.startPort}3号堆场，减少转运距离`, color: '#10b981' },
    { content: `当前${assignedShip}利用率仅65%，可合并小批量货物提升装载率`, color: '#3b82f6' },
    { content: `建议调整${selectedOrder.startPort}龙门吊排班，高峰时段增加1台设备`, color: '#f59e0b' }
  ]

  ElMessage.success('调度方案已生成')
}

const handleExceptionQuery = () => {
  const plans = {
    'equipment': {
      title: '设备故障替代方案',
      type: 'warning',
      description: '建议：1. 启用备用设备（2号龙门吊）；2. 调整作业顺序，优先处理紧急订单；3. 联系维修团队预计2小时内修复。受影响订单可临时转至相邻泊位作业。'
    },
    'delay': {
      title: '船舶延误处理方案',
      type: 'warning',
      description: '建议：1. 通知货主预计延误时间；2. 协调目的港延长卸货窗口期；3. 评估是否启用备用船舶；4. 调整后续订单的发运计划。'
    },
    'congestion': {
      title: '港口拥堵应对方案',
      type: 'warning',
      description: '建议：1. 启用临时堆场扩容20%；2. 分流部分货物至邻近港口；3. 延长作业时间至24小时运营；4. 优先处理中转货物减少积压。'
    },
    'stranded': {
      title: '货物滞留处理方案',
      type: 'warning',
      description: '建议：1. 核查滞留原因（单据/海关/费用）；2. 协调仓储延期或转存；3. 加急处理滞留货物优先出库；4. 通知上下游调整配送计划。'
    }
  }
  exceptionPlan.value = plans[exceptionType.value]
}
</script>

<style scoped>
.section-card {
  margin-bottom: 0;
  height: 100%;
}

.dispatch-form {
  margin-bottom: 0;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tip-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 8px 12px;
  background-color: #f5f7fa;
  border-radius: 6px;
  font-size: 13px;
  line-height: 1.5;
}

.exception-section {
  padding: 4px 0;
}

.exception-plan {
  margin-top: 12px;
}
</style>
