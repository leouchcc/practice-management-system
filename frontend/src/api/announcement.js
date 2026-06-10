import request from '@/utils/request'

export function getAnnouncementList(params) {
  return request({
    url: '/announcement/list',
    method: 'get',
    params
  })
}

export function getAnnouncementById(id) {
  return request({
    url: `/announcement/${id}`,
    method: 'get'
  })
}

export function createAnnouncement(data) {
  return request({
    url: '/announcement',
    method: 'post',
    data
  })
}

export function updateAnnouncement(data) {
  return request({
    url: '/announcement',
    method: 'put',
    data
  })
}

export function deleteAnnouncement(id) {
  return request({
    url: `/announcement/${id}`,
    method: 'delete'
  })
}
