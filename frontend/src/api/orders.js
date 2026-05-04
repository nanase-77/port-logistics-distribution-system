import request from '@/utils/request'

// 订单管理接口
export const getOrders = (params) => {
  return request({
    url: '/orders',
    method: 'get',
    params
  })
}

export const getOrderById = (id) => {
  return request({
    url: `/orders/${id}`,
    method: 'get'
  })
}

export const addOrder = (data) => {
  return request({
    url: '/orders',
    method: 'post',
    data
  })
}

export const updateOrder = (id, data) => {
  return request({
    url: `/orders/${id}`,
    method: 'put',
    data
  })
}

export const deleteOrder = (id) => {
  return request({
    url: `/orders/${id}`,
    method: 'delete'
  })
}
