import assert from 'node:assert/strict'
import fs from 'node:fs/promises'
import os from 'node:os'
import path from 'node:path'

import { scanProject } from './layering-guard.mjs'

async function testLayeringGuardCatchesFeatureCardAndFoundationDrift() {
  const tempRoot = await fs.mkdtemp(path.join(os.tmpdir(), 'sourcelin-layering-guard-'))

  try {
    await fs.mkdir(path.join(tempRoot, 'src/modules/demo/pages'), { recursive: true })
    await fs.mkdir(path.join(tempRoot, 'src/shared/components/business'), { recursive: true })

    await fs.writeFile(
      path.join(tempRoot, 'src/modules/demo/pages/DemoPage.vue'),
      `<template>\n  <FeatureCard />\n  <div class="glass-card sourcelin-panel-soft" />\n</template>\n`,
      'utf8'
    )

    await fs.writeFile(
      path.join(tempRoot, 'src/shared/components/business/DemoPanel.vue'),
      `<template>\n  <span class="sourcelin-chip-surface">标签</span>\n</template>\n<style scoped lang="scss">\n@import '@/assets/styles/foundation/responsive';\n</style>\n`,
      'utf8'
    )

    await fs.writeFile(
      path.join(tempRoot, 'src/modules/demo/pages/AllowedResponsivePage.vue'),
      `<style scoped lang="scss">\n@import '@/shared/styles/responsive';\n.demo { display: grid; }\n</style>\n`,
      'utf8'
    )

    const violations = await scanProject(tempRoot)

    assert.equal(
      violations.some((item) => item.rule === 'feature-card-usage'),
      true,
      'layering guard should catch new FeatureCard usages'
    )
    assert.equal(
      violations.some((item) => item.rule === 'foundation-surface-direct'),
      true,
      'layering guard should catch direct foundation surface classes in business/page layers'
    )
    assert.equal(
      violations.some((item) => item.rule === 'foundation-responsive-import-direct'),
      true,
      'layering guard should catch direct foundation responsive imports in business/page layers'
    )
    assert.equal(
      violations.some((item) => item.file.endsWith('AllowedResponsivePage.vue')),
      false,
      'layering guard should allow shared responsive facade imports'
    )
  } finally {
    await fs.rm(tempRoot, { recursive: true, force: true })
  }
}

async function testDocsRecordBusinessLayerAndGuardContracts() {
  const agents = await fs.readFile(path.resolve('AGENTS.md'), 'utf8')
  const architecture = await fs.readFile(path.resolve('docs/frontend-architecture.md'), 'utf8')

  for (const expected of ['src/shared/components/business/**', 'FeatureCard', 'SSurfacePanel', 'layering:guard']) {
    assert.equal(agents.includes(expected), true, `AGENTS.md should include "${expected}"`)
  }

  for (const expected of ['src/shared/components/business/**', 'SSurfacePanel', 'FeatureCard', 'layering:guard']) {
    assert.equal(
      architecture.includes(expected),
      true,
      `docs/frontend-architecture.md should include "${expected}"`
    )
  }
}

await testLayeringGuardCatchesFeatureCardAndFoundationDrift()
await testDocsRecordBusinessLayerAndGuardContracts()
