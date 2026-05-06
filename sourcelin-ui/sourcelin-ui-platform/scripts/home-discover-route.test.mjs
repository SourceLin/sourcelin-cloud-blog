import test from 'node:test'
import assert from 'node:assert/strict'
import path from 'node:path'
import { pathToFileURL } from 'node:url'

const routeHelperUrl = pathToFileURL(
  path.resolve('src/modules/home/model/home-discover-route.ts')
).href
const scrollBehaviorUrl = pathToFileURL(
  path.resolve('src/app/router/scroll-behavior.ts')
).href

const routeHelpers = await import(routeHelperUrl)
const scrollBehaviorModule = await import(scrollBehaviorUrl)

test('parseDiscoverCategoryId parses valid query values and rejects invalid ones', () => {
  assert.equal(routeHelpers.parseDiscoverCategoryId('12'), 12)
  assert.equal(routeHelpers.parseDiscoverCategoryId(['8']), 8)
  assert.equal(routeHelpers.parseDiscoverCategoryId(['', '3']), null)
  assert.equal(routeHelpers.parseDiscoverCategoryId('0'), null)
  assert.equal(routeHelpers.parseDiscoverCategoryId('abc'), null)
  assert.equal(routeHelpers.parseDiscoverCategoryId(undefined), null)
})

test('buildDiscoverQuery keeps other query params while adding or removing dc', () => {
  assert.deepEqual(
    routeHelpers.buildDiscoverQuery({ keyword: 'vue', page: '2' }, 18),
    { keyword: 'vue', page: '2', dc: '18' }
  )

  assert.deepEqual(
    routeHelpers.buildDiscoverQuery({ keyword: 'vue', dc: '18' }, null),
    { keyword: 'vue' }
  )
})

test('resolveDiscoverCategoryId falls back to all when route category no longer exists', () => {
  const categories = [{ id: 1 }, { id: 8 }, { id: 12 }]

  assert.equal(routeHelpers.resolveDiscoverCategoryId(8, categories), 8)
  assert.equal(routeHelpers.resolveDiscoverCategoryId(5, categories), null)
  assert.equal(routeHelpers.resolveDiscoverCategoryId(null, categories), null)
})

test('resolveAppScrollPosition keeps current viewport on same-path query updates', () => {
  assert.equal(
    scrollBehaviorModule.resolveAppScrollPosition(
      { path: '/', query: { dc: '2' } },
      { path: '/', query: {} },
      null
    ),
    false
  )

  assert.deepEqual(
    scrollBehaviorModule.resolveAppScrollPosition(
      { path: '/article/1' },
      { path: '/' },
      null
    ),
    { left: 0, top: 0 }
  )

  assert.deepEqual(
    scrollBehaviorModule.resolveAppScrollPosition(
      { path: '/', query: {} },
      { path: '/article/1' },
      { left: 10, top: 20 }
    ),
    { left: 10, top: 20 }
  )
})
