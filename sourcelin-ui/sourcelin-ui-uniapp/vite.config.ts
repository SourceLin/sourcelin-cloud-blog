import { defineConfig } from 'vite';
import uni from '@dcloudio/vite-plugin-uni';
import path from 'node:path';
import { createRequire } from 'node:module';

const require = createRequire(import.meta.url);
const sass = require('sass') as typeof import('sass');

function mergeSilenceDeprecations(value: unknown): string[] {
  const list = Array.isArray(value) ? value.filter((item): item is string => typeof item === 'string') : [];
  return list.includes('legacy-js-api') ? list : [...list, 'legacy-js-api'];
}

function patchSassLegacyWarnings(): void {
  const target = sass as typeof import('sass') & {
    __sourcelinLegacyPatched__?: boolean;
  };
  if (target.__sourcelinLegacyPatched__) {
    return;
  }

  const renderSync = target.renderSync.bind(target);
  target.renderSync = ((options) => {
    const nextOptions = {
      ...(options || {}),
      silenceDeprecations: mergeSilenceDeprecations((options as { silenceDeprecations?: unknown } | undefined)?.silenceDeprecations)
    } as Parameters<typeof sass.renderSync>[0];
    return renderSync(nextOptions);
  }) as typeof target.renderSync;

  const render = target.render.bind(target);
  target.render = ((options, callback) => {
    const nextOptions = {
      ...(options || {}),
      silenceDeprecations: mergeSilenceDeprecations((options as { silenceDeprecations?: unknown } | undefined)?.silenceDeprecations)
    } as Parameters<typeof sass.render>[0];
    return render(nextOptions, callback);
  }) as typeof target.render;

  target.__sourcelinLegacyPatched__ = true;
}

patchSassLegacyWarnings();

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [uni()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        // 全局注入 SCSS 变量与 mixin
        additionalData: `@use "@/styles/variables.scss" as *;`
      }
    }
  },
  build: {
    target: 'es2017',
    minify: 'terser',
    sourcemap: false
  }
});
