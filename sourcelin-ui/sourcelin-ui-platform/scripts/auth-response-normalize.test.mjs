import assert from 'node:assert/strict'
import test from 'node:test'
import { extractAuthResponseData } from '../src/modules/auth/api/normalize-auth-response.ts'

test('extractAuthResponseData: 统一协议成功响应应提取 data', () => {
  const payload = {
    code: 200,
    message: 'ok',
    data: {
      captchaEnabled: true,
      uuid: 'abc',
      img: 'base64'
    }
  }

  const result = extractAuthResponseData(payload)
  assert.equal(result.uuid, 'abc')
})

test('extractAuthResponseData: 旧版裸对象应原样返回', () => {
  const payload = {
    captchaEnabled: true,
    uuid: 'legacy',
    img: 'legacyBase64'
  }

  const result = extractAuthResponseData(payload)
  assert.equal(result.uuid, 'legacy')
})

test('extractAuthResponseData: 统一协议失败响应应抛出 message', () => {
  assert.throws(() => {
    extractAuthResponseData({
      code: 500,
      message: '验证码错误'
    })
  }, /验证码错误/)
})
