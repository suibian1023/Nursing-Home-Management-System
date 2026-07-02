// Spring 后端 API 代理层（Admin 自动登录，RSA-OAEP 加密）
const axios = require('axios');
const crypto = require('crypto');

class SpringProxy {
  constructor() {
    this.baseURL = process.env.SPRING_BASE_URL;
    this.adminToken = null;
    this.username = process.env.SPRING_ADMIN_USERNAME || 'admin';
    this.password = process.env.SPRING_ADMIN_PASSWORD || 'admin123';
  }

  async login() {
    // 1) 并行获取公钥和 nonce
    const [pkRes, nonceRes] = await Promise.all([
      axios.get(`${this.baseURL}/user/public-key`),
      axios.post(`${this.baseURL}/user/login-nonce`, null, { params: { username: this.username } }),
    ]);
    const pubKey = crypto.createPublicKey(pkRes.data.data);
    const loginNonce = nonceRes.data.data;

    // 2) RSA-OAEP 加密密码
    const encryptedPassword = crypto.publicEncrypt(
      { key: pubKey, padding: crypto.constants.RSA_PKCS1_OAEP_PADDING, oaepHash: 'sha256' },
      Buffer.from(this.password, 'utf-8')
    ).toString('base64');

    // 3) 提交登录
    const res = await axios.post(`${this.baseURL}/user/login`, {
      username: this.username,
      encryptedPassword,
      loginNonce,
    });
    this.adminToken = res.data.data.token;
    console.log('[Spring] Admin 登录成功，token:', this.adminToken.slice(0, 20) + '...');
  }

  async get(path, params = {}) {
    try {
      const res = await axios.get(`${this.baseURL}${path}`, {
        headers: { token: this.adminToken },
        params,
      });
      return res.data;
    } catch (error) {
      if (error.response?.status === 401) {
        await this.login();
        return this.get(path, params);
      }
      throw error;
    }
  }
}

module.exports = { SpringProxy };
