import fs from 'node:fs/promises'
import path from 'node:path'
import process from 'node:process'
import { fileURLToPath } from 'node:url'

const DEFAULT_ALLOWLIST_PATH = 'scripts/layering-guard.allowlist.json'
const DEFAULT_SCAN_ROOTS = [
  'src/modules',
  'src/shared/components/layout',
  'src/shared/components/feedback',
  'src/shared/components/comments',
  'src/shared/components/business'
]
const SCAN_EXTENSIONS = new Set(['.vue', '.ts', '.tsx', '.js', '.jsx', '.mjs', '.cjs', '.scss'])

const RULES = {
  featureCard: 'feature-card-usage',
  foundationSurface: 'foundation-surface-direct',
  foundationResponsiveImport: 'foundation-responsive-import-direct'
}

const FEATURE_CARD_PATTERN = /<\s*FeatureCard\b|FeatureCard(?:\.vue)?['"]/g
const FOUNDATION_SURFACE_PATTERN =
  /(?<![-\w])glass-card(?![-\w])|\b(?:sourcelin-panel(?:-(?:soft|strong|inset|modal-field|hoverable))?|sourcelin-chip-surface(?:--accent)?)\b/g
const FOUNDATION_RESPONSIVE_IMPORT_PATTERN =
  /@(import|use)\s+['"]@\/assets\/styles\/foundation\/responsive(?:\.scss)?['"]/g

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

function collectPatternViolations(rule, content, pattern, relativeFilePath) {
  const violations = []
  let match

  while ((match = pattern.exec(content)) !== null) {
    const line = getLineNumber(content, match.index ?? 0)
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
  return violations.filter((item) => {
    const key = `${item.rule}|${item.file}|${item.line}|${item.snippet}`
    if (seen.has(key)) {
      return false
    }
    seen.add(key)
    return true
  })
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

      files.push(relativePath)
    }
  }

  await walk(rootPath)
  return files
}

export function scanFileContent(relativePath, source) {
  const filePath = toPosixPath(relativePath)

  return deduplicateViolations([
    ...collectPatternViolations(RULES.featureCard, source, FEATURE_CARD_PATTERN, filePath),
    ...collectPatternViolations(RULES.foundationSurface, source, FOUNDATION_SURFACE_PATTERN, filePath),
    ...collectPatternViolations(
      RULES.foundationResponsiveImport,
      source,
      FOUNDATION_RESPONSIVE_IMPORT_PATTERN,
      filePath
    )
  ])
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
    console.log(`Synced layering allowlist entries: ${violations.length}`)
    return
  }

  const allowlist = await readAllowlist(allowlistPath)
  const result = partitionViolations(violations, allowlist)

  console.log('Layering guard report')
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
    for (const item of result.stale.slice(0, 20)) {
      console.log(`- ${item.rule}  ${item.file}:${item.line}`)
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
