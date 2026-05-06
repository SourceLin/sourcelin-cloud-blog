import test from 'node:test'
import assert from 'node:assert/strict'

import { createStartupScrollLock } from '../src/shared/utils/startupScrollLock.ts'

function createMockDocument(initialHtmlOverflow = '', initialBodyOverflow = '') {
  return {
    documentElement: {
      style: {
        overflow: initialHtmlOverflow
      }
    },
    body: {
      style: {
        overflow: initialBodyOverflow
      }
    }
  }
}

test('restores original overflow after duplicate visible sync calls', () => {
  const mockDocument = createMockDocument('', 'auto')
  const scrollLock = createStartupScrollLock(mockDocument)

  scrollLock.sync(true)
  scrollLock.sync(true)

  assert.equal(mockDocument.documentElement.style.overflow, 'hidden')
  assert.equal(mockDocument.body.style.overflow, 'hidden')

  scrollLock.sync(false)

  assert.equal(mockDocument.documentElement.style.overflow, '')
  assert.equal(mockDocument.body.style.overflow, 'auto')
})

test('release is idempotent when no lock has been applied', () => {
  const mockDocument = createMockDocument('clip', 'auto')
  const scrollLock = createStartupScrollLock(mockDocument)

  scrollLock.sync(false)
  scrollLock.release()

  assert.equal(mockDocument.documentElement.style.overflow, 'clip')
  assert.equal(mockDocument.body.style.overflow, 'auto')
})

test('overlapping locks restore scroll only after the final release', () => {
  const mockDocument = createMockDocument('', 'auto')
  const startupLock = createStartupScrollLock(mockDocument)
  const searchLock = createStartupScrollLock(mockDocument)

  startupLock.sync(true)
  searchLock.sync(true)
  startupLock.sync(false)

  assert.equal(mockDocument.documentElement.style.overflow, 'hidden')
  assert.equal(mockDocument.body.style.overflow, 'hidden')

  searchLock.sync(false)

  assert.equal(mockDocument.documentElement.style.overflow, '')
  assert.equal(mockDocument.body.style.overflow, 'auto')
})
