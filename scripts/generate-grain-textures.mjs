/**
 * 生成液态玻璃噪声纹理 PNG（纯 Node.js 方案，无需 canvas 依赖）
 *
 * 用途：为 Ice / Frost / Gem 变体提供 2-3% 透明度静态噪声，消除色彩断层（Banding），增加物理质感
 *
 * 运行：node scripts/generate-grain-textures.mjs
 * 输出：sourcelin-ui-platform/public/assets/textures/glass-grain-*.png
 */

import { writeFileSync, mkdirSync } from 'fs'
import { dirname, join } from 'path'
import { fileURLToPath } from 'url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = dirname(__filename)

// 纹理输出路径
const OUTPUT_DIR = join(__dirname, '../sourcelin-ui/sourcelin-ui-platform/public/assets/textures')

/**
 * 生成最小 PNG（1x1 像素，透明）
 * 实际上我们将使用 CSS 渐变模拟噪声，这个脚本仅作为占位
 */
function generateMinimalPNG() {
  // 1x1 透明 PNG 的最小字节序列
  return Buffer.from([
    0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // PNG 签名
    0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52, // IHDR 长度
    0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, // 宽度 1, 高度 1
    0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, 0xC4,
    0x89, 0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41, // IDAT 长度
    0x54, 0x78, 0x9C, 0x63, 0x00, 0x01, 0x00, 0x00,
    0x05, 0x00, 0x01, 0x0D, 0x0A, 0x2D, 0xB4, 0x00,
    0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, 0xAE, // IEND
    0x42, 0x60, 0x82
  ])
}

// 纹理配置（实际使用 CSS 渐变模拟，PNG 仅作为 fallback）
const textures = [
  'glass-grain-light.png',
  'glass-grain-dark.png',
  'glass-grain-frost.png',
  'glass-grain-frost-dark.png',
  'glass-grain-gem-dark.png',
]

console.log('🔨 生成液态玻璃噪声纹理（占位文件）...\n')
console.log('⚠️  注意：实际项目使用 CSS 渐变模拟噪声纹理，无需外部 PNG 文件')
console.log('   这些文件仅作为 fallback 保留。\n')

// 确保输出目录存在
mkdirSync(OUTPUT_DIR, { recursive: true })

const minimalPNG = generateMinimalPNG()

textures.forEach((name) => {
  const outputPath = join(OUTPUT_DIR, name)
  writeFileSync(outputPath, minimalPNG)
  console.log(`✅ ${name.padEnd(30)} 1x1 placeholder (使用 CSS 渐变模拟)}`)
})

console.log(`\n📁 输出目录: ${OUTPUT_DIR}`)
console.log('✨ 噪声纹理占位文件生成完成！')
console.log('\n💡 提示：在 SCSS 中使用 --glass-*-grain 变量定义 CSS 渐变噪声，无需加载外部文件')
