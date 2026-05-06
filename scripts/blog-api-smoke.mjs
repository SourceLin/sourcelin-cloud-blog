/**
 * 博客 / 系统 HTTP 契约冒烟。
 *
 * 必需环境变量：
 * - BLOG_SMOKE_BLOG_USERNAME
 * - BLOG_SMOKE_BLOG_PASSWORD
 * - BLOG_SMOKE_ADMIN_USERNAME
 * - BLOG_SMOKE_ADMIN_PASSWORD
 *
 * 可选环境变量：
 * - BLOG_SMOKE_GATEWAY_BASE_URL，默认 http://localhost:8080
 * - BLOG_SMOKE_AUTH_BASE_URL，默认 ${GATEWAY}/auth
 * - BLOG_SMOKE_BLOG_BASE_URL，默认 ${GATEWAY}/blog
 * - BLOG_SMOKE_SYSTEM_BASE_URL，默认 ${GATEWAY}/system
 * - BLOG_SMOKE_REDIS_HOST / BLOG_SMOKE_REDIS_PORT / BLOG_SMOKE_REDIS_PASSWORD
 */

import { constants, createPublicKey, publicEncrypt } from 'node:crypto'
import net from 'node:net'

const TIMEOUT_MS = Number(process.env.BLOG_SMOKE_TIMEOUT_MS || 15000)
const GATEWAY_BASE_URL = normalizeBase(process.env.BLOG_SMOKE_GATEWAY_BASE_URL || 'http://localhost:8080')
const AUTH_BASE_URL = normalizeBase(process.env.BLOG_SMOKE_AUTH_BASE_URL || `${GATEWAY_BASE_URL}/auth`)
const BLOG_BASE_URL = normalizeBase(process.env.BLOG_SMOKE_BLOG_BASE_URL || `${GATEWAY_BASE_URL}/blog`)
const SYSTEM_BASE_URL = normalizeBase(process.env.BLOG_SMOKE_SYSTEM_BASE_URL || `${GATEWAY_BASE_URL}/system`)

const REDIS_OPTIONS = {
  host: process.env.BLOG_SMOKE_REDIS_HOST || '127.0.0.1',
  port: Number(process.env.BLOG_SMOKE_REDIS_PORT || 6379),
  password: process.env.BLOG_SMOKE_REDIS_PASSWORD || '',
}

const RSA_PUBLIC_KEY_BASE64 =
  process.env.BLOG_SMOKE_RSA_PUBLIC_KEY_BASE64 ||
  'MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKoR8mX0rGKLqzcWmOzbfj64K8ZIgOdHnzkXSOVOZbFu/TJhZ7rFAN+eaGkl3C4buccQd/EjEsj9ir7ijT7h96MCAwEAAQ=='

const LEGACY_KEYS = ['rows', 'records', 'list', 'msg', 'pageNum', 'limit']

const results = []

function normalizeBase(raw) {
  return String(raw || '').replace(/\/$/, '')
}

function requireEnv(name) {
  const value = process.env[name]
  if (!value) {
    throw new Error(`缺少环境变量 ${name}`)
  }
  return value
}

function encryptPassword(rawPassword) {
  const publicKeyObj = createPublicKey({
    key: Buffer.from(RSA_PUBLIC_KEY_BASE64, 'base64'),
    format: 'der',
    type: 'spki',
  })
  return publicEncrypt(
    {
      key: publicKeyObj,
      padding: constants.RSA_PKCS1_PADDING,
    },
    Buffer.from(rawPassword, 'utf8'),
  ).toString('base64')
}

function toQueryString(params) {
  if (!params) return ''
  const query = new URLSearchParams()
  for (const [key, value] of Object.entries(params)) {
    if (value === undefined || value === null || value === '') continue
    query.append(key, String(value))
  }
  const text = query.toString()
  return text ? `?${text}` : ''
}

async function fetchJson({ name, method = 'GET', baseUrl, path, query, body, token }) {
  const url = `${baseUrl}${path}${toQueryString(query)}`
  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(), TIMEOUT_MS)
  try {
    const response = await fetch(url, {
      method,
      signal: controller.signal,
      headers: {
        Accept: 'application/json',
        ...(body === undefined ? {} : { 'Content-Type': 'application/json' }),
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      body: body === undefined ? undefined : JSON.stringify(body),
    })
    const text = await response.text()
    let payload
    try {
      payload = text ? JSON.parse(text) : null
    } catch {
      payload = null
    }
    if (!response.ok) {
      const errorSummary = payload && typeof payload === 'object' ? JSON.stringify(payload) : text.slice(0, 300)
      throw new Error(`HTTP ${response.status} ${errorSummary}`)
    }
    assertApiResponse(payload)
    return payload
  } catch (error) {
    throw new Error(`${name} 失败：${error instanceof Error ? error.message : String(error)}`)
  } finally {
    clearTimeout(timer)
  }
}

function assertApiResponse(payload) {
  if (!payload || typeof payload !== 'object' || Array.isArray(payload)) {
    throw new Error('响应不是 JSON 对象')
  }
  // api-contract-scan: allow-reject-legacy-code
  if (payload.code === 200) {
    throw new Error('响应 code 使用旧成功码 200')
  }
  if (payload.code !== 0) {
    throw new Error(`响应 code 非 0：${payload.code}`)
  }
  if (typeof payload.message !== 'string') {
    throw new Error('响应 message 不是 string')
  }
  if (!Object.prototype.hasOwnProperty.call(payload, 'data')) {
    throw new Error('响应缺少 data 字段')
  }
  assertNoLegacyKeys(payload, 'root')
}

function assertNoLegacyKeys(value, path) {
  if (!value || typeof value !== 'object') return
  if (Array.isArray(value)) {
    value.forEach((item, index) => assertNoLegacyKeys(item, `${path}[${index}]`))
    return
  }
  for (const key of Object.keys(value)) {
    if (LEGACY_KEYS.includes(key)) {
      throw new Error(`出现旧协议字段 ${path}.${key}`)
    }
    assertNoLegacyKeys(value[key], `${path}.${key}`)
  }
}

function assertPageResult(data) {
  if (!data || typeof data !== 'object' || Array.isArray(data)) {
    throw new Error('分页 data 不是对象')
  }
  for (const key of ['items', 'total', 'page', 'pageSize', 'totalPages']) {
    if (!Object.prototype.hasOwnProperty.call(data, key)) {
      throw new Error(`分页结果缺少 ${key}`)
    }
  }
  if (!Array.isArray(data.items)) {
    throw new Error('分页 items 不是数组')
  }
  for (const key of ['total', 'page', 'pageSize', 'totalPages']) {
    if (typeof data[key] !== 'number') {
      throw new Error(`分页 ${key} 不是 number`)
    }
  }
}

function encodeRedisCommand(parts) {
  return `*${parts.length}\r\n${parts.map((part) => `$${Buffer.byteLength(String(part))}\r\n${part}\r\n`).join('')}`
}

function parseFirstBulkString(buffer) {
  const text = buffer.toString('utf8')
  const bulkMatch = text.match(/\$(\d+)\r\n([\s\S]*)\r\n$/)
  if (!bulkMatch) return ''
  const length = Number(bulkMatch[1])
  return bulkMatch[2].slice(0, length)
}

async function redisGet(key) {
  return new Promise((resolve, reject) => {
    const socket = net.createConnection({ host: REDIS_OPTIONS.host, port: REDIS_OPTIONS.port })
    const chunks = []
    const commands = []
    if (REDIS_OPTIONS.password) {
      commands.push(['AUTH', REDIS_OPTIONS.password])
    }
    commands.push(['GET', key])

    const timeout = setTimeout(() => {
      socket.destroy()
      reject(new Error('读取 Redis 验证码超时'))
    }, TIMEOUT_MS)

    socket.on('connect', () => {
      for (const command of commands) {
        socket.write(encodeRedisCommand(command))
      }
    })
    socket.on('data', (chunk) => {
      chunks.push(chunk)
      const text = Buffer.concat(chunks).toString('utf8')
      if (text.includes('\r\n')) {
        clearTimeout(timeout)
        socket.end()
        resolve(parseFirstBulkString(Buffer.concat(chunks)))
      }
    })
    socket.on('error', (error) => {
      clearTimeout(timeout)
      reject(error)
    })
  })
}

async function resolveCaptcha(loginType) {
  const payload = await fetchJson({
    name: `${loginType} 获取验证码`,
    baseUrl: AUTH_BASE_URL,
    path: '/captcha',
    query: { loginType },
  })
  const captcha = payload.data || {}
  if (captcha.captchaEnabled === false) {
    return { captchaCode: '', captchaUuid: '' }
  }
  if (!captcha.uuid) {
    throw new Error(`${loginType} 验证码开启但未返回 uuid`)
  }
  const captchaCode = await redisGet(`captcha:shear:${captcha.uuid}`)
  if (!captchaCode) {
    throw new Error(`${loginType} 未能从 Redis 读取验证码`)
  }
  return { captchaCode, captchaUuid: captcha.uuid }
}

async function login(loginType, username, password) {
  const captcha = await resolveCaptcha(loginType)
  const payload = await fetchJson({
    name: `${loginType} 登录`,
    method: 'POST',
    baseUrl: AUTH_BASE_URL,
    path: '/login',
    body: {
      username,
      password: encryptPassword(password),
      loginType,
      ...captcha,
    },
  })
  const token = payload.data?.accessToken || payload.data?.token
  if (!token) {
    throw new Error(`${loginType} 登录响应未返回 token`)
  }
  return token
}

async function record(name, fn) {
  try {
    await fn()
    results.push({ name, ok: true })
  } catch (error) {
    results.push({ name, ok: false, reason: error instanceof Error ? error.message : String(error) })
  }
}

async function main() {
  const blogUsername = requireEnv('BLOG_SMOKE_BLOG_USERNAME')
  const blogPassword = requireEnv('BLOG_SMOKE_BLOG_PASSWORD')
  const adminUsername = requireEnv('BLOG_SMOKE_ADMIN_USERNAME')
  const adminPassword = requireEnv('BLOG_SMOKE_ADMIN_PASSWORD')

  const blogToken = await login('blog', blogUsername, blogPassword)
  const adminToken = await login('admin', adminUsername, adminPassword)
  console.log(`LOGIN\tblog\tOK\ttokenLength=${blogToken.length}`)
  console.log(`LOGIN\tadmin\tOK\ttokenLength=${adminToken.length}`)

  let firstArticleId

  await record('无登录：首页', async () => {
    await fetchJson({ name: '首页', baseUrl: BLOG_BASE_URL, path: '/front/home' })
  })
  await record('无登录：分类', async () => {
    await fetchJson({ name: '分类', baseUrl: BLOG_BASE_URL, path: '/front/categories' })
  })
  await record('无登录：标签', async () => {
    await fetchJson({ name: '标签', baseUrl: BLOG_BASE_URL, path: '/front/tags' })
  })
  await record('无登录：文章列表分页', async () => {
    const payload = await fetchJson({
      name: '文章列表',
      baseUrl: BLOG_BASE_URL,
      path: '/front/articles',
      query: { page: 1, pageSize: 5 },
    })
    assertPageResult(payload.data)
    firstArticleId = payload.data.items[0]?.id || payload.data.items[0]?.articleId
    if (!firstArticleId) {
      throw new Error('文章列表为空，无法继续登录态互动验证')
    }
  })
  await record('登录后：评论分页', async () => {
    const payload = await fetchJson({
      name: '评论分页',
      baseUrl: BLOG_BASE_URL,
      path: '/front/comments',
      query: { articleId: firstArticleId, source: 'article', page: 1, pageSize: 5 },
      token: blogToken,
    })
    assertPageResult(payload.data)
  })
  await record('登录后：点赞', async () => {
    await fetchJson({
      name: '点赞',
      method: 'PUT',
      baseUrl: BLOG_BASE_URL,
      path: `/front/interactions/likes/article/${firstArticleId}`,
      token: blogToken,
    })
  })
  await record('登录后：收藏', async () => {
    await fetchJson({
      name: '收藏',
      method: 'PUT',
      baseUrl: BLOG_BASE_URL,
      path: `/front/interactions/collects/article/${firstArticleId}`,
      token: blogToken,
    })
  })
  await record('登录后：前台用户信息', async () => {
    await fetchJson({ name: '前台用户信息', baseUrl: BLOG_BASE_URL, path: '/front/user/info', token: blogToken })
  })
  await record('登录后：后台用户分页', async () => {
    const payload = await fetchJson({
      name: '后台用户分页',
      baseUrl: SYSTEM_BASE_URL,
      path: '/user/list',
      query: { page: 1, pageSize: 5 },
      token: adminToken,
    })
    assertPageResult(payload.data)
  })

  const failed = results.filter((item) => !item.ok)
  for (const item of results) {
    console.log(`${item.ok ? 'OK' : 'FAIL'}\t${item.name}${item.ok ? '' : `\t${item.reason}`}`)
  }
  console.log(`SUMMARY\tsuccess=${results.length - failed.length}\tfail=${failed.length}`)

  if (failed.length > 0) {
    process.exitCode = 1
  }
}

main().catch((error) => {
  console.error(error instanceof Error ? error.message : String(error))
  process.exitCode = 1
})
