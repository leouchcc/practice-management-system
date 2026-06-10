import request from '@/utils/request'

export function getContactList() {
  return request({
    url: '/contact/list',
    method: 'get'
  })
}

export function sendContactRequest(data) {
  return request({
    url: '/contact/request',
    method: 'post',
    data
  })
}

export function deleteContact(contactId) {
  return request({
    url: `/contact/${contactId}`,
    method: 'delete'
  })
}
