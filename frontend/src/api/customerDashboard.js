import request from '@/utils/request'

export const getDashboardData = () => {
  return request({
    url: '/api/customer/dashboard/data',
    method: 'get'
  })
}
