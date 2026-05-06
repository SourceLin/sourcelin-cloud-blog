/**
 * 应用启动入口
 *
 * @description
 * Vue3 应用初始化，包括样式、插件、配置的加载
 */

import { createApp } from "vue";
import App from "./App.vue";

// ===== 样式导入 =====
import "element-plus/dist/index.css";
import "element-plus/theme-chalk/dark/css-vars.css";
import "vxe-table/lib/style.css";
import "@/styles/index.scss";
import "uno.css";
import "animate.css";

// ===== 核心配置 =====
import { setupDirective } from "@/directives";
import { setupI18n } from "@/lang";
import { setupRouter } from "@/router";
import { setupStore, useSettingsStore } from "@/store";

// ===== 全局组件 =====
import * as ElementPlusIcons from "@element-plus/icons-vue";

// ===== 第三方插件 =====
import VXETable from "vxe-table";
import { InstallCodeMirror } from "codemirror-editor-vue3";
import { configureVxeTable } from "@/plugins/vxe-table";

// ===== 路由守卫 =====
import { setupPermissionGuard } from "@/router/guards/permission";

async function bootstrap() {
  const app = createApp(App);

  setupDirective(app);
  setupStore(app);

  const settingsStore = useSettingsStore();
  // 注意：settingsStore.initialize() 不在此处调用，
  // 而是在路由守卫中登录成功后触发，避免未登录时发起无效请求
  settingsStore.applyLocalTheme();

  setupRouter(app);
  setupI18n(app);

  Object.entries(ElementPlusIcons).forEach(([name, comp]) => app.component(name, comp));

  configureVxeTable();
  app.use(VXETable);
  app.use(InstallCodeMirror);

  setupPermissionGuard();
  app.mount("#app");
}

void bootstrap();
