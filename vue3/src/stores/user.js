import { defineStore } from 'pinia'
import { login as loginApi } from '@/api'

export const useUserStore = defineStore('user', {
  state: () => {
    let userInfo = null
    try {
      const raw = localStorage.getItem('userInfo')
      if (raw && raw !== 'undefined') {
        userInfo = JSON.parse(raw)
      }
    } catch {
      localStorage.removeItem('userInfo')
    }
    return {
      token: localStorage.getItem('token') || '',
      userInfo
    }
  },
  actions: {
    async login(loginForm) {
      const res = await loginApi(loginForm)
      this.token = res.data.token
      this.userInfo = res.data
      localStorage.setItem('token', this.token)
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
      return res
    },
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})
