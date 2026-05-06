declare module 'jsencrypt' {
  export default class JSEncrypt {
    constructor(options?: Record<string, unknown>)
    setPublicKey(publicKey: string): void
    encrypt(text: string): string | false
  }
}

