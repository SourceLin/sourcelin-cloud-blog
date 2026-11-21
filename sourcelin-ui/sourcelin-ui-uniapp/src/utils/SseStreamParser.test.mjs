import { TextEncoder } from 'node:util';
import { test } from 'node:test';
import assert from 'node:assert/strict';
import { SseStreamParser } from './SseStreamParser.ts';
test('UTF-8 characters, emoji, CRLF and multiline data survive one-byte chunks', () => {
 const events = []; const parser = new SseStreamParser(event => events.push(event));
 const bytes = new TextEncoder().encode('event: message\r\ndata: 你好🌏\r\ndata: next\r\n\r\n');
 for (const byte of bytes) parser.push(Uint8Array.of(byte).buffer);
 assert.deepEqual(events, [{ event: 'message', data: '你好🌏\nnext' }]);
});
