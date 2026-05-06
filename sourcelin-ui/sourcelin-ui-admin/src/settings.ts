/**
 * 应用配置
 */

import type { AdminUiConfigAggregate, AdminUiDefaults, AdminUiPolicy } from "@/types/api";
import {
  ComponentSize,
  LayoutMode,
  PageSwitchingAnimationEnum,
  SidebarColor,
  ThemeMode,
} from "@/enums";

const env = import.meta.env;
const { pkg } = __APP_INFO__;

// ============================================
// 应用配置
// ============================================
export const appConfig = {
  name: pkg.name as string,
  version: pkg.version as string,
  title: (env.VITE_APP_TITLE as string) || pkg.name,
} as const;

// ============================================
// 后台默认配置前端兜底值
// ============================================
export const adminUiDefaultsFallback: AdminUiDefaults = {
  themePreference: "system",
  layout: LayoutMode.LEFT,
  sidebarAppearance: "classic",
  themeColor: "#4B55FA",
  showTagsView: true,
  showAppLogo: true,
  pageSwitchingAnimation: PageSwitchingAnimationEnum.FADE_SLIDE,
  componentSize: ComponentSize.DEFAULT,
};

export const adminUiPolicyFallback: AdminUiPolicy = {
  allowCustomThemeColor: true,
  allowUserLayoutSwitch: true,
  allowUserSidebarAppearanceSwitch: true,
};

export const themeColorPresetsFallback = [
  "#4B55FA",
  "#0F766E",
  "#C2410C",
  "#1D4ED8",
  "#BE185D",
] as const;

export const adminUiConfigFallback: AdminUiConfigAggregate = {
  defaults: adminUiDefaultsFallback,
  policy: adminUiPolicyFallback,
  presets: [...themeColorPresetsFallback],
};

// ============================================
// 兼容旧代码的前端默认值
// ============================================
export const defaults = {
  theme: ThemeMode.AUTO,
  themeColor: adminUiDefaultsFallback.themeColor,
  sidebarColorScheme: SidebarColor.CLASSIC_BLUE,
  layout: adminUiDefaultsFallback.layout,
  size: adminUiDefaultsFallback.componentSize,
  showTagsView: adminUiDefaultsFallback.showTagsView,
  showAppLogo: adminUiDefaultsFallback.showAppLogo,
  pageSwitchingAnimation: adminUiDefaultsFallback.pageSwitchingAnimation,
  showSettings: true,
} as const;
