import request from '@/utils/request'

// 报表统计接口
export const getReportData = (params) => {
  return request({
    url: '/reports',
    method: 'get',
    params
  })
}

export const getMonthlyThroughput = (params) => {
  return request({
    url: '/reports/monthly-throughput',
    method: 'get',
    params
  })
}

export const getOrderStatistics = (params) => {
  return request({
    url: '/reports/order-statistics',
    method: 'get',
    params
  })
}

export const getPortTurnover = (params) => {
  return request({
    url: '/reports/port-turnover',
    method: 'get',
    params
  })
}

export const getEquipmentUsage = (params) => {
  return request({
    url: '/reports/equipment-usage',
    method: 'get',
    params
  })
}
