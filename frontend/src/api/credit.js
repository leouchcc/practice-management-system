import request from '@/utils/request'
import axios from 'axios'

export function getCreditRecordList(params) {
  return request({
    url: '/credit/list',
    method: 'get',
    params
  })
}

export function getTotalCredits(studentId) {
  return request({
    url: `/credit/total/${studentId}`,
    method: 'get'
  })
}

export function exportStudentCreditRecord(studentId) {
  const token = localStorage.getItem('token')
  return axios({
    url: '/api/credit/export/student/' + studentId,
    method: 'get',
    responseType: 'blob',
    headers: {
      'Authorization': token ? 'Bearer ' + token : '',
      'userId': studentId
    }
  })
}

export function exportContactsCreditRecords(teacherId) {
  const token = localStorage.getItem('token')
  return axios({
    url: '/api/credit/export/contacts/' + teacherId,
    method: 'get',
    responseType: 'blob',
    headers: {
      'Authorization': token ? 'Bearer ' + token : '',
      'userId': teacherId
    }
  })
}

export function exportAllCreditRecords() {
  const token = localStorage.getItem('token')
  const userInfo = localStorage.getItem('userInfo')
  let userId = ''
  if (userInfo && userInfo !== 'undefined' && userInfo !== 'null') {
    try {
      const user = JSON.parse(userInfo)
      if (user.id) {
        userId = user.id
      }
    } catch (e) {
      console.error('Failed to parse userInfo:', e)
    }
  }
  return axios({
    url: '/api/credit/export/all',
    method: 'get',
    responseType: 'blob',
    headers: {
      'Authorization': token ? 'Bearer ' + token : '',
      'userId': userId
    }
  })
}
