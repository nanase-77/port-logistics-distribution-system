import request from '@/utils/request'

export const getContainers = (params) => {
  return request({
    url: '/customer/container/select',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}
