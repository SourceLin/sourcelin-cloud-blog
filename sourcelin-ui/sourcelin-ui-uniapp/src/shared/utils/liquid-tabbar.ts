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
    text: '社区',
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

export function hideNativeTabbar(): void {
  uni.hideTabBar({ animation: false, fail: () => undefined });
}

export function switchLiquidTab(path: string, currentPath: string): void {
  if (path === currentPath) return;
  uni.switchTab({ url: `/${path}` });
}
