import test from 'node:test'
import assert from 'node:assert/strict'

import { partitionViolations, scanFileContent } from './style-guard.mjs'

test('scanFileContent detects naive tag, glass mixin and hardcoded colors in vue style block', () => {
  const vueSource = `
<template>
  <div>
    <n-card />
  </div>
</template>

<script setup>
const color = '#ffffff'
</script>

<style scoped lang="scss">
.demo {
  @include liquid-glass-effect('lite');
  color: #fff;
  background: rgba(0, 0, 0, 0.3);
}
</style>
`

  const violations = scanFileContent('src/modules/demo/pages/DemoPage.vue', vueSource)
  const rules = violations.map((item) => item.rule).sort()

  assert.deepEqual(rules, ['glass-mixin', 'hardcoded-color', 'hardcoded-color', 'naive-tag'])
})

test('partitionViolations keeps allowlisted debt out of active list', () => {
  const violations = [
    { rule: 'glass-mixin', file: 'src/modules/demo/pages/DemoPage.vue', line: 12, snippet: "@include liquid-glass-effect('lite');" },
    { rule: 'hardcoded-color', file: 'src/modules/demo/pages/DemoPage.vue', line: 13, snippet: 'color: #fff;' }
  ]
  const allowlist = [
    { rule: 'glass-mixin', file: 'src/modules/demo/pages/DemoPage.vue', line: 12 }
  ]

  const result = partitionViolations(violations, allowlist)

  assert.equal(result.active.length, 1)
  assert.equal(result.allowlisted.length, 1)
  assert.equal(result.stale.length, 0)
  assert.equal(result.active[0].rule, 'hardcoded-color')
})

test('scanFileContent does not auto-allow legacy glass mixins under legacy pages styles', () => {
  const styleSource = `
.demo {
  @include liquid-glass-effect('lite');
}
`

  const violations = scanFileContent('src/assets/styles/legacy/pages/_legacy.scss', styleSource)

  assert.equal(violations.length, 1)
  assert.equal(violations[0].rule, 'glass-mixin')
})

test('scanFileContent still allows glass mixins inside ui abstraction layer', () => {
  const vueSource = `
<template>
  <div />
</template>

<style scoped lang="scss">
.demo {
  @include liquid-glass-effect('lite');
}
</style>
`

  const violations = scanFileContent('src/shared/components/ui/DemoGlass.vue', vueSource)

  assert.equal(violations.length, 0)
})

test('scanFileContent allows naive imports in discrete bridge composables', () => {
  const source = `
import { createDiscreteApi } from 'naive-ui'

export const message = createDiscreteApi(['message'])
`

  const violations = scanFileContent('src/shared/composables/useSMessage.ts', source)

  assert.equal(violations.length, 0)
})
