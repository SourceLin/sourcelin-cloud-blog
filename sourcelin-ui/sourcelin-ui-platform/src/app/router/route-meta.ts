import type { RouteRecordRaw } from 'vue-router'

export type RouteNavKind = 'header' | 'header-child' | 'mobile' | 'footer'

export interface AppRouteMeta {
  title?: string
  childTitle?: string
  icon?: string
  hidden?: boolean
  order?: number
  nav?: RouteNavKind
  parent?: string
  mobile?: boolean
  footer?: boolean
  shell?: 'default' | 'auth'
  /** SEO 专用标题，未指定时降级使用 title */
  seoTitle?: string
  /** SEO 专用描述，未指定时使用站点默认描述 */
  seoDescription?: string
}

export type AppRouteRecordRaw = Omit<RouteRecordRaw, 'children' | 'meta'> & {
  children?: AppRouteRecordRaw[]
  meta?: AppRouteMeta
}

export interface NavigationChild {
  label: string
  path: string
}

export interface NavigationItem extends NavigationChild {
  children?: NavigationChild[]
}

export interface MobileNavigationItem extends NavigationChild {
  icon: string
}

function flattenRoutes(routes: AppRouteRecordRaw[]): AppRouteRecordRaw[] {
  return routes.flatMap((route) => [route, ...flattenRoutes(route.children ?? [])])
}

function sortRoutes<T extends { order?: number; label: string }>(items: T[]) {
  return [...items].sort((left, right) => {
    const orderDelta = (left.order ?? 999) - (right.order ?? 999)
    if (orderDelta !== 0) {
      return orderDelta
    }

    return left.label.localeCompare(right.label, 'zh-CN')
  })
}

export function collectHeaderNavigation(routes: AppRouteRecordRaw[]): NavigationItem[] {
  const flattened = flattenRoutes(routes)
  const topLevelRoutes = flattened
    .filter((route) => route.meta?.nav === 'header' && !route.meta.hidden && route.meta.title)
    .map((route) => ({
      label: route.meta?.title ?? '',
      path: route.path,
      order: route.meta?.order,
      childTitle: route.meta?.childTitle
    }))

  const childRouteMap = new Map<string, NavigationChild[]>()

  flattened
    .filter((route) => route.meta?.nav === 'header-child' && !route.meta.hidden && route.meta.title && route.meta.parent)
    .forEach((route) => {
      const parentPath = route.meta?.parent as string
      const items = childRouteMap.get(parentPath) ?? []
      items.push({
        label: route.meta?.title ?? '',
        path: route.path,
        order: route.meta?.order
      } as NavigationChild & { order?: number })
      childRouteMap.set(parentPath, items)
    })

  return sortRoutes(topLevelRoutes).map((route) => ({
    label: route.label,
    path: route.path,
    children: (() => {
      const childRoutes = sortRoutes((childRouteMap.get(route.path) ?? []) as Array<NavigationChild & { order?: number }>)
      const selfChild = route.childTitle
        ? [{ label: route.childTitle, path: route.path, order: route.order }]
        : []

      return sortRoutes([...selfChild, ...childRoutes] as Array<NavigationChild & { order?: number }>).map((child) => ({
        label: child.label,
        path: child.path
      }))
    })()
  }))
}

export function collectMobileNavigation(routes: AppRouteRecordRaw[]): MobileNavigationItem[] {
  const flattened = flattenRoutes(routes)

  return sortRoutes(
    flattened
      .filter((route) => !route.meta?.hidden && route.meta?.title && (route.meta?.nav === 'mobile' || route.meta?.mobile))
      .map((route) => ({
        label: route.meta?.title ?? '',
        path: route.path,
        icon: route.meta?.icon ?? 'document',
        order: route.meta?.order
      }))
  ).map((route) => ({
    label: route.label,
    path: route.path,
    icon: route.icon
  }))
}

export function collectFooterNavigation(routes: AppRouteRecordRaw[]): NavigationChild[] {
  const flattened = flattenRoutes(routes)

  return sortRoutes(
    flattened
      .filter((route) => !route.meta?.hidden && route.meta?.title && (route.meta?.nav === 'footer' || route.meta?.footer))
      .map((route) => ({
        label: route.meta?.title ?? '',
        path: route.path,
        order: route.meta?.order
      }))
  ).map((route) => ({
    label: route.label,
    path: route.path
  }))
}

export function setupRouterNavigation<T extends { push: Function; replace: Function }>(router: T) {
  const originalPush = router.push
  const originalReplace = router.replace

  router.push = function(to: unknown) {
    return originalPush.call(router, to).catch(() => {})
  }

  router.replace = function(to: unknown) {
    return originalReplace.call(router, to).catch(() => {})
  }
}

