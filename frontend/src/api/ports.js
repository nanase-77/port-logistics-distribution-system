import request from '@/utils/request'

export const getPorts = (params) => {
  return request({
    url: '/port/select',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}

export const addPort = (data) => {
  return request({
    url: '/port/add',
    method: 'post',
    data
  })
}

export const updatePort = (data) => {
  return request({
    url: '/port/update',
    method: 'put',
    data
  })
}

export const deletePort = (id) => {
  return request({
    url: `/port/delete/${id}`,
    method: 'delete'
  })
}
