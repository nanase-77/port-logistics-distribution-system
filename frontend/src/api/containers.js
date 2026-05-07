import request from '@/utils/request'

export const getContainers = (params) => {
  return request({
    url: '/admin/container/select',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}

export const addContainer = (data) => {
  return request({
    url: '/admin/container/add',
    method: 'post',
    data
  })
}

export const updateContainer = (data) => {
  return request({
    url: '/admin/container/update',
    method: 'put',
    data
  })
}

export const deleteContainer = (id) => {
  return request({
    url: `/admin/container/delete/${id}`,
    method: 'delete'
  })
}
