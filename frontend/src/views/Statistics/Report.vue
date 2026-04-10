<template>
  <el-row :gutter="20">
    <el-col :span="12">
      <el-card>
        <template #header>月度吞吐量</template>
        <div ref="barChart" style="height: 300px"></div>
      </el-card>
    </el-col>
    <el-col :span="12">
      <el-card>
        <template #header>订单完成率</template>
        <div ref="pieChart" style="height: 300px"></div>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

const barChart = ref(null)
const pieChart = ref(null)

onMounted(() => {
  const bar = echarts.init(barChart.value)
  bar.setOption({
    xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: [320, 450, 580, 720, 890, 1020], itemStyle: { color: '#409eff' } }]
  })
  const pie = echarts.init(pieChart.value)
  pie.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: '50%',
      data: [
        { value: 785, name: '已完成' },
        { value: 215, name: '进行中' }
      ],
      label: { show: true }
    }]
  })
})
</script>