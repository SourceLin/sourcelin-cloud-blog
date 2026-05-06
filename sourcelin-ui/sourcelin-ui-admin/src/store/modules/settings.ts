import {
  ComponentSize,
  LayoutMode,
  PageSwitchingAnimationEnum,
  SidebarColor,
  ThemeMode,
} from "@/enums";
import type { AdminUiDefaults, AdminUiPolicy } from "@/types/api";
import type { EffectiveThemeSettings } from "@/types/ui";
import { STORAGE_KEYS } from "@/constants";
import AdminUiAPI from "@/api/system/admin-ui";
import {
  adminUiDefaultsFallback,
  adminUiPolicyFallback,
  defaults,
  themeColorPresetsFallback,
} from "@/settings";
import {
  applyTheme,
  clearResolvedThemeVars,
  generateThemeColors,
  resolveThemeColor,
  syncResolvedThemeVars,
  toggleDarkMode,
  toggleSidebarColor,
} from "@/utils/theme";

function cloneDefaults(): AdminUiDefaults {
  return { ...adminUiDefaultsFallback };
}

function clonePolicy(): AdminUiPolicy {
  return { ...adminUiPolicyFallback };
}

function mapThemePreference(themePreference?: string | null): ThemeMode {
  if (themePreference === ThemeMode.DARK) {
    return ThemeMode.DARK;
  }
  if (themePreference === ThemeMode.LIGHT) {
    return ThemeMode.LIGHT;
  }
  return ThemeMode.AUTO;
}

function mapSidebarAppearance(sidebarAppearance?: string | null): SidebarColor {
  return sidebarAppearance === "minimal" ? SidebarColor.MINIMAL_WHITE : SidebarColor.CLASSIC_BLUE;
}

function normalizeDefaults(payload?: Partial<AdminUiDefaults> | null): AdminUiDefaults {
  return {
    ...cloneDefaults(),
    ...(payload || {}),
  };
}

function normalizePolicy(payload?: Partial<AdminUiPolicy> | null): AdminUiPolicy {
  return {
    ...clonePolicy(),
    ...(payload || {}),
  };
}

function normalizePresets(presets?: string[] | null) {
  const source = presets && presets.length > 0 ? presets : [...themeColorPresetsFallback];
  return source.map((item) => resolveThemeColor(item));
}

export const useSettingsStore = defineStore("setting", () => {
  const settingsVisible = ref(false);
  const preferredDark = usePreferredDark();

  const systemDefaults = ref<AdminUiDefaults>(cloneDefaults());
  const systemPolicy = ref<AdminUiPolicy>(clonePolicy());
  const themeColorPresets = ref<string[]>(normalizePresets());
  const initialized = ref(false);
  let initializeTask: Promise<void> | null = null;

  const localTheme = useStorage<ThemeMode | null>(STORAGE_KEYS.THEME, null);
  const localThemeColor = useStorage<string | null>(STORAGE_KEYS.THEME_COLOR, null);
  const localLayout = useStorage<LayoutMode | null>(STORAGE_KEYS.LAYOUT, null);
  const localSidebarColorScheme = useStorage<SidebarColor | null>(
    STORAGE_KEYS.SIDEBAR_COLOR_SCHEME,
    null
  );
  const localShowTagsView = useStorage<boolean | null>(STORAGE_KEYS.SHOW_TAGS_VIEW, null);
  const localShowAppLogo = useStorage<boolean | null>(STORAGE_KEYS.SHOW_APP_LOGO, null);
  const localPageSwitchingAnimation = useStorage<PageSwitchingAnimationEnum | null>(
    STORAGE_KEYS.PAGE_SWITCHING_ANIMATION,
    null
  );
  const localSize = useStorage<ComponentSize | null>(STORAGE_KEYS.SIZE, null);

  const theme = computed<ThemeMode>({
    get: () => localTheme.value ?? mapThemePreference(systemDefaults.value.themePreference),
    set: (value) => {
      localTheme.value = value;
    },
  });

  const themeColor = computed<string>({
    get: () => localThemeColor.value ?? systemDefaults.value.themeColor,
    set: (value) => {
      localThemeColor.value = value;
    },
  });

  const layout = computed<LayoutMode>({
    get: () => localLayout.value ?? (systemDefaults.value.layout as LayoutMode),
    set: (value) => {
      localLayout.value = value;
    },
  });

  const sidebarColorScheme = computed<SidebarColor>({
    get: () =>
      localSidebarColorScheme.value ?? mapSidebarAppearance(systemDefaults.value.sidebarAppearance),
    set: (value) => {
      localSidebarColorScheme.value = value;
    },
  });

  const showTagsView = computed<boolean>({
    get: () => localShowTagsView.value ?? systemDefaults.value.showTagsView,
    set: (value) => {
      localShowTagsView.value = value;
    },
  });

  const showAppLogo = computed<boolean>({
    get: () => localShowAppLogo.value ?? systemDefaults.value.showAppLogo,
    set: (value) => {
      localShowAppLogo.value = value;
    },
  });

  const pageSwitchingAnimation = computed<PageSwitchingAnimationEnum>({
    get: () =>
      localPageSwitchingAnimation.value ??
      (systemDefaults.value.pageSwitchingAnimation as PageSwitchingAnimationEnum),
    set: (value) => {
      localPageSwitchingAnimation.value = value;
    },
  });

  const size = computed<ComponentSize>({
    get: () => localSize.value ?? (systemDefaults.value.componentSize as ComponentSize),
    set: (value) => {
      localSize.value = value;
    },
  });

  const resolvedTheme = computed<ThemeMode.LIGHT | ThemeMode.DARK>(() =>
    theme.value === ThemeMode.AUTO
      ? preferredDark.value
        ? ThemeMode.DARK
        : ThemeMode.LIGHT
      : theme.value === ThemeMode.DARK
        ? ThemeMode.DARK
        : ThemeMode.LIGHT
  );

  const effectiveSettings = computed<EffectiveThemeSettings>(() => ({
    theme: theme.value,
    themeColor: themeColor.value,
    layout: layout.value,
    sidebarColorScheme: sidebarColorScheme.value,
    showTagsView: showTagsView.value,
    showAppLogo: showAppLogo.value,
    pageSwitchingAnimation: pageSwitchingAnimation.value,
    size: size.value,
  }));

  let themeSyncTaskId = 0;
  watch(
    [resolvedTheme, themeColor],
    (newValues, oldValues) => {
      const [nextTheme, nextColor] = newValues;
      const prevTheme = oldValues?.[0];
      const prevColor = oldValues?.[1];
      const themeChanged = prevTheme === undefined || nextTheme !== prevTheme;
      const colorChanged = prevColor === undefined || nextColor !== prevColor;

      if (!themeChanged && !colorChanged) {
        return;
      }

      const currentTaskId = ++themeSyncTaskId;
      if (themeChanged) {
        clearResolvedThemeVars();
        toggleDarkMode(nextTheme === ThemeMode.DARK);
      }
      applyTheme(generateThemeColors(nextColor, nextTheme));
      toggleSidebarColor(
        sidebarColorScheme.value === SidebarColor.CLASSIC_BLUE,
        nextTheme === ThemeMode.DARK
      );

      if (themeChanged) {
        void syncResolvedThemeVars().then(() => {
          if (currentTaskId !== themeSyncTaskId) {
            return;
          }
        });
      }
    },
    { immediate: true, flush: "post" }
  );
  watch(
    sidebarColorScheme,
    (value) => {
      toggleSidebarColor(
        value === SidebarColor.CLASSIC_BLUE,
        resolvedTheme.value === ThemeMode.DARK
      );
    },
    { immediate: true, flush: "post" }
  );

  async function initialize() {
    if (initialized.value) {
      return;
    }
    if (initializeTask) {
      return initializeTask;
    }

    initializeTask = (async () => {
      try {
        const config = await AdminUiAPI.getDefaults();
        systemDefaults.value = normalizeDefaults(config?.defaults);
        systemPolicy.value = normalizePolicy(config?.policy);
        themeColorPresets.value = normalizePresets(config?.presets);
      } catch (error) {
        console.warn("Failed to load admin ui defaults, using fallback defaults.", error);
        systemDefaults.value = normalizeDefaults(adminUiDefaultsFallback);
        systemPolicy.value = normalizePolicy(adminUiPolicyFallback);
        themeColorPresets.value = normalizePresets();
      } finally {
        initialized.value = true;
        initializeTask = null;
      }
    })();

    return initializeTask;
  }

  /**
   * 仅应用本地缓存的主题偏好（不发网络请求），用于应用启动时避免闪烁
   *
   * 主题的 watch immediate 会在 computed 值确定后自动触发 applyTheme，
   * 但 systemDefaults 初始值来自 fallback，所以本地主题在启动时即可生效。
   */
  function applyLocalTheme() {
    // watch([resolvedTheme, themeColor], ..., { immediate: true }) 已在 store 创建时注册，
    // computed 依赖 localTheme/localThemeColor 有值时会自动触发 applyTheme，
    // 无需额外操作。
  }

  function resetSettings() {
    localTheme.value = null;
    localThemeColor.value = null;
    localLayout.value = null;
    localSidebarColorScheme.value = null;
    localShowTagsView.value = null;
    localShowAppLogo.value = null;
    localPageSwitchingAnimation.value = null;
    localSize.value = null;
  }

  return {
    settingsVisible,
    initialized,
    systemDefaults,
    systemPolicy,
    themeColorPresets,
    showTagsView,
    showAppLogo,
    pageSwitchingAnimation,
    sidebarColorScheme,
    layout,
    themeColor,
    theme,
    size,
    resolvedTheme,
    effectiveSettings,
    initialize,
    applyLocalTheme,
    resetSettings,
    showSettings: defaults.showSettings,
  };
});
