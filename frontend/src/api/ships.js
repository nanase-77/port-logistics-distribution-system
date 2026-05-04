import request from '@/utils/request'

// 船舶管理接口
export const getShips = (params) => {
  return request({
    url: '/ships',
    method: 'get',
    params
  })
}

export const getShipById = (id) => {
  return request({
    url: `/ships/${id}`,
    method: 'get'
  })
}

export const addShip = (data) => {
  return request({
    url: '/ships',
    method: 'post',
    data
  })
}

export const updateShip = (id, data) => {
  return request({
    url: `/ships/${id}`,
    method: 'put',
    data
  })
}

export const deleteShip = (id) => {
  return request({
    url: `/ships/${id}`,
    method: 'delete'
  })
}
