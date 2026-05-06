import type { App } from "vue";
import { createI18n } from "vue-i18n";
import zhCnLocale from "./package/zh-cn.json";

const messages = {
  "zh-cn": zhCnLocale,
};

const i18n = createI18n({
  legacy: false,
  locale: "zh-cn",
  fallbackLocale: "zh-cn",
  messages,
  globalInjection: true,
});

// 全局注册 i18n
export function setupI18n(app: App<Element>) {
  app.use(i18n);
}

export default i18n;
