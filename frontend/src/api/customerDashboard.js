import request from '@/utils/request'

export const getDashboardData = () => {
  return request({
    url: '/customer/dashboard/data',
    method: 'get'
  })
}
