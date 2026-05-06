import request from '@/utils/request'

export const login = (data) => {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export const register = (data) => {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

export const getUserInfo = () => {
  return request({
    url: '/auth/userInfo',
    method: 'get'
  })
}

export const getUsers = (params) => {
  return request({
    url: '/user/select',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}

export const addUser = (data) => {
  return request({
    url: '/user/add',
    method: 'post',
    data
  })
}

export const updateUser = (data) => {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

export const deleteUser = (id) => {
  return request({
    url: `/user/delete/${id}`,
    method: 'delete'
  })
}
