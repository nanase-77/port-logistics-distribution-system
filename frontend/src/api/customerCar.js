import request from '@/utils/request'

export const getVehicles = (params) => {
  return request({
    url: '/customer/car/select',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}
