import request from '@/utils/request'

export const getLogistics = (params) => {
  return request({
    url: '/customer/logistic/list',
    method: 'get',
    params
  })
}
