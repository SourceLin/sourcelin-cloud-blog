/**
 * 应用设置相关类型定义
 */

import type {
  ComponentSize,
  LayoutMode,
  PageSwitchingAnimationEnum,
  SidebarColor,
  ThemeMode,
} from "@/enums";

export interface AppSettings {
  title: string;
  version: string;
  showSettings: boolean;
  showTagsView: boolean;
  showAppLogo: boolean;
  layout: "left" | "top" | "mix";
  themeColor: string;
  theme: ThemeMode;
  size: string;
  sidebarColorScheme: "classic-blue" | "minimal-white";
}

export interface LocalThemePreference {
  theme?: ThemeMode | null;
  themeColor?: string | null;
  layout?: LayoutMode | null;
  sidebarColorScheme?: SidebarColor | null;
  showTagsView?: boolean | null;
  showAppLogo?: boolean | null;
  pageSwitchingAnimation?: PageSwitchingAnimationEnum | null;
  size?: ComponentSize | null;
}

export interface EffectiveThemeSettings {
  theme: ThemeMode;
  themeColor: string;
  layout: LayoutMode;
  sidebarColorScheme: SidebarColor;
  showTagsView: boolean;
  showAppLogo: boolean;
  pageSwitchingAnimation: PageSwitchingAnimationEnum;
  size: ComponentSize;
}
