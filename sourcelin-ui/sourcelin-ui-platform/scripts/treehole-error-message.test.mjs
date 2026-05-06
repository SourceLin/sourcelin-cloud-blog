import assert from 'node:assert/strict'
import { readFileSync } from 'node:fs'
import path from 'node:path'
import test from 'node:test'
import { resolveRequestErrorMessage } from '../src/shared/api/error-message.ts'

test('resolveRequestErrorMessage: 应优先返回 response.data.message', () => {
  const error = {
    response: {
      data: {
        message: '内容包含敏感词，请修改后重试'
      }
    }
  }

  assert.equal(resolveRequestErrorMessage(error), '内容包含敏感词，请修改后重试')
})

test('resolveRequestErrorMessage: 应兼容错误对象中的 message', () => {
  const error = {
    message: '网络错误'
  }

  assert.equal(resolveRequestErrorMessage(error), '网络错误')
})

test('resolveRequestErrorMessage: 没有可读 message 时应返回默认文案', () => {
  assert.equal(resolveRequestErrorMessage({}), '请求失败')
})

test('http + treehole api: 应支持并使用 skipErrorMessage 做页面级错误提示编排', () => {
  const rootDir = path.resolve(import.meta.dirname, '..')
  const httpSource = readFileSync(path.join(rootDir, 'src/shared/api/http.ts'), 'utf-8')
  const treeholeApiSource = readFileSync(path.join(rootDir, 'src/modules/treehole/api/treehole.api.ts'), 'utf-8')

  assert.match(httpSource, /skipErrorMessage\?: boolean/)
  assert.match(httpSource, /config\?\.skipErrorMessage === true/)
  assert.match(treeholeApiSource, /createTreehole[\s\S]*skipErrorMessage:\s*true/)
})
