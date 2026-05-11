import request from '@/utils/request'

export const getVehicles = (params) => {
  return request({
    url: '/admin/car/select',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}

export const addVehicle = (data) => {
  return request({
    url: '/admin/car/add',
    method: 'post',
    data
  })
}

export const updateVehicle = (data) => {
  return request({
    url: '/admin/car/update',
    method: 'put',
    data
  })
}

export const deleteVehicle = (id) => {
  return request({
    url: `/admin/car/delete/${id}`,
    method: 'delete'
  })
}
