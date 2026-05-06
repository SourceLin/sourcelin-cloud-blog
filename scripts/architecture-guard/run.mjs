import { spawnSync } from 'node:child_process'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const __filename = fileURLToPath(import.meta.url)
const guardDir = path.dirname(__filename)
const repoRoot = path.resolve(guardDir, '../..')

const commands = [
  ['node', ['scripts/api-contract-scan.mjs']],
  ['node', ['scripts/architecture-guard/backend-guard.mjs']],
  ['node', ['scripts/architecture-guard/frontend-platform-guard.mjs']],
  ['node', ['scripts/architecture-guard/frontend-admin-guard.mjs']],
  ['node', ['scripts/architecture-guard/rules-system-guard.mjs']],
]

let failed = false
for (const [command, args] of commands) {
  const result = spawnSync(command, args, { cwd: repoRoot, stdio: 'inherit', shell: process.platform === 'win32' })
  if (result.status !== 0) {
    failed = true
  }
}

if (failed) {
  process.exitCode = 1
} else {
  console.log('architecture-guard: OK')
}
