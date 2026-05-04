import request from '@/utils/request'

// 车辆管理接口
export const getVehicles = (params) => {
  return request({
    url: '/vehicles',
    method: 'get',
    params
  })
}

export const getVehicleById = (id) => {
  return request({
    url: `/vehicles/${id}`,
    method: 'get'
  })
}

export const addVehicle = (data) => {
  return request({
    url: '/vehicles',
    method: 'post',
    data
  })
}

export const updateVehicle = (id, data) => {
  return request({
    url: `/vehicles/${id}`,
    method: 'put',
    data
  })
}

export const deleteVehicle = (id) => {
  return request({
    url: `/vehicles/${id}`,
    method: 'delete'
  })
}
