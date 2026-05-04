import request from '@/utils/request'

// 港口管理接口
export const getPorts = (params) => {
  return request({
    url: '/ports',
    method: 'get',
    params
  })
}

export const getPortById = (id) => {
  return request({
    url: `/ports/${id}`,
    method: 'get'
  })
}

export const addPort = (data) => {
  return request({
    url: '/ports',
    method: 'post',
    data
  })
}

export const updatePort = (id, data) => {
  return request({
    url: `/ports/${id}`,
    method: 'put',
    data
  })
}

export const deletePort = (id) => {
  return request({
    url: `/ports/${id}`,
    method: 'delete'
  })
}
