/**
 * 将纯文本写入系统剪贴板（优先 Clipboard API，失败时降级 execCommand）。
 */
export async function copyPlainText(text: string): Promise<void> {
  const payload = text ?? ''
  try {
    await navigator.clipboard.writeText(payload)
  } catch {
    const textarea = document.createElement('textarea')
    textarea.value = payload
    textarea.setAttribute('readonly', '')
    textarea.style.position = 'fixed'
    textarea.style.left = '-9999px'
    textarea.style.top = '0'
    textarea.style.opacity = '0'

    document.body.appendChild(textarea)
    try {
      textarea.focus()
      textarea.select()
      const copied = document.execCommand('copy')
      if (!copied) {
        throw new Error('fallback copy failed')
      }
    } finally {
      document.body.removeChild(textarea)
    }
  }
}
