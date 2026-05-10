import request from '@/utils/request'

export const getShips = (params) => {
  return request({
    url: '/admin/ship/select',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}
export const getShipCount = (params) => {
  return request({
    url: '/admin/ship/count',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}

export const addShip = (data) => {
  return request({
    url: '/admin/ship/add',
    method: 'post',
    data
  })
}

export const updateShip = (data) => {
  return request({
    url: '/admin/ship/update',
    method: 'put',
    data
  })
}

export const deleteShip = (id) => {
  return request({
    url: `/admin/ship/delete/${id}`,
    method: 'delete'
  })
}
