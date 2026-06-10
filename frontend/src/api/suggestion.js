import request from '@/utils/request'

export function createSuggestion(params) {
  return request({
    url: '/suggestion',
    method: 'post',
    data: params
  })
}

export function replySuggestion(params) {
  return request({
    url: '/suggestion/reply',
    method: 'post',
    data: params
  })
}

export function getSuggestionList(params) {
  return request({
    url: '/suggestion/list',
    method: 'get',
    params
  })
}

export function markAsRead(id) {
  return request({
    url: `/suggestion/${id}/read`,
    method: 'post'
  })
}

export function markAllAsRead() {
  return request({
    url: '/suggestion/read-all',
    method: 'post'
  })
}
