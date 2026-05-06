import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { resolve } from 'path'

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
  if (
    normalizedId.includes('/node_modules/@vueup/vue-quill/') ||
    normalizedId.includes('/node_modules/highlight.js/')
  ) {
    return 'editor-highlight'
  }
  if (
    normalizedId.includes('/node_modules/echarts/') ||
    normalizedId.includes('/node_modules/vue-echarts/')
  ) {
    return 'charts'
  }
  return undefined
}

export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      imports: ['vue', 'vue-router', 'pinia', '@vueuse/core'],
      dts: 'src/auto-imports.d.ts'
    }),
    Components({
      dirs: ['src/shared/components', 'src/modules'],
      dts: 'src/components.d.ts'
    })
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
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
      '/blog-api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => {
          const normalized = path.replace(/^\/blog-api/, '')
          if (normalized.startsWith('/front') || normalized.startsWith('/app')) {
            return `/blog${normalized}`
          }
          return normalized
        }
      },
      '/blog': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/file': {
        target: 'http://localhost:8080',
        changeOrigin: true,
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
})
