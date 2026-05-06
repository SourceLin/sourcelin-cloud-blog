import test from 'node:test'
import assert from 'node:assert/strict'
import fs from 'node:fs/promises'
import path from 'node:path'

const marqueePath = path.resolve('src/modules/home/components/HomeMarquee.vue')

test('Home marquee supports dismiss action with persisted state', async () => {
  const source = await fs.readFile(marqueePath, 'utf8')

  assert.match(source, /v-if="showMarquee"/)
  assert.match(source, /class="marquee-close[\s\S]*@click="handleClose"/)
  assert.match(source, /const dismissed = ref\(false\)/)
  assert.match(source, /window\.localStorage\.setItem/)
  assert.match(source, /window\.localStorage\.getItem/)
})

test('Home marquee uses a perimeter runner that follows the shell path instead of rotating the whole ring', async () => {
  const source = await fs.readFile(marqueePath, 'utf8')

  assert.match(source, /marquee-frame/)
  assert.doesNotMatch(source, /marquee-progress/)
  assert.match(source, /marquee-frame__svg/)
  assert.match(source, /marquee-frame__track/)
  assert.match(source, /marquee-frame__runner/)
  assert.match(source, /<path[\s\S]*pathLength="100"/)
  assert.match(source, /border-radius:\s*inherit;/)
  assert.match(source, /stroke-dasharray:/)
  assert.match(source, /stroke-dashoffset:/)
  assert.match(source, /marquee-frame-run/)
  assert.match(source, /ResizeObserver/)
  assert.doesNotMatch(source, /conic-gradient\(/)
  assert.doesNotMatch(source, /mask-composite:\s*exclude;/)
  assert.doesNotMatch(source, /marquee-frame-rotate/)
})

test('Home marquee merges label, content, and actions into a single shell instead of nested boxed chrome', async () => {
  const source = await fs.readFile(marqueePath, 'utf8')

  assert.match(source, /marquee-signet/)
  assert.match(source, /marquee-copy/)
  assert.doesNotMatch(source, /marquee-content-wrapper/)
  assert.doesNotMatch(source, /marquee-label sourcelin-chip-surface/)
  assert.doesNotMatch(source, /marquee-close sourcelin-chip-surface/)
  assert.doesNotMatch(source, />站点</)
  assert.doesNotMatch(source, />公告</)
  assert.doesNotMatch(source, /通知公告/)
  assert.doesNotMatch(source, /marquee-signet__eyebrow/)
  assert.doesNotMatch(source, /marquee-signet__title/)
  assert.doesNotMatch(source, /marquee-body__eyebrow/)
  assert.match(source, /aria-label="查看公告"/)
  assert.match(source, /aria-label="关闭公告"/)
  assert.match(source, /inset:\s*0;/)
  assert.match(source, /overflow:\s*hidden;/)
})

test('Home marquee keeps the rotating notice copy as a pure text rail instead of an inner slab', async () => {
  const source = await fs.readFile(marqueePath, 'utf8')

  assert.doesNotMatch(source, /class="marquee-fade/)
  assert.match(source, /\.marquee-copy\s*\{[\s\S]*display:\s*flex;/)
  assert.match(source, /\.marquee-copy\s*\{[\s\S]*align-items:\s*center;/)
  assert.match(source, /\.marquee-copy\s*\{[\s\S]*min-height:\s*1\.78rem;/)
  assert.match(source, /\.marquee-item\s*\{[\s\S]*background:\s*none;/)
  assert.match(source, /\.marquee-item\s*\{[\s\S]*line-height:\s*1\.35;/)
})
