import request from '@/utils/request'

export const getDispatchResult = (params) => {
    return request({
        url: '/api/admin/dispatch/optimize',
        method: 'get',
        params
    })
}
