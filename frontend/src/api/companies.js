import request from '@/utils/request'

export const getCompanies = (params) => {
  return request({
    url: '/company/select',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}

export const addCompany = (data) => {
  return request({
    url: '/company/add',
    method: 'post',
    data
  })
}

export const updateCompany = (data) => {
  return request({
    url: '/company/update',
    method: 'put',
    data
  })
}

export const deleteCompany = (id) => {
  return request({
    url: `/company/delete/${id}`,
    method: 'delete'
  })
}
