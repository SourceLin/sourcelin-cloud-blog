import test from 'node:test'
import assert from 'node:assert/strict'
import fs from 'node:fs/promises'
import path from 'node:path'

const root = path.resolve('.')

async function read(relativePath) {
  return fs.readFile(path.join(root, relativePath), 'utf8')
}

test('frontend file gateway prefix is unified to /file', async () => {
  const envSource = await read('.env.development')
  const fileUrlSource = await read('src/shared/composables/useFileUrl.ts')
  const sayApiSource = await read('src/modules/say/api/say.api.ts')
  const articleApiSource = await read('src/modules/article/api/article.api.ts')

  assert.match(envSource, /VITE_FILE_BASE_API=\/file\b/)
  assert.doesNotMatch(envSource, /VITE_FILE_BASE_API=\/file-api\b/)
  assert.match(fileUrlSource, /VITE_FILE_BASE_API\s*\|\|\s*'\/file'/)
  assert.match(sayApiSource, /VITE_FILE_BASE_API\s*\|\|\s*'\/file'/)
  assert.match(articleApiSource, /VITE_FILE_BASE_API\s*\|\|\s*'\/file'/)
})

test('frontend image previews use download endpoint instead of metadata endpoint', async () => {
  const postEditorSource = await read('src/modules/article/components/PostEditorForm.vue')
  const postRichEditorSource = await read('src/modules/article/components/PostRichEditor.vue')
  const fileUrlSource = await read('src/shared/composables/useFileUrl.ts')

  assert.doesNotMatch(postEditorSource, /\$\{FILE_API_BASE\}\/file\/\$\{form\.value\.avatarFileId\}/)
  assert.match(postEditorSource, /resolveCoverUrl|resolveUploadedFileDisplayUrl/)
  assert.doesNotMatch(postRichEditorSource, /\$\{import\.meta\.env\.VITE_FILE_BASE_API\s*\|\|\s*'\/file-api'\}\/file\/\$\{fileId\}/)
  assert.match(postRichEditorSource, /resolveUploadedFileDisplayUrl/)
  assert.match(fileUrlSource, /\/download\/\$\{id\}/)
  assert.doesNotMatch(fileUrlSource, /\/file\/\$\{id\}\/download/)
})

test('vite proxies /file requests through the gateway', async () => {
  const viteConfigSource = await read('vite.config.ts')

  assert.match(viteConfigSource, /'\/file':\s*\{/)
  assert.match(viteConfigSource, /target:\s*'http:\/\/localhost:8080'/)
  assert.doesNotMatch(viteConfigSource, /'\/file-api':\s*\{/)
})
