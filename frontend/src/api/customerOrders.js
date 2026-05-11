import request from '@/utils/request'

export const getOrders = (params) => {
  return request({
    url: '/customer/order/list',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}

export const getOrderStats = () => {
  return request({
    url: '/customer/order/stats',
    method: 'get'
  })
}

export const addOrder = (data) => {
  return request({
    url: '/customer/order/add',
    method: 'post',
    data
  })
}
