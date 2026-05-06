import test from 'node:test'
import assert from 'node:assert/strict'
import fs from 'node:fs/promises'
import path from 'node:path'

const linksPagePath = path.resolve('src/modules/navigation/pages/LinksPage.vue')

test('Links page groups the apply card and empty state into a spaced tail section', async () => {
  const source = await fs.readFile(linksPagePath, 'utf8')

  assert.match(source, /class="links-tail"/)
  assert.match(
    source,
    /links-tail\s*\{[\s\S]*display:\s*grid;[\s\S]*gap:\s*(24px|var\(--spacing-xxl\));/
  )
  assert.match(source, /<div class="links-tail">[\s\S]*links-showcase[\s\S]*apply-card/s)
  assert.match(source, /class="links-showcase[\s\S]*links-showcase__empty"/)
})
