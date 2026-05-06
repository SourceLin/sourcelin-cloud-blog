import test from 'node:test'
import assert from 'node:assert/strict'
import fs from 'node:fs/promises'
import path from 'node:path'

import { authRoutes } from '../src/modules/auth/routes.ts'

test('auth routes use the standalone auth shell', () => {
  const authShellRoutes = authRoutes.map((route) => ({
    path: route.path,
    shell: route.meta?.shell
  }))

  assert.deepEqual(authShellRoutes, [
    { path: '/login', shell: 'auth' },
    { path: '/register', shell: 'auth' }
  ])
})

test('Auth scene uses direct background-image bindings instead of CSS var URL indirection', async () => {
  const source = await fs.readFile(path.resolve('src/modules/auth/components/AuthScene.vue'), 'utf8')

  assert.match(source, /class="auth-scene__photo"[\s\S]*:style="sceneImageStyle"/)
  assert.match(source, /class="auth-scene__visual-photo"[\s\S]*:style="sceneImageStyle"/)
  assert.doesNotMatch(source, /--auth-scene-image/)
})
