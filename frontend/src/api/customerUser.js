import request from '@/utils/request'

export const getUserInfo = () => {
  return request({
    url: '/customer/user/info',
    method: 'get'
  })
}

export const updateUser = (data) => {
  return request({
    url: '/customer/user/update',
    method: 'put',
    data
  })
}
