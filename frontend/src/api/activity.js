import request from '@/utils/request'

export function getActivityList(params) {
  return request({
    url: '/activity/list',
    method: 'get',
    params
  })
}

export function getActivityById(id) {
  return request({
    url: `/activity/${id}`,
    method: 'get'
  })
}

export function createActivity(data) {
  return request({
    url: '/activity',
    method: 'post',
    data
  })
}

export function updateActivity(data) {
  return request({
    url: '/activity',
    method: 'put',
    data
  })
}

export function publishActivity(id) {
  return request({
    url: `/activity/${id}/publish`,
    method: 'post'
  })
}

export function cancelActivity(id) {
  return request({
    url: `/activity/${id}/cancel`,
    method: 'post'
  })
}

export function deleteActivity(id) {
  return request({
    url: `/activity/${id}`,
    method: 'delete'
  })
}
