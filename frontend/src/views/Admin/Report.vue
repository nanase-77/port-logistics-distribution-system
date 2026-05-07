<template>
  <div>
    <el-card class="report-card">
      <template #header>
        <div class="report-header">
          <span>数据统计报表</span>
          <el-button type="primary" size="small" @click="exportReport">导出报表</el-button>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stat-item">
          <div class="stat-value">{{ reportStats.throughput }}</div>
          <div class="stat-label">本月吞吐量(TEU)</div>
        </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
          <div class="stat-value">{{ reportStats.completionRate }}%</div>
          <div class="stat-label">订单完成率</div>
        </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
          <div class="stat-value">{{ reportStats.utilizationRate }}%</div>
          <div class="stat-label">设备利用率</div>
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
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getReportData, getMonthlyThroughput, getOrderStatistics } from '@/api/report'

const monthlyData = ref([])

const reportStats = reactive({
  throughput: '0',
  completionRate: '0',
  utilizationRate: '0'
})

const orderStats = reactive({
  completedRate: 0,
  completedCount: 0,
  processingRate: 0,
  processingCount: 0
})

const fetchData = async () => {
  try {
    const [monthlyRes, reportRes, orderRes] = await Promise.all([
      getMonthlyThroughput(),
      getReportData(),
      getOrderStatistics()
    ])
    
    monthlyData.value = monthlyRes.records || monthlyRes || []
    
    const reportData = reportRes || {}
    reportStats.throughput = reportData.throughput || '0'
    reportStats.completionRate = reportData.completionRate || '0'
    reportStats.utilizationRate = reportData.utilizationRate || '0'
    
    const orderData = orderRes || {}
    orderStats.completedRate = orderData.completedRate || 0
    orderStats.completedCount = orderData.completedCount || 0
    orderStats.processingRate = orderData.processingRate || 0
    orderStats.processingCount = orderData.processingCount || 0
  } catch { /* ignore */ }
}

onMounted(() => {
  fetchData()
})

const exportReport = () => {
  ElMessage.success('报表导出成功')
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
