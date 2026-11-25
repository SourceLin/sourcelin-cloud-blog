import hljs from 'highlight.js/lib/core'
import bash from 'highlight.js/lib/languages/bash'
import c from 'highlight.js/lib/languages/c'
import cpp from 'highlight.js/lib/languages/cpp'
import css from 'highlight.js/lib/languages/css'
import dockerfile from 'highlight.js/lib/languages/dockerfile'
import go from 'highlight.js/lib/languages/go'
import java from 'highlight.js/lib/languages/java'
import javascript from 'highlight.js/lib/languages/javascript'
import json from 'highlight.js/lib/languages/json'
import kotlin from 'highlight.js/lib/languages/kotlin'
import markdown from 'highlight.js/lib/languages/markdown'
import php from 'highlight.js/lib/languages/php'
import python from 'highlight.js/lib/languages/python'
import rust from 'highlight.js/lib/languages/rust'
import scss from 'highlight.js/lib/languages/scss'
import shell from 'highlight.js/lib/languages/shell'
import sql from 'highlight.js/lib/languages/sql'
import typescript from 'highlight.js/lib/languages/typescript'
import xml from 'highlight.js/lib/languages/xml'
import yaml from 'highlight.js/lib/languages/yaml'
import 'highlight.js/styles/github-dark.css'

const REGISTERED_LANGUAGES = [
  ['bash', bash],
  ['sh', bash],
  ['zsh', bash],
  ['shell', shell],
  ['c', c],
  ['cpp', cpp],
  ['css', css],
  ['scss', scss],
  ['dockerfile', dockerfile],
  ['docker', dockerfile],
  ['go', go],
  ['golang', go],
  ['java', java],
  ['javascript', javascript],
  ['js', javascript],
  ['jsx', javascript],
  ['typescript', typescript],
  ['ts', typescript],
  ['tsx', typescript],
  ['json', json],
  ['kotlin', kotlin],
  ['markdown', markdown],
  ['md', markdown],
  ['php', php],
  ['python', python],
  ['py', python],
  ['rust', rust],
  ['rs', rust],
  ['sql', sql],
  ['xml', xml],
  ['html', xml],
  ['vue', xml],
  ['yaml', yaml],
  ['yml', yaml]
] as const

for (const [name, definition] of REGISTERED_LANGUAGES) {
  if (!hljs.getLanguage(name)) {
    hljs.registerLanguage(name, definition)
  }
}

export function useMarkdown() {
  function parseMarkdown(text: string): string {
    if (!text) return ''

    // 1. 转义 HTML，防止 XSS
    let html = text
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')

    // 2. 解析代码块: ```lang\ncode\n```
    const codeBlockRegex = /```(\w*)\n([\s\S]*?)```/g
    html = html.replace(codeBlockRegex, (_, lang, code) => {
      const codeTrimmed = code.trim()
      let highlighted = codeTrimmed
      if (lang && hljs.getLanguage(lang)) {
        try {
          highlighted = hljs.highlight(codeTrimmed, { language: lang }).value
        } catch (e) {
          console.error(e)
        }
      } else {
        try {
          highlighted = hljs.highlightAuto(codeTrimmed).value
        } catch (e) {
          console.error(e)
        }
      }
      const encodedCode = encodeURIComponent(codeTrimmed)
      return `<pre class="code-block hljs"><div class="code-header"><div class="code-mac-dots"><span class="code-mac-dot code-mac-dot--red"></span><span class="code-mac-dot code-mac-dot--yellow"></span><span class="code-mac-dot code-mac-dot--green"></span></div><span class="code-lang">${lang || 'code'}</span><button class="code-copy-btn" data-code="${encodedCode}"><span class="code-copy-btn-text">复制</span></button></div><code class="language-${lang}">${highlighted}</code></pre>`
    })

    // 3. 解析行内代码: `code`
    html = html.replace(/`([^`\n]+)`/g, '<code class="inline-code">$1</code>')

    // 4. 解析粗体: \*\*text\*\*
    html = html.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')

    // 5. 解析斜体: \*text\*
    html = html.replace(/\*([^*]+)\*/g, '<em>$1</em>')

    // 6. 解析无序列表和有序列表
    const lines = html.split('\n')
    let inUl = false
    let inOl = false
    const resultLines: string[] = []

    for (let i = 0; i < lines.length; i++) {
      const line = lines[i]

      // 跳过 pre 代码块内部
      if (line.includes('<pre') || (inUl && line.includes('</pre>')) || line.includes('<code')) {
        resultLines.push(line)
        continue
      }

      // 无序列表
      const ulMatch = line.match(/^[-*+]\s+(.+)$/)
      if (ulMatch) {
        if (inOl) {
          resultLines.push('</ol>')
          inOl = false
        }
        if (!inUl) {
          resultLines.push('<ul class="markdown-list">')
          inUl = true
        }
        resultLines.push(`<li>${ulMatch[1]}</li>`)
        continue
      }

      // 有序列表
      const olMatch = line.match(/^\d+\.\s+(.+)$/)
      if (olMatch) {
        if (inUl) {
          resultLines.push('</ul>')
          inUl = false
        }
        if (!inOl) {
          resultLines.push('<ol class="markdown-list-ordered">')
          inOl = true
        }
        resultLines.push(`<li>${olMatch[1]}</li>`)
        continue
      }

      // 如果既不是有序也不是无序列表
      if (inUl) {
        resultLines.push('</ul>')
        inUl = false
      }
      if (inOl) {
        resultLines.push('</ol>')
        inOl = false
      }

      resultLines.push(line)
    }

    if (inUl) resultLines.push('</ul>')
    if (inOl) resultLines.push('</ol>')

    html = resultLines.join('\n')

    // 7. 换行符处理（跳过 pre 代码块）
    const parts = html.split(/(<\/pre>|<pre[^>]*>)/g)
    let inPre = false
    for (let i = 0; i < parts.length; i++) {
      if (parts[i].startsWith('<pre')) {
        inPre = true
      } else if (parts[i] === '</pre>') {
        inPre = false
      } else if (!inPre) {
        parts[i] = parts[i].replace(/\n/g, '<br>')
      }
    }
    html = parts.join('')

    return html
  }

  return {
    parseMarkdown
  }
}
