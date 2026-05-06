import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const __filename = fileURLToPath(import.meta.url)
const guardDir = path.dirname(__filename)
const repoRoot = path.resolve(guardDir, '../..')

export function repoPath(relativePath) {
  return path.join(repoRoot, relativePath)
}

export function toRepoRelative(fullPath) {
  return path.relative(repoRoot, fullPath).replace(/\\/g, '/')
}

export function assertFile(relativePath, findings) {
  if (!fs.existsSync(repoPath(relativePath))) {
    findings.push({ rule: 'missing-file', file: relativePath, message: '缺少约定文件' })
  }
}

export function walk(dir, predicate, ignored = new Set(['node_modules', 'dist', 'target', '.git'])) {
  if (!fs.existsSync(dir)) return []
  const entries = fs.readdirSync(dir, { withFileTypes: true })
  const files = []
  for (const entry of entries) {
    if (ignored.has(entry.name)) continue
    const fullPath = path.join(dir, entry.name)
    if (entry.isDirectory()) files.push(...walk(fullPath, predicate, ignored))
    if (entry.isFile() && predicate(entry.name, fullPath)) files.push(fullPath)
  }
  return files
}

export function assertPascalCaseVueFiles(relativeDir, findings) {
  const dir = repoPath(relativeDir)
  for (const file of walk(dir, (name) => name.endsWith('.vue'))) {
    const name = path.basename(file, '.vue')
    if (!/^[A-Z][A-Za-z0-9]*$/.test(name)) {
      findings.push({ rule: 'vue-pascal-case', file: toRepoRelative(file), message: 'Vue 组件文件名必须使用 PascalCase' })
    }
  }
}

export function printResult(name, findings) {
  if (findings.length === 0) {
    console.log(`${name}: OK`)
    return
  }
  console.log(`${name}: FAIL`)
  console.log(JSON.stringify(findings, null, 2))
  process.exitCode = 1
}
