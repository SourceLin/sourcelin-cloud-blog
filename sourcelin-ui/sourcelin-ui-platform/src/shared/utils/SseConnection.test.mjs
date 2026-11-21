import { test } from 'node:test'
import assert from 'node:assert/strict'
import { SseConnection } from './SseConnection.ts'
const source = () => ({ onopen: null, onerror: null, handlers: {}, closed: false, addEventListener(name, handler) { this.handlers[name] = handler }, close() { this.closed = true } })
test('disconnect during ticket handshake prevents a late connection', async () => {
 let resolve; let created = 0
 const manager = new SseConnection({ fetchTicket: () => new Promise(done => { resolve = done }), createSource: () => { created++; return source() } })
 const pending = manager.connect(); manager.disconnect(); resolve('ticket'); await pending; assert.equal(created, 0)
})
test('error before onopen retries and does not reconnect after logout', async () => {
 const sources = []; const manager = new SseConnection({ retryDelay: 0, fetchTicket: async () => 'ticket', createSource: () => { const next = source(); sources.push(next); return next } })
 await manager.connect(); sources[0].onerror(); await new Promise(done => setTimeout(done, 10)); assert.equal(sources.length, 2)
 manager.disconnect(); sources[1].onerror(); await new Promise(done => setTimeout(done, 10)); assert.equal(sources.length, 2)
})
test('named heartbeat preserves the active stream and validated messages reach listeners', async () => {
 const stream = source(); const received = []; const manager = new SseConnection({ fetchTicket: async () => 'ticket', createSource: () => stream })
 manager.on('message', data => received.push(data)); await manager.connect(); stream.onopen(); stream.handlers.heartbeat({ data: '{}' })
 stream.handlers.message({ data: '{"content":"changed","metadata":"system"}' }); assert.equal(received[0].content, 'changed'); assert.equal(stream.closed, false); manager.disconnect()
})
