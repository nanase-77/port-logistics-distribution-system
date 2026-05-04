import request from '@/utils/request'

// 物流跟踪接口
export const getLogistics = (params) => {
  return request({
    url: '/logistics',
    method: 'get',
    params
  })
}

export const getLogisticsById = (id) => {
  return request({
    url: `/logistics/${id}`,
    method: 'get'
  })
}

export const addLogistics = (data) => {
  return request({
    url: '/logistics',
    method: 'post',
    data
  })
}

export const updateLogistics = (id, data) => {
  return request({
    url: `/logistics/${id}`,
    method: 'put',
    data
  })
}

export const deleteLogistics = (id) => {
  return request({
    url: `/logistics/${id}`,
    method: 'delete'
  })
}
