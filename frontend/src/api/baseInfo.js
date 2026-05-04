import request from '@/utils/request'

export const getBerthList = (params) => {
    return request({ url: '/berths', method: 'get', params })
}

export const addBerth = (data) => {
    return request({ url: '/berths', method: 'post', data })
}

export const updateBerth = (id, data) => {
    return request({ url: `/berths/${id}`, method: 'put', data })
}

export const deleteBerth = (id) => {
    return request({ url: `/berths/${id}`, method: 'delete' })
}