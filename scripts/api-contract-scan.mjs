import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)
const repoRoot = path.resolve(__dirname, '..')

const controllerRoots = [
  'sourcelin-modules/sourcelin-blog/src/main/java/com/sourcelin/blog/controller',
  'sourcelin-modules/sourcelin-system/src/main/java/com/sourcelin/system/controller',
  'sourcelin-auth/src/main/java/com/sourcelin/auth/controller',
]

const frontendRoots = [
  'sourcelin-ui/sourcelin-ui-platform/src',
  'sourcelin-ui/sourcelin-ui-admin/src',
]

const scriptRoots = ['scripts']
const scriptSelf = 'scripts/api-contract-scan.mjs'

const ignoredDirNames = new Set(['node_modules', 'dist', 'target', '.git', '.turbo', '.vite', 'coverage'])

const controllerBlockingRules = [
  { code: 'old-wrapper', pattern: /\b(AjaxResult|TableDataInfo)\b|getDataTable\s*\(|toAjax\s*\(/, message: '旧响应包装结构' },
  { code: 'manual-api-response-return', pattern: /public\s+ApiResponse\s*</, message: 'Controller 直接返回 ApiResponse' },
  { code: 'manual-api-response-factory', pattern: /return\s+ApiResponse\s*\./, message: 'Controller 手动创建 ApiResponse' },
  { code: 'old-page-param', pattern: /@RequestParam\s*\([^\n]*("|')(pageNum|limit)("|')/, message: '旧分页参数' },
  { code: 'map-response', pattern: /public\s+Map\s*<\s*String\s*,\s*Object\s*>/, message: 'Controller 返回 Map<String,Object>' },
]

const frontendBlockingRules = [
  { code: 'legacy-code-200', pattern: /\bcode\s*={0,2}=\s*200\b/, message: '前端判断旧 code=200 协议' },
  { code: 'legacy-page-fallback', pattern: /\?\?\s*(rows|records|list)\b|\|\|\s*(rows|records|list)\b/, message: '前端分页存在旧字段 fallback' },
  { code: 'legacy-response-field', pattern: /\b(data|payload|response|res)\.(rows|records|list|msg|pageNum|limit)\b/, message: '前端读取旧响应字段' },
]

const scriptBlockingRules = [
  { code: 'legacy-code-200', pattern: /\bcode\s*={0,2}=\s*200\b/, message: '脚本判断旧 code=200 协议' },
  { code: 'legacy-page-fallback', pattern: /\?\?\s*(rows|records|list)\b|\|\|\s*(rows|records|list)\b/, message: '脚本分页存在旧字段 fallback' },
]

function walk(dir, predicate) {
  if (!fs.existsSync(dir)) return []
  const entries = fs.readdirSync(dir, { withFileTypes: true })
  const files = []
  for (const entry of entries) {
    if (ignoredDirNames.has(entry.name)) continue
    const fullPath = path.join(dir, entry.name)
    if (entry.isDirectory()) files.push(...walk(fullPath, predicate))
    if (entry.isFile() && predicate(entry.name, fullPath)) files.push(fullPath)
  }
  return files
}

function toRepoPath(fullPath) {
  return path.relative(repoRoot, fullPath).replace(/\\/g, '/')
}

function getLines(content) {
  return content.split(/\r?\n/)
}

function isInternalR(lines, lineIndex, filePath) {
  if (toRepoPath(filePath).includes('/controller/internal/')) return true
  const contextStart = Math.max(0, lineIndex - 6)
  const context = lines.slice(contextStart, lineIndex + 1).join('\n')
  return /@InnerAuth\b|SecurityConstants\.FROM_SOURCE|\/inner\b/.test(context)
}

function collectLineFindings({ files, rules, domain }) {
  const findings = []
  for (const file of files) {
    const lines = getLines(fs.readFileSync(file, 'utf8'))
    lines.forEach((line, index) => {
      const previousLine = index > 0 ? lines[index - 1] : ''
      if (/api-contract-scan:\s*allow-reject-legacy-code/.test(line) || /api-contract-scan:\s*allow-reject-legacy-code/.test(previousLine)) {
        return
      }
      for (const rule of rules) {
        if (rule.pattern.test(line)) {
          findings.push({ domain, rule: rule.code, message: rule.message, file: toRepoPath(file), line: index + 1, text: line.trim() })
        }
      }
    })
  }
  return findings
}

const controllers = controllerRoots.flatMap((root) => walk(path.join(repoRoot, root), (name) => name.endsWith('Controller.java')))
const frontendFiles = frontendRoots.flatMap((root) => walk(path.join(repoRoot, root), (name) => /\.(ts|vue)$/.test(name)))
const scriptFiles = scriptRoots
  .flatMap((root) => walk(path.join(repoRoot, root), (name) => /\.(mjs|js)$/.test(name)))
  .filter((file) => toRepoPath(file) !== scriptSelf)
const findings = []
let publicRouteCount = 0
let internalRCount = 0

for (const file of controllers) {
  const content = fs.readFileSync(file, 'utf8')
  const lines = getLines(content)
  lines.forEach((line, index) => {
    if (/@(GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping|RequestMapping)\b/.test(line)) {
      publicRouteCount += 1
    }
    for (const rule of controllerBlockingRules) {
      if (rule.pattern.test(line)) {
        findings.push({ domain: 'controller', rule: rule.code, message: rule.message, file: toRepoPath(file), line: index + 1, text: line.trim() })
      }
    }
    if (/public\s+R\s*</.test(line)) {
      if (isInternalR(lines, index, file)) {
        internalRCount += 1
      } else {
        findings.push({ domain: 'controller', rule: 'exposed-r', message: '对外 Controller 暴露 R<T>', file: toRepoPath(file), line: index + 1, text: line.trim() })
      }
    }
  })
}

findings.push(...collectLineFindings({ files: frontendFiles, rules: frontendBlockingRules, domain: 'frontend' }))
findings.push(...collectLineFindings({ files: scriptFiles, rules: scriptBlockingRules, domain: 'script' }))

const summary = {
  scannedControllerFiles: controllers.length,
  scannedMappingAnnotations: publicRouteCount,
  allowedInternalRMethods: internalRCount,
  scannedFrontendFiles: frontendFiles.length,
  scannedScriptFiles: scriptFiles.length,
  blockingFindings: findings.length,
}

console.log(JSON.stringify({ summary, findings }, null, 2))

if (findings.length > 0) {
  process.exitCode = 1
}
