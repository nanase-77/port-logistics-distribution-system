import request from '@/utils/request'

export const getReportData = (params) => {
  return request({
    url: '/api/admin/report/select',
    method: 'get',
    params
  })
}

export const getMonthlyThroughput = (params) => {
  return request({
    url: '/api/admin/report/monthly-throughput',
    method: 'get',
    params
  })
}

export const getOrderStatistics = (params) => {
  return request({
    url: '/api/admin/report/order-statistics',
    method: 'get',
    params
  })
}

export const getPortTurnover = (params) => {
  return request({
    url: '/api/admin/report/port-turnover',
    method: 'get',
    params
  })
}

export const getEquipmentUsage = (params) => {
  return request({
    url: '/api/admin/report/equipment-usage',
    method: 'get',
    params
  })
}
