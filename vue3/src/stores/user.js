import { defineStore } from 'pinia'
import { login as loginApi, getPublicKey, getLoginNonce } from '@/api'
import { importPublicKey, rsaEncrypt } from '@/utils/crypto'

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
      // 1. 并行拉取 RSA 公钥 + 一次性登录令牌
      const [pkRes, nonceRes] = await Promise.all([
        getPublicKey(),
        getLoginNonce(loginForm.username)
      ])
      const pubKey = await importPublicKey(pkRes.data)
      const loginNonce = nonceRes.data

      // 2. 用公钥加密密码
      const encryptedPassword = await rsaEncrypt(loginForm.password, pubKey)

      // 3. 发送密文 + nonce；无 nonce 或 nonce 被篡改/重放都会被后端拒绝
      const res = await loginApi({
        username: loginForm.username,
        encryptedPassword,
        loginNonce
      })
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
