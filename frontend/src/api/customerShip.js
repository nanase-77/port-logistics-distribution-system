import request from '@/utils/request'

export const getShips = (params) => {
  return request({
    url: '/api/customer/ship/select',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}
