import request from '@/utils/request'

export const sendMessage = (message) => {
    // 模拟AI咨询回答
    return request({ url: '/ai/chat', method: 'post', data: { message } })
}