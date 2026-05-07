import request from '@/utils/request'

export const getPorts = (params) => {
  return request({
    url: '/api/customer/port/select',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}
