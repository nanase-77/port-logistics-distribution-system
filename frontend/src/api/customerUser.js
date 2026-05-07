import request from '@/utils/request'

export const getUserInfo = () => {
  return request({
    url: '/api/customer/user/info',
    method: 'get'
  })
}

export const updateUser = (data) => {
  return request({
    url: '/api/customer/user/update',
    method: 'put',
    data
  })
}
