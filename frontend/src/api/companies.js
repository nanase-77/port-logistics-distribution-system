import request from '@/utils/request'

// 公司管理接口
export const getCompanies = (params) => {
  return request({
    url: '/companies',
    method: 'get',
    params
  })
}

export const getCompanyById = (id) => {
  return request({
    url: `/companies/${id}`,
    method: 'get'
  })
}

export const addCompany = (data) => {
  return request({
    url: '/companies',
    method: 'post',
    data
  })
}

export const updateCompany = (id, data) => {
  return request({
    url: `/companies/${id}`,
    method: 'put',
    data
  })
}

export const deleteCompany = (id) => {
  return request({
    url: `/companies/${id}`,
    method: 'delete'
  })
}
