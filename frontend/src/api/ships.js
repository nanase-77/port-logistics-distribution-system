import request from '@/utils/request'

export const getShips = (params) => {
  return request({
    url: '/ship/select',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}

export const addShip = (data) => {
  return request({
    url: '/ship/add',
    method: 'post',
    data
  })
}

export const updateShip = (data) => {
  return request({
    url: '/ship/update',
    method: 'put',
    data
  })
}

export const deleteShip = (id) => {
  return request({
    url: `/ship/delete/${id}`,
    method: 'delete'
  })
}
