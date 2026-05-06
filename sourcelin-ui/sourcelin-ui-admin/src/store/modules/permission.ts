import type { RouteRecordRaw } from "vue-router";
import { constantRoutes } from "@/router";
import { store } from "@/store";
import router from "@/router";
import { useUserStoreHook } from "@/store/modules/user";

import MenuAPI from "@/api/system/menu";
import { RouteItem } from "@/types";
const modules = import.meta.glob("../../views/**/**.vue");
const Layout = () => import("../../layouts/index.vue");
const InnerLink = () => import("../../layouts/components/InnerLink/index.vue");

function joinRoutePath(parentPath: string, currentPath: string) {
  if (!currentPath) {
    return parentPath || "/";
  }
  if (currentPath.startsWith("http://") || currentPath.startsWith("https://")) {
    return currentPath;
  }
  if (currentPath.startsWith("/")) {
    return currentPath;
  }

  const normalizedParent = parentPath && parentPath !== "/" ? parentPath.replace(/\/+$/g, "") : "";
  return `${normalizedParent}/${currentPath}`.replace(/\/+/g, "/");
}

function sanitizeRouteNameSuffix(fullPath: string) {
  return (
    fullPath
      .replace(/^https?:\/\//, "")
      .replace(/[^a-zA-Z0-9_]/g, "_")
      .replace(/^_+|_+$/g, "") || "route"
  );
}

function normalizeRouteName(
  rawName: unknown,
  fullPath: string,
  ancestorNames: string[],
  usedNames: Set<string>
) {
  if (typeof rawName !== "string" || !rawName.trim()) {
    return undefined;
  }

  const base = rawName.trim();
  let candidate = base;

  if (ancestorNames.includes(candidate) || usedNames.has(candidate)) {
    candidate = `${base}__${sanitizeRouteNameSuffix(fullPath)}`;
  }

  let index = 1;
  const uniqueBase = candidate;
  while (ancestorNames.includes(candidate) || usedNames.has(candidate)) {
    candidate = `${uniqueBase}_${index++}`;
  }

  usedNames.add(candidate);
  return candidate;
}

function resolveViewComponent(componentPath: string) {
  const normalized = componentPath
    .trim()
    .replace(/^\/+/, "")
    .replace(/\.vue$/i, "");
  return (
    modules[`../../views/${normalized}.vue`] ||
    modules[`../../views/${normalized}/index.vue`] ||
    null
  );
}

function isHttpPath(path: string) {
  return /^https?:\/\//.test(path);
}

function cloneRegisterableRoute(route: RouteRecordRaw): RouteRecordRaw | null {
  const pathValue = typeof route.path === "string" ? route.path : "";
  // 外链路由只用于侧边栏展示与新窗口跳转，不注册进 vue-router
  if (isHttpPath(pathValue)) {
    return null;
  }

  const cloned: RouteRecordRaw = { ...route };
  if (Array.isArray(route.children) && route.children.length > 0) {
    const registerableChildren = route.children
      .map((child) => cloneRegisterableRoute(child))
      .filter((child): child is RouteRecordRaw => !!child);
    cloned.children = registerableChildren.length > 0 ? registerableChildren : undefined;
  }
  return cloned;
}

function getRegisterableRoutes(routes: RouteRecordRaw[]): RouteRecordRaw[] {
  return routes
    .map((route) => cloneRegisterableRoute(route))
    .filter((r): r is RouteRecordRaw => !!r);
}

/**
 * 将后端 RouterVo.query（JSON 字符串）合并到路由 meta.params，供侧边栏 AppLink 与页面使用。
 */
function mergeQueryIntoRouteMeta(
  meta: RouteRecordRaw["meta"] | undefined,
  queryJson: string | undefined
): RouteRecordRaw["meta"] | undefined {
  const prev =
    meta && typeof meta === "object"
      ? ({ ...meta } as Record<string, unknown>)
      : ({} as Record<string, unknown>);
  if (!queryJson?.trim()) {
    return Object.keys(prev).length ? (prev as RouteRecordRaw["meta"]) : meta;
  }
  try {
    const parsed = JSON.parse(queryJson) as Record<string, unknown>;
    if (parsed && typeof parsed === "object" && !Array.isArray(parsed)) {
      const oldParams =
        prev.params && typeof prev.params === "object" && !Array.isArray(prev.params)
          ? (prev.params as Record<string, unknown>)
          : {};
      return {
        ...prev,
        params: { ...oldParams, ...parsed },
      } as RouteRecordRaw["meta"];
    }
  } catch {
    // 非法 JSON 则忽略，不阻塞其它路由
  }
  return Object.keys(prev).length ? (prev as RouteRecordRaw["meta"]) : meta;
}

export const usePermissionStore = defineStore("permission", () => {
  // 所有路由（静态路由 + 动态路由）
  const routes = ref<RouteRecordRaw[]>([]);
  // 混合布局的左侧菜单路由
  const mixLayoutSideMenus = ref<RouteRecordRaw[]>([]);
  // 动态路由是否已生成
  const isRouteGenerated = ref(false);

  /** 生成动态路由 */
  async function generateRoutes(): Promise<RouteRecordRaw[]> {
    try {
      const data = await MenuAPI.getRoutes(); // 获取当前登录人的菜单路由
      const dynamicRoutes = transformRoutes(data);

      routes.value = [...constantRoutes, ...dynamicRoutes];

      return dynamicRoutes;
    } catch (error) {
      // 路由生成失败，重置状态
      isRouteGenerated.value = false;
      throw error;
    }
  }

  /** 设置混合布局左侧菜单 */
  const setMixLayoutSideMenus = (parentPath: string) => {
    const parentMenu = routes.value.find((item: RouteRecordRaw) => item.path === parentPath);
    mixLayoutSideMenus.value = parentMenu?.children || [];
  };

  /** 重置路由状态 */
  const resetRouter = () => {
    // 移除动态添加的路由
    const constantRouteNames = new Set(constantRoutes.map((route) => route.name).filter(Boolean));
    routes.value.forEach((route: RouteRecordRaw) => {
      if (route.name && !constantRouteNames.has(route.name)) {
        router.removeRoute(route.name);
      }
    });

    // 重置所有状态
    routes.value = [...constantRoutes];
    mixLayoutSideMenus.value = [];
    isRouteGenerated.value = false;
  };

  /** 注册动态路由（过滤外链 http(s) 节点） */
  const registerDynamicRoutes = (dynamicRoutes: RouteRecordRaw[]) => {
    const registerableRoutes = getRegisterableRoutes(dynamicRoutes);
    registerableRoutes.forEach((route: RouteRecordRaw) => {
      router.addRoute(route);
    });
    isRouteGenerated.value = true;
  };

  let reloadPromise: Promise<RouteRecordRaw[]> | null = null;

  /**
   * 重新加载动态路由（单飞）。
   *
   * 典型场景：后端权限变更导致接口返回权限不足（A0301），前端需要刷新路由和菜单以同步最新权限。
   *
   * - 会先清理已注册的动态路由（resetRouter）
   * - 重新从后端拉取路由（generateRoutes）
   * - 将动态路由注册到 vue-router（router.addRoute）
   */
  async function reloadDynamicRoutesOnce(): Promise<RouteRecordRaw[]> {
    if (reloadPromise) return reloadPromise;

    reloadPromise = (async () => {
      try {
        resetRouter();
        const dynamicRoutes = await generateRoutes();
        registerDynamicRoutes(dynamicRoutes);
        return dynamicRoutes;
      } finally {
        reloadPromise = null;
      }
    })();

    return reloadPromise;
  }

  let snapshotPromise: Promise<void> | null = null;

  /**
   * 刷新权限快照（单飞）。
   *
   * - 刷新用户信息（包含 perms/roles 等）
   * - 重新加载动态路由
   */
  async function reloadPermissionSnapshotOnce(): Promise<void> {
    if (snapshotPromise) return snapshotPromise;

    snapshotPromise = (async () => {
      try {
        const userStore = useUserStoreHook();
        await userStore.getUserInfo();
        await reloadDynamicRoutesOnce();
      } finally {
        snapshotPromise = null;
      }
    })();

    return snapshotPromise;
  }

  return {
    routes,
    mixLayoutSideMenus,
    isRouteGenerated,
    generateRoutes,
    setMixLayoutSideMenus,
    resetRouter,
    registerDynamicRoutes,
    reloadDynamicRoutesOnce,
    reloadPermissionSnapshotOnce,
  };
});

/**
 * 转换后端路由数据为Vue Router配置
 * 处理组件路径映射和Layout层级嵌套
 */
const transformRoutes = (routes: RouteItem[], isTopLevel: boolean = true): RouteRecordRaw[] => {
  return transformRoutesWithContext(routes, {
    isTopLevel,
    parentPath: "",
    ancestorNames: [],
    usedNames: new Set<string>(),
  });
};

interface TransformContext {
  isTopLevel: boolean;
  parentPath: string;
  ancestorNames: string[];
  usedNames: Set<string>;
}

function transformRoutesWithContext(
  routes: RouteItem[],
  context: TransformContext
): RouteRecordRaw[] {
  const normalizedRoutes: RouteRecordRaw[] = [];
  const { isTopLevel, parentPath, ancestorNames, usedNames } = context;

  routes.forEach((route) => {
    const { component, children, query: menuQueryStr, ...rest } = route;
    const processedComponent = isTopLevel || component !== "Layout" ? component : undefined;
    const normalizedRoute = { ...rest } as RouteRecordRaw;
    const routePath = typeof normalizedRoute.path === "string" ? normalizedRoute.path : "";
    const fullPath = joinRoutePath(parentPath, routePath);

    const normalizedName = normalizeRouteName(
      normalizedRoute.name,
      fullPath,
      ancestorNames,
      usedNames
    );
    if (normalizedName) {
      normalizedRoute.name = normalizedName;
    }

    if (!processedComponent) {
      // 多级菜单父级无需显式组件
      normalizedRoute.component = undefined;
    } else if (processedComponent === "Layout") {
      normalizedRoute.component = Layout;
    } else if (processedComponent === "InnerLink") {
      normalizedRoute.component = InnerLink;
    } else {
      // 后端下发了前端不存在的组件时直接丢弃，避免残留模板路由进入菜单
      const viewComponent = resolveViewComponent(processedComponent);
      if (!viewComponent) return;
      normalizedRoute.component = viewComponent;
    }

    if (children && children.length > 0) {
      const nextAncestorNames = normalizedName ? [...ancestorNames, normalizedName] : ancestorNames;
      normalizedRoute.children = transformRoutesWithContext(children, {
        isTopLevel: false,
        parentPath: fullPath,
        ancestorNames: nextAncestorNames,
        usedNames,
      });
    }

    // 对无组件且无子路由的节点做收敛；http 外链叶子节点应保留给 AppLink 处理
    if (
      !normalizedRoute.component &&
      (!normalizedRoute.children || normalizedRoute.children.length === 0)
    ) {
      if (!isHttpPath(routePath)) {
        return;
      }
    }

    // Layout 父菜单如果子路由全部被丢弃（例如组件不存在），则该节点本身也应丢弃；
    // 但 http 外链路径场景需要保留。
    if (
      normalizedRoute.component === Layout &&
      (!normalizedRoute.children || normalizedRoute.children.length === 0)
    ) {
      if (!isHttpPath(routePath)) {
        return;
      }
    }

    normalizedRoute.meta = mergeQueryIntoRouteMeta(normalizedRoute.meta, menuQueryStr);

    normalizedRoutes.push(normalizedRoute);
  });

  return normalizedRoutes;
}

/** 非组件环境使用权限store */
export function usePermissionStoreHook() {
  return usePermissionStore(store);
}
