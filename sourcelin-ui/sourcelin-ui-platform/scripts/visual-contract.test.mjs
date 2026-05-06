import test from 'node:test'
import assert from 'node:assert/strict'
import fs from 'node:fs/promises'
import path from 'node:path'

const read = (relativePath) => fs.readFile(path.resolve(relativePath), 'utf8')

test('theme tokens expose page rhythm and readable content variables', async () => {
  const base = await read('src/assets/styles/tokens/base.scss')
  const semantic = await read('src/assets/styles/tokens/semantic.scss')
  const component = await read('src/assets/styles/tokens/component.scss')

  assert.match(base, /--page-section-gap:/)
  assert.match(base, /--page-block-gap:/)
  assert.match(base, /--reading-line-height:/)
  assert.match(semantic, /--surface-page:/)
  assert.match(semantic, /--content-max-width-readable:/)
  assert.match(component, /--button-glow:/)
})

test('light theme transition keeps a warm sunrise palette', async () => {
  const component = await read('src/assets/styles/tokens/component.scss')

  assert.match(component, /--theme-transition-sky-overlay-light:[\s\S]*#FFE0B8|#FFD6A5|#FFC98B/)
  assert.match(component, /--theme-transition-sky-bg-light:[\s\S]*#FFD6A5|#FFE6C7|#FFF3D6/)
  assert.match(component, /--theme-transition-sun-gradient:[\s\S]*#FFD76A|#FFBF5F|#FFA94D/)
})

test('dark theme keeps layered blue-night surfaces instead of flat black panels', async () => {
  const base = await read('src/assets/styles/tokens/base.scss')
  const component = await read('src/assets/styles/tokens/component.scss')

  assert.match(base, /html\[data-theme='dark'\][\s\S]*rgba\(56, 189, 248, 0\.08\)/)
  assert.match(base, /html\[data-theme='dark'\][\s\S]*linear-gradient\(180deg, #09111F 0%, #0C1425 42%, #101A30 100%\)/)
  assert.match(base, /html\[data-theme='dark'\][\s\S]*--layout-surface: linear-gradient\(/)
  assert.match(base, /html\[data-theme='dark'\][\s\S]*--footer-surface: linear-gradient\(/)
  assert.match(component, /html\[data-theme='dark'\][\s\S]*color-mix\(in srgb, var\(--accent-color\) 6%, transparent\)/)
})

test('shared ui wrappers consume unified panel and glow variables', async () => {
  const card = await read('src/shared/components/ui/SCard.vue')
  const button = await read('src/shared/components/ui/SButton.vue')
  const input = await read('src/shared/components/ui/SInput.vue')
  const pagination = await read('src/shared/components/ui/SPagination.vue')

  assert.match(card, /surface-panel|shadow-panel|highlight-panel/)
  assert.match(button, /--button-glow|surface-panel/)
  assert.match(input, /input-focus-ring|surface-panel/)
  assert.match(pagination, /pagination-shell|pagination-item/)
})

test('native inputs keep token backgrounds while naive inner fields stay transparent', async () => {
  const input = await read('src/shared/components/ui/SInput.vue')
  const theme = await read('src/assets/styles/foundation/theme.scss')
  const override = await read('src/assets/styles/naive/override.scss')

  assert.match(input, /\.n-input__input-el\),[\s\S]*\.n-input__textarea-el\)\s*\{[\s\S]*background:\s*transparent !important;/)
  assert.doesNotMatch(input, /--s-input-min-height:\s*var\(--s-input-min-height,/)
  assert.doesNotMatch(input, /--s-input-wrapper-padding:\s*var\(--s-input-wrapper-padding,/)
  assert.doesNotMatch(input, /--s-input-padding-x:\s*var\(--s-input-padding-x,/)
  assert.doesNotMatch(input, /--s-input-textarea-padding-y:\s*var\(--s-input-textarea-padding-y,/)
  assert.match(input, /--s-input-min-height-resolved:\s*var\(--s-input-min-height,\s*46px\)/)
  assert.match(input, /--s-input-wrapper-padding-resolved:\s*var\(--s-input-wrapper-padding,\s*4px\)/)
  assert.match(input, /--s-input-padding-x-resolved:\s*var\(--s-input-padding-x,\s*0\.95rem\)/)
  assert.match(input, /--s-input-textarea-padding-y-resolved:\s*var\(--s-input-textarea-padding-y,\s*0\.9rem\)/)
  assert.match(input, /--n-padding-left:\s*var\(--s-input-padding-x-resolved\)/)
  assert.match(input, /--n-padding-right:\s*var\(--s-input-padding-x-resolved\)/)
  assert.match(input, /--n-padding-vertical:\s*var\(--s-input-textarea-padding-y-resolved\)/)
  assert.match(input, /--s-input-autofill-background:\s*var\(--input-background\)/)
  assert.match(input, /--s-input-autofill-background-focus:\s*var\(--input-background-focus\)/)
  assert.match(input, /s-input--filled[\s\S]*--s-input-autofill-background:\s*var\(--surface-panel-soft\)/)
  assert.match(theme, /input,\s*textarea\s*\{/)
  assert.match(theme, /background:\s*var\(--input-background\) !important;/)
  assert.match(theme, /background:\s*var\(--input-background-focus\) !important;/)
  assert.match(override, /--s-input-autofill-background,\s*var\(--input-background\)/)
  assert.match(override, /--s-input-autofill-background-focus,\s*var\(--input-background-focus\)/)
})

test('critical layout and reading surfaces keep visual rhythm contracts', async () => {
  const header = await read('src/shared/components/layout/AppHeader.vue')
  const footer = await read('src/shared/components/layout/AppFooter.vue')
  const home = await read('src/modules/home/pages/HomePage.vue')
  const article = await read('src/modules/article/pages/ArticlePage.vue')
  const comments = await read('src/shared/components/comments/CommentThread.vue')
  const treehole = await read('src/modules/treehole/pages/TreeholePage.vue')
  const say = await read('src/modules/say/pages/SayPage.vue')

  assert.match(header, /layout-surface-strong|toolbar-button-surface-hover/)
  assert.match(footer, /footer-card-surface|footer-divider/)
  assert.match(home, /page-section-gap|page-block-gap/)
  assert.match(article, /content-max-width-readable|page-block-gap/)
  assert.match(comments, /reply-item|comment-pagination/)
  assert.match(treehole, /page-block-gap|surface-panel/)
  assert.match(say, /page-block-gap|surface-panel/)
})

test('say publishing stays closed for guests across page and header entry points', async () => {
  const say = await read('src/modules/say/pages/SayPage.vue')
  const header = await read('src/shared/components/layout/AppHeader.vue')

  assert.match(say, /function openPublishComposer\(\)\s*\{[\s\S]*if\s*\(!userStore\.isLoggedIn\)[\s\S]*showPublishModal\.value = false[\s\S]*uiStore\.setSayPublishVisible\(false\)/)
  assert.match(say, /@publish="openPublishComposer"/)
  assert.match(say, /@click="openPublishComposer"/)
  assert.match(say, /if\s*\(uiStore\.sayPublishVisible\)\s*\{[\s\S]*void openPublishComposer\(\)/)
  assert.match(header, /function openSayPublish\(\)\s*\{[\s\S]*if\s*\(!userStore\.isLoggedIn\)[\s\S]*uiStore\.setSayPublishVisible\(false\)[\s\S]*router\.push\('\/login'\)[\s\S]*return[\s\S]*uiStore\.setSayPublishVisible\(true\)[\s\S]*router\.push\('\/say'\)/)
})


