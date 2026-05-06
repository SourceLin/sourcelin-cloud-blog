import fs from 'node:fs'
import path from 'node:path'
import { assertFile, printResult, repoPath, toRepoRelative, walk } from './shared.mjs'

const findings = []

const requiredRules = [
  'rules/backend.md',
  'rules/frontend-platform.md',
  'rules/frontend-admin.md',
  'rules/api-contract.md',
  'rules/coding-conventions.md',
  'rules/testing-and-validation.md',
]

const removedRuleFiles = [
  'rules/ai-coding-rules.md',
  'rules/ai-review-checklist.md',
]

const requiredSkills = [
  'skills/frontend-platform-dev/SKILL.md',
  'skills/frontend-admin-dev/SKILL.md',
  'skills/backend-dev/SKILL.md',
  'skills/api-contract-governance/SKILL.md',
  'skills/architecture-refactor/SKILL.md',
]

const removedSkillNames = [
  'sourcelin-backend-dev',
  'sourcelin-frontend-admin',
  'sourcelin-frontend-platform',
  'unified-response-governance',
]

for (const rule of requiredRules) {
  assertFile(rule, findings)
}

for (const skill of requiredSkills) {
  assertFile(skill, findings)
}

assertFile('AGENTS.md', findings)
assertFile('skills/README.md', findings)
assertFile('rules/README.md', findings)

for (const file of removedRuleFiles) {
  if (fs.existsSync(repoPath(file))) {
    findings.push({ rule: 'removed-rule-present', file, message: '旧规则文件不应继续存在' })
  }
}

for (const skillName of removedSkillNames) {
  if (fs.existsSync(repoPath(`skills/${skillName}`))) {
    findings.push({ rule: 'removed-skill-present', file: `skills/${skillName}`, message: '旧技能目录不应继续存在' })
  }
}

for (const rule of requiredRules) {
  const file = repoPath(rule)
  if (!fs.existsSync(file)) continue
  const text = fs.readFileSync(file, 'utf8')
  for (const marker of ['MUST', 'MUST NOT', 'CHECK']) {
    if (!text.includes(marker)) {
      findings.push({ rule: 'rule-marker-missing', file: rule, message: `规则文件缺少 ${marker}` })
    }
  }
}

for (const skill of requiredSkills) {
  const file = repoPath(skill)
  if (!fs.existsSync(file)) continue
  const text = fs.readFileSync(file, 'utf8')
  for (const section of ['使用场景', '输入', '输出', '执行步骤', '依赖 rules', '禁止行为']) {
    if (!text.includes(section)) {
      findings.push({ rule: 'skill-section-missing', file: skill, message: `技能缺少章节：${section}` })
    }
  }
}

const docsToScan = [
  repoPath('AGENTS.md'),
  ...walk(repoPath('rules'), (name) => name.endsWith('.md')),
  ...walk(repoPath('skills'), (name) => name.endsWith('.md') || name.endsWith('.yaml')),
  ...walk(repoPath('docs/architecture'), (name) => name.endsWith('.md')),
]

const forbiddenRefs = [
  'ai-coding-rules',
  'ai-review-checklist',
  ...removedSkillNames,
]

for (const file of docsToScan) {
  const text = fs.readFileSync(file, 'utf8')
  for (const forbidden of forbiddenRefs) {
    if (text.includes(forbidden)) {
      findings.push({
        rule: 'stale-reference',
        file: toRepoRelative(file),
        message: `仍引用旧规则或旧技能：${forbidden}`,
      })
    }
  }
}

const agentsText = fs.existsSync(repoPath('AGENTS.md')) ? fs.readFileSync(repoPath('AGENTS.md'), 'utf8') : ''
for (const rule of requiredRules) {
  if (!agentsText.includes(rule)) {
    findings.push({ rule: 'agents-rule-missing', file: 'AGENTS.md', message: `AGENTS.md 未引用 ${rule}` })
  }
}

for (const skill of requiredSkills) {
  if (!agentsText.includes(skill)) {
    findings.push({ rule: 'agents-skill-missing', file: 'AGENTS.md', message: `AGENTS.md 未引用 ${skill}` })
  }
}

printResult('rules-system-guard', findings)
