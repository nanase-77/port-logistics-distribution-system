import request from '@/utils/request'

export const getOrders = (params) => {
  return request({
    url: '/admin/order/select',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}

export const addOrder = (data) => {
  return request({
    url: '/admin/order/add',
    method: 'post',
    data
  })
}

export const updateOrder = (data) => {
  return request({
    url: '/admin/order/update',
    method: 'put',
    data
  })
}

export const deleteOrder = (id) => {
  return request({
    url: `/admin/order/delete/${id}`,
    method: 'delete'
  })
}
