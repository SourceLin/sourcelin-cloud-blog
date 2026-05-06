import NProgress from "@/plugins/nprogress";
import router from "@/router";
import { usePermissionStore, useSettingsStore, useUserStore } from "@/store";
import { addRecentMenu } from "@/composables/useRecentMenus";

/**
 * 路由权限守卫
 *
 * 处理登录验证、动态路由生成、404检测等
 */
export function setupPermissionGuard() {
  const whiteList = ["/login"];

  router.beforeEach(async (to, _from, next) => {
    NProgress.start();

    try {
      const isLoggedIn = useUserStore().isLoggedIn();

      // 未登录处理
      if (!isLoggedIn) {
        if (whiteList.includes(to.path)) {
          next();
        } else {
          next(`/login?redirect=${encodeURIComponent(to.fullPath)}`);
          NProgress.done();
        }
        return;
      }

      // 已登录访问登录页，重定向到首页
      if (to.path === "/login") {
        next({ path: "/" });
        return;
      }

      const permissionStore = usePermissionStore();
      const userStore = useUserStore();

      // 动态路由生成
      if (!permissionStore.isRouteGenerated) {
        // 登录后首次路由跳转：加载服务端 UI 配置 + 用户信息
        const settingsStore = useSettingsStore();
        if (!settingsStore.initialized) {
          await settingsStore.initialize();
        }
        if (!userStore.userInfo?.roles?.length) {
          await userStore.getUserInfo();
        }

        const dynamicRoutes = await permissionStore.generateRoutes();
        permissionStore.registerDynamicRoutes(dynamicRoutes);

        next({ ...to, replace: true });
        return;
      }

      // 路由 404 检查
      if (to.matched.length === 0) {
        next("/404");
        return;
      }

      // 动态标题
      const title = (to.params.title as string) || (to.query.title as string);
      if (title) {
        to.meta.title = title;
      }

      next();
    } catch (error) {
      console.error("Route guard error:", error);
      await useUserStore().resetAllState();
      next("/login");
      NProgress.done();
    }
  });

  router.afterEach((to) => {
    NProgress.done();

    // 记录最近访问
    if (to.meta?.title && to.path) {
      const icon = typeof to.meta.icon === "string" ? to.meta.icon : undefined;
      addRecentMenu(to.path, to.meta.title as string, icon);
    }
  });
}
