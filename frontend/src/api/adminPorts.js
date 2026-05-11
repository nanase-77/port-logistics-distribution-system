import request from '@/utils/request'

export const getPorts = (params) => {
  return request({
    url: '/admin/port/select',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}

export const addPort = (data) => {
  return request({
    url: '/admin/port/add',
    method: 'post',
    data
  })
}

export const updatePort = (data) => {
  return request({
    url: '/admin/port/update',
    method: 'put',
    data
  })
}

export const deletePort = (id) => {
  return request({
    url: `/admin/port/delete/${id}`,
    method: 'delete'
  })
}
