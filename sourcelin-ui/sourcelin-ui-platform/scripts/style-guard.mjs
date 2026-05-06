import fs from 'node:fs/promises'
import path from 'node:path'
import process from 'node:process'
import { fileURLToPath } from 'node:url'

const DEFAULT_ALLOWLIST_PATH = 'scripts/style-guard.allowlist.json'
const DEFAULT_SCAN_ROOTS = ['src/app', 'src/modules', 'src/shared', 'src/stores', 'src/assets/styles']
const STYLE_EXTENSIONS = new Set(['.vue', '.scss', '.sass', '.css'])
const SCRIPT_EXTENSIONS = new Set(['.vue', '.ts', '.tsx', '.js', '.jsx', '.mjs', '.cjs'])
const SCAN_EXTENSIONS = new Set([...STYLE_EXTENSIONS, ...SCRIPT_EXTENSIONS])
const UI_COMPONENT_PREFIX = 'src/shared/components/ui/'

const RULES = {
  naiveImport: 'naive-import',
  naiveTag: 'naive-tag',
  naiveDeep: 'naive-deep-selector',
  naiveClass: 'naive-class-override',
  glassMixin: 'glass-mixin',
  hardcodedColor: 'hardcoded-color'
}

const NAIVE_IMPORT_PATTERN = /\bfrom\s+['"]naive-ui['"]|\brequire\(\s*['"]naive-ui['"]\s*\)/g
const NAIVE_TAG_PATTERN = /<\s*(?:n-[a-z0-9-]+|N[A-Z][A-Za-z0-9]*)\b/g
const NAIVE_DEEP_PATTERN = /:deep\(\s*\.n-[a-z0-9-]+/gi
const NAIVE_CLASS_PATTERN = /\.n-[a-z0-9-]+/gi
const GLASS_MIXIN_PATTERN = /@include\s+[\w-]*(?:glass|liquid)[\w-]*\s*\(/g
const HEX_COLOR_PATTERN = /#[0-9a-fA-F]{3,8}\b/g
const RGB_COLOR_PATTERN = /\brgba?\(\s*\d+(?:\.\d+)?\s*,\s*\d+(?:\.\d+)?\s*,\s*\d+(?:\.\d+)?(?:\s*,\s*(?:\d+(?:\.\d+)?|\.\d+))?\s*\)/g
const HSL_COLOR_PATTERN = /\bhsla?\(\s*\d+(?:\.\d+)?(?:deg)?\s*,\s*\d+(?:\.\d+)?%\s*,\s*\d+(?:\.\d+)?%(?:\s*,\s*(?:\d+(?:\.\d+)?|\.\d+))?\s*\)/g
const ROOT_ALLOWED_RULE_PATHS = {
  [RULES.naiveImport]: [
    'src/shared/api/http.ts',
    'src/app/App.vue',
    'src/shared/composables/useSMessage.ts',
    'src/shared/composables/useSNotification.ts'
  ],
  [RULES.naiveTag]: ['src/app/App.vue'],
  [RULES.naiveDeep]: ['src/assets/styles/naive/override.scss'],
  [RULES.naiveClass]: ['src/assets/styles/naive/override.scss'],
  [RULES.glassMixin]: [
    'src/assets/styles/foundation/',
    'src/shared/components/ui/'
  ],
  [RULES.hardcodedColor]: ['src/assets/styles/']
}

function toPosixPath(filePath) {
  return filePath.split(path.sep).join('/')
}

function getLineNumber(source, index) {
  let line = 1
  for (let i = 0; i < index; i += 1) {
    if (source.charCodeAt(i) === 10) {
      line += 1
    }
  }
  return line
}

function extractTagBlocks(source, tagName) {
  const blocks = []
  const regex = new RegExp(`<${tagName}\\b[^>]*>([\\s\\S]*?)<\\/${tagName}>`, 'gi')
  let match

  while ((match = regex.exec(source)) !== null) {
    const fullMatch = match[0]
    const content = match[1] ?? ''
    const startOffset = match.index + fullMatch.indexOf(content)
    blocks.push({
      content,
      startLine: getLineNumber(source, startOffset)
    })
  }

  return blocks
}

function collectPatternViolations(rule, content, pattern, relativeFilePath, startLine) {
  const violations = []
  let match

  while ((match = pattern.exec(content)) !== null) {
    const matchIndex = match.index ?? 0
    const line = startLine + getLineNumber(content, matchIndex) - 1
    violations.push({
      rule,
      file: relativeFilePath,
      line,
      snippet: match[0].trim()
    })
  }

  return violations
}

function deduplicateViolations(violations) {
  const seen = new Set()
  const deduplicated = []

  for (const violation of violations) {
    const key = `${violation.rule}|${violation.file}|${violation.line}|${violation.snippet}`
    if (!seen.has(key)) {
      seen.add(key)
      deduplicated.push(violation)
    }
  }

  return deduplicated
}

function shouldSkipFile(relativePath) {
  return relativePath.startsWith(UI_COMPONENT_PREFIX)
}

function isRuleAllowedInFile(rule, filePath) {
  const allowedPaths = ROOT_ALLOWED_RULE_PATHS[rule] || []
  return allowedPaths.some((item) => filePath === item || filePath.startsWith(item))
}

export function scanFileContent(relativePath, source) {
  const filePath = toPosixPath(relativePath)
  const extension = path.extname(filePath).toLowerCase()
  const violations = []

  if (SCRIPT_EXTENSIONS.has(extension)) {
    violations.push(
      ...collectPatternViolations(RULES.naiveImport, source, NAIVE_IMPORT_PATTERN, filePath, 1)
    )
  }

  if (extension === '.vue') {
    const templateBlocks = extractTagBlocks(source, 'template')
    for (const block of templateBlocks) {
      violations.push(
        ...collectPatternViolations(RULES.naiveTag, block.content, NAIVE_TAG_PATTERN, filePath, block.startLine)
      )
    }

    const styleBlocks = extractTagBlocks(source, 'style')
    for (const block of styleBlocks) {
      violations.push(
        ...collectPatternViolations(RULES.naiveDeep, block.content, NAIVE_DEEP_PATTERN, filePath, block.startLine),
        ...collectPatternViolations(RULES.naiveClass, block.content, NAIVE_CLASS_PATTERN, filePath, block.startLine),
        ...collectPatternViolations(RULES.glassMixin, block.content, GLASS_MIXIN_PATTERN, filePath, block.startLine),
        ...collectPatternViolations(RULES.hardcodedColor, block.content, HEX_COLOR_PATTERN, filePath, block.startLine),
        ...collectPatternViolations(RULES.hardcodedColor, block.content, RGB_COLOR_PATTERN, filePath, block.startLine),
        ...collectPatternViolations(RULES.hardcodedColor, block.content, HSL_COLOR_PATTERN, filePath, block.startLine)
      )
    }
  } else if (STYLE_EXTENSIONS.has(extension)) {
    violations.push(
      ...collectPatternViolations(RULES.naiveDeep, source, NAIVE_DEEP_PATTERN, filePath, 1),
      ...collectPatternViolations(RULES.naiveClass, source, NAIVE_CLASS_PATTERN, filePath, 1),
      ...collectPatternViolations(RULES.glassMixin, source, GLASS_MIXIN_PATTERN, filePath, 1),
      ...collectPatternViolations(RULES.hardcodedColor, source, HEX_COLOR_PATTERN, filePath, 1),
      ...collectPatternViolations(RULES.hardcodedColor, source, RGB_COLOR_PATTERN, filePath, 1),
      ...collectPatternViolations(RULES.hardcodedColor, source, HSL_COLOR_PATTERN, filePath, 1)
    )
  }

  const filtered = violations.filter((item) => !isRuleAllowedInFile(item.rule, filePath))
  return deduplicateViolations(filtered)
}

async function collectSourceFiles(repoRoot, scanRoot) {
  const files = []
  const rootPath = path.resolve(repoRoot, scanRoot)

  async function walk(currentPath) {
    let entries
    try {
      entries = await fs.readdir(currentPath, { withFileTypes: true })
    } catch (error) {
      if (error && typeof error === 'object' && 'code' in error && error.code === 'ENOENT') {
        return
      }
      throw error
    }

    for (const entry of entries) {
      const absolutePath = path.join(currentPath, entry.name)
      if (entry.isDirectory()) {
        await walk(absolutePath)
        continue
      }

      const relativePath = toPosixPath(path.relative(repoRoot, absolutePath))
      const extension = path.extname(relativePath).toLowerCase()
      if (!SCAN_EXTENSIONS.has(extension)) {
        continue
      }
      if (shouldSkipFile(relativePath)) {
        continue
      }

      files.push(relativePath)
    }
  }

  await walk(rootPath)
  return files
}

export async function scanProject(repoRoot = process.cwd(), scanRoots = DEFAULT_SCAN_ROOTS) {
  const allFiles = []
  for (const scanRoot of scanRoots) {
    const files = await collectSourceFiles(repoRoot, scanRoot)
    allFiles.push(...files)
  }

  const violations = []
  for (const relativePath of allFiles) {
    const absolutePath = path.resolve(repoRoot, relativePath)
    const source = await fs.readFile(absolutePath, 'utf8')
    violations.push(...scanFileContent(relativePath, source))
  }

  return violations.sort((a, b) => {
    const fileCompare = a.file.localeCompare(b.file)
    if (fileCompare !== 0) return fileCompare
    if (a.line !== b.line) return a.line - b.line
    return a.rule.localeCompare(b.rule)
  })
}

function normalizeAllowlistEntries(rawAllowlist) {
  if (!rawAllowlist) {
    return []
  }

  const entries = Array.isArray(rawAllowlist) ? rawAllowlist : rawAllowlist.entries
  if (!Array.isArray(entries)) {
    return []
  }

  return entries
    .filter((item) => item && typeof item === 'object')
    .map((item) => ({
      rule: String(item.rule || ''),
      file: toPosixPath(String(item.file || '')),
      line: Number(item.line || 0)
    }))
    .filter((item) => item.rule && item.file && Number.isInteger(item.line) && item.line > 0)
}

function createViolationKey(item) {
  return `${item.rule}|${toPosixPath(item.file)}|${item.line}`
}

export function partitionViolations(violations, allowlistEntries) {
  const normalizedAllowlist = normalizeAllowlistEntries(allowlistEntries)
  const allowlistKeySet = new Set(normalizedAllowlist.map(createViolationKey))
  const violationKeySet = new Set(violations.map(createViolationKey))

  const active = []
  const allowlisted = []
  for (const violation of violations) {
    if (allowlistKeySet.has(createViolationKey(violation))) {
      allowlisted.push(violation)
    } else {
      active.push(violation)
    }
  }

  const stale = normalizedAllowlist.filter((item) => !violationKeySet.has(createViolationKey(item)))
  return { active, allowlisted, stale }
}

async function readAllowlist(allowlistPath) {
  try {
    const raw = await fs.readFile(allowlistPath, 'utf8')
    return JSON.parse(raw)
  } catch (error) {
    if (error && typeof error === 'object' && 'code' in error && error.code === 'ENOENT') {
      return { version: 1, entries: [] }
    }
    throw error
  }
}

async function writeAllowlist(allowlistPath, violations) {
  const entries = violations
    .map((item) => ({
      rule: item.rule,
      file: item.file,
      line: item.line
    }))
    .sort((a, b) => {
      const fileCompare = a.file.localeCompare(b.file)
      if (fileCompare !== 0) return fileCompare
      if (a.line !== b.line) return a.line - b.line
      return a.rule.localeCompare(b.rule)
    })

  const payload = {
    version: 1,
    updatedAt: new Date().toISOString(),
    entries
  }

  await fs.writeFile(allowlistPath, `${JSON.stringify(payload, null, 2)}\n`, 'utf8')
}

function formatViolation(violation) {
  return `${violation.rule}  ${violation.file}:${violation.line}  ${violation.snippet}`
}

async function main() {
  const args = new Set(process.argv.slice(2))
  const repoRoot = process.cwd()
  const allowlistPath = path.resolve(repoRoot, DEFAULT_ALLOWLIST_PATH)

  const violations = await scanProject(repoRoot)

  if (args.has('--sync-allowlist')) {
    await writeAllowlist(allowlistPath, violations)
    console.log(`Synced allowlist entries: ${violations.length}`)
    return
  }

  const allowlist = await readAllowlist(allowlistPath)
  const result = partitionViolations(violations, allowlist)

  console.log('Style guard report')
  console.log(`Active violations: ${result.active.length}`)
  console.log(`Allowlisted debt: ${result.allowlisted.length}`)
  console.log(`Stale allowlist entries: ${result.stale.length}`)

  if (result.active.length > 0) {
    console.log('\nActive violations (first 50):')
    for (const violation of result.active.slice(0, 50)) {
      console.log(`- ${formatViolation(violation)}`)
    }
  }

  if (result.stale.length > 0) {
    console.log('\nStale allowlist entries (first 20):')
    for (const staleItem of result.stale.slice(0, 20)) {
      console.log(`- ${staleItem.rule}  ${staleItem.file}:${staleItem.line}`)
    }
  }

  if (result.active.length > 0) {
    process.exitCode = 1
  }
}

if (process.argv[1] && path.resolve(process.argv[1]) === path.resolve(fileURLToPath(import.meta.url))) {
  main().catch((error) => {
    console.error(error)
    process.exit(1)
  })
}
