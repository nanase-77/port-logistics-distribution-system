import request from '@/utils/request'

export const getCompanies = (params) => {
  return request({
    url: '/admin/company/select',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}

export const addCompany = (data) => {
  return request({
    url: '/admin/company/add',
    method: 'post',
    data
  })
}

export const updateCompany = (data) => {
  return request({
    url: '/admin/company/update',
    method: 'put',
    data
  })
}

export const deleteCompany = (id) => {
  return request({
    url: `/admin/company/delete/${id}`,
    method: 'delete'
  })
}
