export interface LiquidTabItem {
  path: string;
  text: string;
  icon: string;
  activeIcon: string;
}

export const liquidTabItems: LiquidTabItem[] = [
  {
    path: 'pages/home/home',
    text: '首页',
    icon: '/static/tabbar/home.png',
    activeIcon: '/static/tabbar/home-active.png'
  },
  {
    path: 'pages/discover/discover',
    text: '发现',
    icon: '/static/tabbar/discover.png',
    activeIcon: '/static/tabbar/discover-active.png'
  },
  {
    path: 'pages/community/community',
    text: '圈子',
    icon: '/static/tabbar/community.png',
    activeIcon: '/static/tabbar/community-active.png'
  },
  {
    path: 'pages/mine/mine',
    text: '我的',
    icon: '/static/tabbar/mine.png',
    activeIcon: '/static/tabbar/mine-active.png'
  }
];

/** 原生 tabBar 中的页面路径，switchTab 只对这些有效 */
const NATIVE_TAB_PATHS = new Set(['pages/home/home', 'pages/discover/discover', 'pages/mine/mine']);

export function hideNativeTabbar(): void {
  uni.hideTabBar({ animation: false, fail: () => undefined });
}

export function switchLiquidTab(path: string, currentPath: string): void {
  if (path === currentPath) return;
  if (NATIVE_TAB_PATHS.has(path)) {
    uni.switchTab({ url: `/${path}` });
  } else {
    uni.navigateTo({ url: `/${path}` });
  }
}