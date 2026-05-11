<template>
  <div>
    <el-card class="report-card">
      <template #header>
        <div class="report-header">
          <span>数据统计报表</span>
          <el-button type="primary" size="small" @click="handleExportReport" :loading="exportLoading">
            <el-icon><Download /></el-icon>
            导出报表
          </el-button>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ reportStats.orderCount }}</div>
            <div class="stat-label">订单总数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ reportStats.containerCount }}</div>
            <div class="stat-label">集装箱数量</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ reportStats.carCount }}</div>
            <div class="stat-label">车辆数量</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value">{{ reportStats.logisticCount }}</div>
            <div class="stat-label">物流跟踪数</div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12">
          <el-card title="月度吞吐量趋势">
            <el-table :data="monthlyData" stripe size="small">
              <el-table-column prop="month" label="月份" />
              <el-table-column prop="throughput" label="吞吐量(TEU)" />
              <el-table-column prop="growth" label="环比增长">
                <template #default="{ row }">
                  <el-tag :type="row.growth >= 0 ? 'success' : 'danger'">
                    {{ row.growth >= 0 ? '+' : '' }}{{ row.growth }}%
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card title="订单完成情况">
            <div class="progress-section">
              <div class="progress-item">
                <span class="progress-label">已完成</span>
                <el-progress :percentage="orderStats.completedRate" color="#10b981" />
                <span class="progress-value">{{ orderStats.completedCount }} 单</span>
              </div>
              <div class="progress-item">
                <span class="progress-label">进行中</span>
                <el-progress :percentage="orderStats.processingRate" color="#3b82f6" />
                <span class="progress-value">{{ orderStats.processingCount }} 单</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12">
          <el-card title="港口周转量">
            <el-table :data="portTurnover" stripe size="small">
              <el-table-column prop="portName" label="港口名称" />
              <el-table-column prop="turnover" label="周转量(TEU)" />
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card title="设备使用状态">
            <el-table :data="equipmentUsage" stripe size="small">
              <el-table-column prop="total" label="设备总数" />
              <el-table-column prop="working" label="运行中" />
              <el-table-column prop="idle" label="空闲" />
              <el-table-column prop="maintenance" label="维护中" />
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Download } from '@element-plus/icons-vue'
import request from '@/utils/request'

const monthlyData = ref([])
const portTurnover = ref([])
const equipmentUsage = ref([])
const exportLoading = ref(false)

const reportStats = reactive({
  orderCount: '0',
  containerCount: '0',
  carCount: '0',
  logisticCount: '0'
})

const orderStats = reactive({
  completedRate: 0,
  completedCount: 0,
  processingRate: 0,
  processingCount: 0
})

const fetchData = async () => {
  try {
    const [monthlyRes, reportRes, orderRes, portRes, equipmentRes] = await Promise.all([
      request({ url: '/reports/monthly-throughput', method: 'get' }),
      request({ url: '/reports', method: 'get' }),
      request({ url: '/reports/order-statistics', method: 'get' }),
      request({ url: '/reports/port-turnover', method: 'get' }),
      request({ url: '/reports/equipment-usage', method: 'get' })
    ])
    
    monthlyData.value = monthlyRes.data || monthlyRes || []
    portTurnover.value = portRes.data || portRes || []
    equipmentUsage.value = equipmentRes.data || equipmentRes || []
    
    const reportData = reportRes.data || reportRes || {}
    reportStats.orderCount = reportData.orderCount || '0'
    reportStats.containerCount = reportData.containerCount || '0'
    reportStats.carCount = reportData.carCount || '0'
    reportStats.logisticCount = reportData.logisticCount || '0'
    
    const orderData = orderRes.data || orderRes || {}
    orderStats.completedRate = orderData.completedRate || 0
    orderStats.completedCount = orderData.completedCount || 0
    orderStats.processingRate = orderData.processingRate || 0
    orderStats.processingCount = orderData.processingCount || 0
  } catch (error) {
    console.error('获取报表数据失败:', error)
  }
}

onMounted(() => {
  fetchData()
})

const handleExportReport = async () => {
  exportLoading.value = true
  try {
    const res = await request({ url: '/reports/export', method: 'get' })
    ElMessage.success(res.msg || res.data || '报表导出成功')
  } catch (error) {
    ElMessage.error('导出失败: ' + (error.message || '未知错误'))
  } finally {
    exportLoading.value = false
  }
}
</script>

<style scoped>
.report-card {
  margin-bottom: 20px;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.progress-section {
  padding: 10px 0;
}

.progress-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.progress-label {
  width: 60px;
  font-size: 14px;
  color: #606266;
}

.progress-value {
  width: 60px;
  font-size: 14px;
  color: #909399;
  text-align: right;
}
</style>
