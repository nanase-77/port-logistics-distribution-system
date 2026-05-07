import request from '@/utils/request'

export const getLogistics = (params) => {
  return request({
    url: '/api/admin/logistic/select',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}

export const addLogistics = (data) => {
  return request({
    url: '/api/admin/logistic/add',
    method: 'post',
    data
  })
}

export const updateLogistics = (data) => {
  return request({
    url: '/api/admin/logistic/update',
    method: 'put',
    data
  })
}

export const deleteLogistics = (id) => {
  return request({
    url: `/api/admin/logistic/delete/${id}`,
    method: 'delete'
  })
}
