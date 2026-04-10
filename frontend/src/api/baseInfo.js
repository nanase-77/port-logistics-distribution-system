import request from '@/utils/request'

// 码头信息模拟
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

// 船舶、货物类似（此处简化为直接返回mock数据，实际可统一拦截）
// 为避免冗长，在前端视图中直接使用本地模拟数组（便于演示），也可通过此api调用后端
// 课程设计演示采用前端本地数据模拟，但保留接口结构