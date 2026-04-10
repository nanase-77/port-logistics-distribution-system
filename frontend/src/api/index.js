import request from '@/utils/request'

export const getMockData = (url, data) => {
    // 实际项目可替换为真实接口
    return request({ url, method: 'get', params: data })
}

export const postMockData = (url, data) => {
    return request({ url, method: 'post', data })
}
