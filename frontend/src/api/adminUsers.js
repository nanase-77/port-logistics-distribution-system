import request from '@/utils/request'

export const getUsers = (params) => {
  return request({
    url: '/admin/user/select',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}

export const addUser = (data) => {
  return request({
    url: '/admin/user/add',
    method: 'post',
    data
  })
}

export const updateUser = (data) => {
  return request({
    url: '/admin/user/update',
    method: 'put',
    data
  })
}

export const deleteUser = (id) => {
  return request({
    url: `/admin/user/delete/${id}`,
    method: 'delete'
  })
}
