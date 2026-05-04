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
            <div class="stat-value">1,258</div>
            <div class="stat-label">本月吞吐量(TEU)</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-value">78.5%</div>
            <div class="stat-label">订单完成率</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-value">92.3%</div>
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
                <el-progress :percentage="78.5" color="#10b981" />
                <span class="progress-value">785 单</span>
              </div>
              <div class="progress-item">
                <span class="progress-label">进行中</span>
                <el-progress :percentage="21.5" color="#3b82f6" />
                <span class="progress-value">215 单</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const monthlyData = ref([])

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
