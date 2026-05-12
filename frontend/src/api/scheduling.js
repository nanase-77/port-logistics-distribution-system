import request from '@/utils/request'

export const schedulingApi = {
  calculatePath: (params) => {
    return request({
      url: '/scheduling/path',
      method: 'get',
      params
    })
  },

  scheduleCargo: (data) => {
    return request({
      url: '/scheduling/cargo',
      method: 'post',
      data
    })
  },

  getSuggestions: () => {
    return request({
      url: '/scheduling/suggestions',
      method: 'get'
    })
  },

  getAlternatives: (params) => {
    return request({
      url: '/scheduling/alternatives',
      method: 'get',
      params
    })
  },

  initGraph: () => {
    return request({
      url: '/scheduling/init-graph',
      method: 'post'
    })
  },

  getGraphData: () => {
    return request({
      url: '/scheduling/graph-data',
      method: 'get'
    })
  },

  syncPorts: () => {
    return request({
      url: '/scheduling/sync-ports',
      method: 'post'
    })
  },

  calculateCost: (params) => {
    return request({
      url: '/scheduling/cost',
      method: 'get',
      params
    })
  },

  findShortestPath: (fromPortId, toPortId) => {
    return request({
      url: '/scheduling/shortest-path',
      method: 'get',
      params: { fromPortId, toPortId }
    })
  },

  confirmOrder: (data) => {
    return request({
      url: '/scheduling/confirm-order',
      method: 'post',
      data
    })
  }
}