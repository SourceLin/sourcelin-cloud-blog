import JSEncrypt from 'jsencrypt'

// 与后端 RsaUtils.privateKey 对应的公钥
const PUBLIC_KEY =
  'MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKoR8mX0rGKLqzcWmOzbfj64K8ZIgOdH' +
  'nzkXSOVOZbFu/TJhZ7rFAN+eaGkl3C4buccQd/EjEsj9ir7ijT7h96MCAwEAAQ=='

export function encryptPassword(rawPassword: string): string {
  if (!rawPassword) {
    return rawPassword
  }
  const encryptor = new JSEncrypt()
  encryptor.setPublicKey(PUBLIC_KEY)
  const encrypted = encryptor.encrypt(rawPassword)
  return encrypted || rawPassword
}

