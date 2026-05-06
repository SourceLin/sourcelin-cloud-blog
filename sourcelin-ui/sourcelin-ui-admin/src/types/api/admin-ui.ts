export interface AdminUiDefaults {
  themePreference: "light" | "dark" | "auto" | "system";
  layout: "left" | "top" | "mix";
  sidebarAppearance: "classic" | "minimal";
  themeColor: string;
  showTagsView: boolean;
  showAppLogo: boolean;
  pageSwitchingAnimation: "none" | "fade" | "fade-slide" | "fade-scale";
  componentSize: "default" | "large" | "small";
}

export interface AdminUiPolicy {
  allowCustomThemeColor: boolean;
  allowUserLayoutSwitch: boolean;
  allowUserSidebarAppearanceSwitch: boolean;
}

export interface AdminUiConfigAggregate {
  defaults: AdminUiDefaults;
  policy: AdminUiPolicy;
  presets: string[];
}
