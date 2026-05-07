import request from '@/utils/request'

export const getCompanies = (params) => {
  return request({
    url: '/api/customer/company/select',
    method: 'get',
    params: { pageNum: 1, pageSize: 100, ...params }
  })
}
