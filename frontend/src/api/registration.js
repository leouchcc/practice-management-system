import request from '@/utils/request'

export function registerActivity(activityId) {
  return request({
    url: '/registration/register',
    method: 'post',
    params: { activityId }
  })
}

export function approveRegistration(id) {
  return request({
    url: `/registration/${id}/approve`,
    method: 'post'
  })
}

export function rejectRegistration(id) {
  return request({
    url: `/registration/${id}/reject`,
    method: 'post'
  })
}

export function cancelRegistration(id) {
  return request({
    url: `/registration/${id}/cancel`,
    method: 'post'
  })
}

export function checkIn(id) {
  return request({
    url: `/registration/${id}/check-in`,
    method: 'post'
  })
}

export function checkOut(id) {
  return request({
    url: `/registration/${id}/check-out`,
    method: 'post'
  })
}

export function getRegistrationList(params) {
  return request({
    url: '/registration/list',
    method: 'get',
    params
  })
}
