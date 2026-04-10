import request from '@/utils/request'

export const getDispatchResult = (params) => {
    // 调用智能调度引擎
    return request({ url: '/dispatch/optimize', method: 'get', params })
}
