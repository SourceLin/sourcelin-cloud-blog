import JSEncrypt from 'jsencrypt';

const LOGIN_PUBLIC_KEY =
  'MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKoR8mX0rGKLqzcWmOzbfj64K8ZIgOdH' +
  'nzkXSOVOZbFu/TJhZ7rFAN+eaGkl3C4buccQd/EjEsj9ir7ijT7h96MCAwEAAQ==';

export function encryptLoginPassword(password: string): string {
  const encryptor = new JSEncrypt();
  encryptor.setPublicKey(LOGIN_PUBLIC_KEY);
  const encrypted = encryptor.encrypt(password);
  if (!encrypted) {
    throw new Error('登录密码加密失败');
  }
  return encrypted;
}
