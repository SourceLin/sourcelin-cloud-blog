import test from 'node:test'
import assert from 'node:assert/strict'
import fs from 'node:fs/promises'
import path from 'node:path'

test('platform uses /blog-api baseURL and no interceptor URL remap', async () => {
  const source = await fs.readFile(path.resolve('src/shared/api/http.ts'), 'utf8')

  assert.match(source, /baseURL:\s*import\.meta\.env\.VITE_APP_BASE_API\s*\|\|\s*''/)
  assert.doesNotMatch(source, /withBlogContext/)
})

test('vite proxy rewrites /blog-api/front|app to /blog/front|app only', async () => {
  const source = await fs.readFile(path.resolve('vite.config.ts'), 'utf8')

  assert.match(source, /normalized\.startsWith\('\/front'\)\s*\|\|\s*normalized\.startsWith\('\/app'\)/)
  assert.match(source, /return `\/blog\$\{normalized\}`/)
})

