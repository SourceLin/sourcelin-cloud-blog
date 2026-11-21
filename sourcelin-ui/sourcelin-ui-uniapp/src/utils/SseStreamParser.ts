export interface SseEvent { event: string; data: string }
/** 增量 UTF-8 解码，保留跨 chunk 的汉字、四字节字符及 SSE 多行数据。 */
export class SseStreamParser {
 private pending = new Uint8Array(0);
 private text = '';
 private receive: (event: SseEvent) => void;
 constructor(receive: (event: SseEvent) => void) { this.receive = receive; }
 push(chunk: ArrayBuffer): void {
  const incoming = new Uint8Array(chunk);
  const bytes = new Uint8Array(this.pending.length + incoming.length);
  bytes.set(this.pending); bytes.set(incoming, this.pending.length);
  let offset = 0;
  while (offset < bytes.length) {
   const first = bytes[offset];
   const width = first < 128 ? 1 : (first & 224) === 192 ? 2 : (first & 240) === 224 ? 3 : (first & 248) === 240 ? 4 : 1;
   if (offset + width > bytes.length) break;
   let point = first & (width === 1 ? 127 : width === 2 ? 31 : width === 3 ? 15 : 7);
   for (let index = 1; index < width; index++) point = (point << 6) | (bytes[offset + index] & 63);
   this.text += String.fromCodePoint(point); offset += width;
  }
  this.pending = bytes.slice(offset);
  this.text = this.text.replace(/\r\n/g, '\n');
  let boundary = this.text.indexOf('\n\n');
  while (boundary >= 0) {
   const block = this.text.slice(0, boundary); this.text = this.text.slice(boundary + 2);
   let event = 'message'; const data: string[] = [];
   block.split('\n').forEach(line => { if (line.startsWith('event:')) event = line.slice(6).trim(); if (line.startsWith('data:')) data.push(line.slice(5).replace(/^ /, '')); });
   if (data.length) this.receive({ event, data: data.join('\n') });
   boundary = this.text.indexOf('\n\n');
  }
 }
}
