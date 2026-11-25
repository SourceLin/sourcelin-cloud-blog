import { defineConfig, loadEnv, type Plugin } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { resolve } from 'path'
import { mkdirSync, writeFileSync } from 'fs'
import type { ApiResponse, PageResult } from './src/shared/types/api'

function manualChunks(id: string) {
  const normalizedId = id.replace(/\\/g, '/')

  if (normalizedId.includes('/node_modules/@vicons/ionicons5/')) {
    return 'vicons'
  }
  if (
    normalizedId.includes('/node_modules/vue/') ||
    normalizedId.includes('/node_modules/vue-router/') ||
    normalizedId.includes('/node_modules/pinia/')
  ) {
    return 'vue-core'
  }
  if (
    normalizedId.includes('/node_modules/@vueuse/core/') ||
    normalizedId.includes('/node_modules/@vueuse/shared/')
  ) {
    return 'vue-use'
  }
  if (normalizedId.includes('/node_modules/naive-ui/')) {
    return 'naive-ui'
  }
  if (
    normalizedId.includes('/node_modules/vueuc/') ||
    normalizedId.includes('/node_modules/vooks/') ||
    normalizedId.includes('/node_modules/vdirs/') ||
    normalizedId.includes('/node_modules/treemate/') ||
    normalizedId.includes('/node_modules/seemly/') ||
    normalizedId.includes('/node_modules/@css-render/')
  ) {
    return 'naive-ecosystem'
  }
  if (normalizedId.includes('/node_modules/@vueup/vue-quill/') || normalizedId.includes('/node_modules/quill/')) {
    return 'quill'
  }
  if (normalizedId.includes('/node_modules/highlight.js/')) {
    return 'highlight'
  }
  if (
    normalizedId.includes('/node_modules/echarts/') ||
    normalizedId.includes('/node_modules/vue-echarts/')
  ) {
    return 'charts'
  }
  return undefined
}

/**
 * Sitemap 构建插件。
 * 构建完成后在产物目录生成 sitemap.xml。
 * 静态路由使用下方清单，动态文章从后端分页接口获取。
 *
 * 使用方式：
 *   - VITE_SITE_ORIGIN: 站点根 URL，例如 https://your-domain.com
 *   - VITE_API_ORIGIN: 后端 API 根地址，例如 http://localhost:8080
 *     若未配置则跳过动态文章条目，仅生成静态路由
 */
function sitemapPlugin(siteOrigin: string, apiOrigin: string): Plugin {
  // 静态路由列表（path、changefreq、priority）
  const staticRoutes = [
    { path: '/', changefreq: 'daily', priority: '1.0' },
    { path: '/archive', changefreq: 'weekly', priority: '0.8' },
    { path: '/categories', changefreq: 'weekly', priority: '0.7' },
    { path: '/tags', changefreq: 'weekly', priority: '0.7' },
    { path: '/hot', changefreq: 'daily', priority: '0.8' },
    { path: '/say', changefreq: 'daily', priority: '0.6' },
    { path: '/treehole', changefreq: 'daily', priority: '0.6' },
    { path: '/about', changefreq: 'monthly', priority: '0.5' },
    { path: '/navigation', changefreq: 'monthly', priority: '0.5' }
  ]

  function buildSitemapXml(origin: string, urlEntries: { loc: string; lastmod?: string; changefreq: string; priority: string }[]) {
    const items = urlEntries
      .map((entry) => [
        '  <url>',
        `    <loc>${entry.loc}</loc>`,
        entry.lastmod ? `    <lastmod>${entry.lastmod}</lastmod>` : '',
        `    <changefreq>${entry.changefreq}</changefreq>`,
        `    <priority>${entry.priority}</priority>`,
        '  </url>'
      ].filter(Boolean).join('\n'))
      .join('\n')

    void origin
    return `<?xml version="1.0" encoding="UTF-8"?>\n<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">\n${items}\n</urlset>`
  }

  return {
    name: 'vite-plugin-sitemap-gen',
    apply: 'build',
    async closeBundle() {
      const outDir = 'sourcelin-ui-platform'

      if (!siteOrigin) {
        console.warn('[sitemap] VITE_SITE_ORIGIN 未配置，跳过 sitemap 生成。请在 .env.production 中设置 VITE_SITE_ORIGIN。')
        return
      }

      const urlEntries: { loc: string; lastmod?: string; changefreq: string; priority: string }[] = staticRoutes.map(
        (route) => ({
          loc: `${siteOrigin}${route.path}`,
          changefreq: route.changefreq,
          priority: route.priority
        })
      )

      if (apiOrigin) {
        const articleEntries: typeof urlEntries = []
        try {
          let totalPages = 1
          for (let page = 1; page <= totalPages; page++) {
            const url = new URL('/blog/front/articles', apiOrigin)
            url.search = new URLSearchParams({ page: String(page), pageSize: '100' }).toString()
            const response = await fetch(url, { signal: AbortSignal.timeout(10000) })
            if (!response.ok) throw new Error(`文章列表请求失败 (${response.status})`)
            const result = await response.json() as ApiResponse<PageResult<{ id: number; updateTime?: string }>>
            if (result.code !== 0 || !Array.isArray(result.data?.items)) {
              throw new Error('文章列表响应不符合分页契约')
            }
            if (page === 1) totalPages = result.data.totalPages
            for (const item of result.data.items) {
              articleEntries.push({
                loc: `${siteOrigin}/article/${item.id}`,
                lastmod: item.updateTime?.slice(0, 10),
                changefreq: 'weekly',
                priority: '0.9'
              })
            }
          }
          urlEntries.push(...articleEntries)
          console.info(`[sitemap] 动态文章条目: ${articleEntries.length} 条`)
        } catch (err) {
          console.warn('[sitemap] 拉取文章列表失败，仅生成静态路由条目。', err)
        }
      }

      const xml = buildSitemapXml(siteOrigin, urlEntries)
      mkdirSync(outDir, { recursive: true })
      writeFileSync(`${outDir}/sitemap.xml`, xml, 'utf-8')
      console.info(`[sitemap] 已生成 ${outDir}/sitemap.xml，共 ${urlEntries.length} 条记录`)
    }
  }
}

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const blogApiBase = env.VITE_APP_BASE_API || '/blog-api'
  const blogApiTarget = env.VITE_APP_API_URL || 'http://localhost:8080'
  const fileApiBase = env.VITE_FILE_BASE_API || '/file'

  return {
    plugins: [
      vue(),
      AutoImport({
        imports: ['vue', 'vue-router', 'pinia', '@vueuse/core'],
        dts: 'src/auto-imports.d.ts'
      }),
      Components({
        dirs: ['src/shared/components', 'src/modules'],
        dts: 'src/components.d.ts'
      }),
      sitemapPlugin(env.VITE_SITE_ORIGIN || '', env.VITE_API_ORIGIN || '')
    ],
    resolve: {
      alias: {
        '@': resolve(import.meta.dirname, 'src')
      },
      // 优先解析 TS，避免同名 .js/.ts 并存时命中历史 JS 文件
      extensions: ['.mjs', '.ts', '.js', '.mts', '.jsx', '.tsx', '.json']
    },
    build: {
      outDir: 'sourcelin-ui-platform',
      rollupOptions: {
        output: {
          manualChunks
        }
      }
    },
    server: {
      host: '0.0.0.0',
      port: 80,
      proxy: {
        [blogApiBase]: {
          target: blogApiTarget,
          changeOrigin: true,
          rewrite: (path) => {
            const normalized = path.replace(new RegExp(`^${blogApiBase}`), '')
            if (normalized.startsWith('/front') || normalized.startsWith('/app')) {
              return `/blog${normalized}`
            }
            return normalized
          }
        },
        '/blog': {
          target: blogApiTarget,
          changeOrigin: true
        },
        [fileApiBase]: {
          target: blogApiTarget,
          changeOrigin: true
        }
      }
    },
    css: {
      preprocessorOptions: {
        scss: {
          silenceDeprecations: ['import', 'legacy-js-api']
        }
      }
    }
  }
})
