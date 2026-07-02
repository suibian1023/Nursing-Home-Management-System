/**
 * 使用浏览器原生 Web Crypto API 做 RSA-OAEP (SHA-256) 加密。
 * 后端用 Java 的 Cipher("RSA/ECB/OAEPPadding") + OAEPParameterSpec(SHA-256, MGF1-SHA256) 解密。
 */

/**
 * 把 PEM 格式的 X.509 公钥解析为 CryptoKey
 */
export async function importPublicKey(pem) {
  const b64 = pem
    .replace(/-----BEGIN PUBLIC KEY-----/g, '')
    .replace(/-----END PUBLIC KEY-----/g, '')
    .replace(/\s+/g, '')
  const bin = atob(b64)
  const bytes = new Uint8Array(bin.length)
  for (let i = 0; i < bin.length; i++) bytes[i] = bin.charCodeAt(i)
  return await crypto.subtle.importKey(
    'spki',
    bytes.buffer,
    { name: 'RSA-OAEP', hash: 'SHA-256' },
    false,
    ['encrypt']
  )
}

/**
 * 用公钥加密字符串，返回 Base64 密文
 */
export async function rsaEncrypt(plainText, publicKey) {
  const data = new TextEncoder().encode(plainText)
  const buf = await crypto.subtle.encrypt({ name: 'RSA-OAEP' }, publicKey, data)
  // Uint8Array -> Base64
  const bytes = new Uint8Array(buf)
  let bin = ''
  for (let i = 0; i < bytes.byteLength; i++) bin += String.fromCharCode(bytes[i])
  return btoa(bin)
}
