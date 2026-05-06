import test from 'node:test'
import assert from 'node:assert/strict'
import fs from 'node:fs/promises'
import path from 'node:path'

const root = path.resolve('src')
const scopes = [path.join(root, 'modules'), path.join(root, 'shared', 'components')]

async function walk(dir) {
  const entries = await fs.readdir(dir, { withFileTypes: true })
  const files = await Promise.all(
    entries.map(async (entry) => {
      const fullPath = path.join(dir, entry.name)
      if (entry.isDirectory()) {
        if (fullPath.includes(`${path.sep}shared${path.sep}components${path.sep}ui`)) {
          return []
        }
        return walk(fullPath)
      }
      return entry.name.endsWith('.vue') ? [fullPath] : []
    })
  )
  return files.flat()
}

async function getScopedVueFiles() {
  const groups = await Promise.all(scopes.map((dir) => walk(dir)))
  return groups.flat()
}

test('business and page layers do not render raw input or textarea tags', async () => {
  const files = await getScopedVueFiles()
  const violations = []

  for (const file of files) {
    const content = await fs.readFile(file, 'utf8')
    if (/<input\b|<textarea\b/.test(content)) {
      violations.push(path.relative(path.resolve(), file))
    }
  }

  assert.deepEqual(violations, [])
})

test('multiline fields use STextarea instead of SInput type=textarea', async () => {
  const files = await getScopedVueFiles()
  const violations = []

  for (const file of files) {
    const content = await fs.readFile(file, 'utf8')
    if (/type=["']textarea["']/.test(content)) {
      violations.push(path.relative(path.resolve(), file))
    }
  }

  assert.deepEqual(violations, [])
})
