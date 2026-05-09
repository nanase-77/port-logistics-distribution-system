import request from '@/utils/request'

export const getMyOrders = (params) => {
  return request({
    url: '/customer/order/select',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}

export const addOrder = (data) => {
  return request({
    url: '/customer/order/add',
    method: 'post',
    data
  })
}
