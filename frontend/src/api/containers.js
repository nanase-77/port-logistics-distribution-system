import request from '@/utils/request'

// 集装箱管理接口
export const getContainers = (params) => {
  return request({
    url: '/containers',
    method: 'get',
    params
  })
}

export const getContainerById = (id) => {
  return request({
    url: `/containers/${id}`,
    method: 'get'
  })
}

export const addContainer = (data) => {
  return request({
    url: '/containers',
    method: 'post',
    data
  })
}

export const updateContainer = (id, data) => {
  return request({
    url: `/containers/${id}`,
    method: 'put',
    data
  })
}

export const deleteContainer = (id) => {
  return request({
    url: `/containers/${id}`,
    method: 'delete'
  })
}
