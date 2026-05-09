import request from '@/utils/request'

export const getMyLogistics = (params) => {
  return request({
    url: '/customer/logistic/select',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}
