import JSEncrypt from "jsencrypt";

const ADMIN_LOGIN_PUBLIC_KEY =
  "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKoR8mX0rGKLqzcWmOzbfj64K8ZIgOdH" +
  "nzkXSOVOZbFu/TJhZ7rFAN+eaGkl3C4buccQd/EjEsj9ir7ijT7h96MCAwEAAQ==";

/**
 * 按旧版 Vue2 管理端约定使用 RSA 公钥加密登录密码。
 */
export function encryptLoginPassword(password: string): string {
  const encryptor = new JSEncrypt();
  encryptor.setPublicKey(ADMIN_LOGIN_PUBLIC_KEY);
  const encrypted = encryptor.encrypt(password);
  if (!encrypted) {
    throw new Error("登录密码加密失败");
  }
  return encrypted;
}
