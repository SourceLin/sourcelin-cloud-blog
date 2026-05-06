const HEX_3_REGEXP = /^#([\da-f]{3})$/i;
const HEX_4_REGEXP = /^#([\da-f]{4})$/i;
const HEX_6_REGEXP = /^#([\da-f]{6})$/i;
const HEX_8_REGEXP = /^#([\da-f]{8})$/i;
const RGB_REGEXP = /^rgba?\((.+)\)$/i;
const CSS_VAR_REGEXP = /^var\(\s*(--[\w-]+)\s*(?:,\s*(.+))?\)$/i;

function clampChannel(value: number) {
  return Math.min(255, Math.max(0, Math.round(value)));
}

function channelsToHex(channels: number[]) {
  const [r, g, b] = channels.map((item) => clampChannel(item));
  return (
    "#" +
    [r, g, b]
      .map((item) => {
        const hex = item.toString(16).toUpperCase();
        return hex.length === 1 ? `0${hex}` : hex;
      })
      .join("")
  );
}

function parseHexColor(color: string) {
  const hex3 = color.match(HEX_3_REGEXP);
  if (hex3) {
    return hex3[1].split("").map((item) => clampChannel(Number.parseInt(item.repeat(2), 16)));
  }

  const hex4 = color.match(HEX_4_REGEXP);
  if (hex4) {
    return hex4[1]
      .slice(0, 3)
      .split("")
      .map((item) => clampChannel(Number.parseInt(item.repeat(2), 16)));
  }

  const hex6 = color.match(HEX_6_REGEXP);
  if (hex6) {
    return [
      clampChannel(Number.parseInt(hex6[1].slice(0, 2), 16)),
      clampChannel(Number.parseInt(hex6[1].slice(2, 4), 16)),
      clampChannel(Number.parseInt(hex6[1].slice(4, 6), 16)),
    ];
  }

  const hex8 = color.match(HEX_8_REGEXP);
  if (hex8) {
    return [
      clampChannel(Number.parseInt(hex8[1].slice(0, 2), 16)),
      clampChannel(Number.parseInt(hex8[1].slice(2, 4), 16)),
      clampChannel(Number.parseInt(hex8[1].slice(4, 6), 16)),
    ];
  }

  return null;
}

function parseRgbColor(color: string) {
  const rgbMatch = color.match(RGB_REGEXP);
  if (!rgbMatch) {
    return null;
  }

  const normalized = rgbMatch[1]
    .replace(/\s*\/\s*[\d.]+%?\s*$/, "")
    .replace(/,/g, " ")
    .trim();
  const parts = normalized.split(/\s+/).slice(0, 3);
  if (parts.length < 3) {
    return null;
  }

  const values = parts.map((item) => Number.parseFloat(item));
  if (values.some((item) => !Number.isFinite(item))) {
    return null;
  }

  return values.map((item) => clampChannel(item));
}

function parseColorChannels(color?: string | null) {
  const normalized = color?.trim();
  if (!normalized) {
    return null;
  }

  return parseHexColor(normalized) || parseRgbColor(normalized);
}

function readCssVariable(name: string) {
  if (!isBrowser()) {
    return "";
  }
  return getComputedStyle(document.documentElement).getPropertyValue(name).trim();
}

function resolveCssVarColor(color?: string | null): string | null {
  const normalized = color?.trim();
  if (!normalized) {
    return null;
  }

  const match = normalized.match(CSS_VAR_REGEXP);
  if (!match) {
    return normalized;
  }

  const [, variableName, fallback] = match;
  const value = readCssVariable(variableName);
  if (value) {
    return value;
  }
  if (!fallback) {
    return null;
  }

  return resolveCssVarColor(fallback);
}

export function resolveThemeColor(color?: string | null, fallback = "#409EFF") {
  const resolvedColor = resolveCssVarColor(color);
  if (resolvedColor) {
    return resolvedColor;
  }
  const resolvedFallback = resolveCssVarColor(fallback);
  return resolvedFallback || fallback;
}

export function withAlpha(color?: string | null, alpha = 1, fallback = "#409EFF") {
  const channels = parseColorChannels(color) || parseColorChannels(fallback);
  if (!channels) {
    return fallback;
  }

  const safeAlpha = Math.min(1, Math.max(0, alpha));
  return `rgba(${channels[0]}, ${channels[1]}, ${channels[2]}, ${safeAlpha})`;
}

function mixChannels(base: number[], mixin: number[], amount: number) {
  const safeAmount = Math.min(1, Math.max(0, amount));
  return [
    base[0] * (1 - safeAmount) + mixin[0] * safeAmount,
    base[1] * (1 - safeAmount) + mixin[1] * safeAmount,
    base[2] * (1 - safeAmount) + mixin[2] * safeAmount,
  ].map((item) => clampChannel(item));
}

function isBrowser() {
  return typeof document !== "undefined" && typeof window !== "undefined";
}

const SYNC_THEME_VAR_NAMES = [
  "--el-bg-color",
  "--el-bg-color-page",
  "--el-bg-color-overlay",
  "--el-fill-color",
  "--el-fill-color-light",
  "--el-fill-color-lighter",
  "--el-fill-color-extra-light",
  "--el-fill-color-dark",
  "--el-fill-color-darker",
  "--el-text-color-primary",
  "--el-text-color-regular",
  "--el-text-color-secondary",
  "--el-text-color-placeholder",
  "--el-text-color-disabled",
  "--el-border-color",
  "--el-border-color-light",
  "--el-border-color-lighter",
  "--el-border-color-extra-light",
  "--el-border-color-dark",
  "--el-border-color-darker",
  "--el-mask-color",
  "--el-overlay-color",
  "--el-overlay-color-light",
  "--el-table-border-color",
  "--el-table-header-bg-color",
  "--el-table-tr-bg-color",
  "--el-table-row-hover-bg-color",
  "--el-table-current-row-bg-color",
  "--vxe-input-border-color",
  "--vxe-table-popup-border-color",
  "--vxe-table-border-color",
  "--vxe-table-header-background-color",
  "--vxe-table-body-background-color",
  "--vxe-table-footer-background-color",
  "--vxe-table-row-hover-background-color",
  "--vxe-table-row-current-background-color",
  "--vxe-table-row-hover-current-background-color",
  "--vxe-toolbar-background-color",
  "--vxe-pager-background-color",
  "--vxe-modal-header-background-color",
  "--vxe-modal-body-background-color",
  "--vxe-modal-border-color",
] as const;

function nextFrame() {
  return new Promise<void>((resolve) => {
    requestAnimationFrame(() => resolve());
  });
}

export function clearResolvedThemeVars() {
  if (!isBrowser()) return;

  const root = document.documentElement;
  SYNC_THEME_VAR_NAMES.forEach((name) => {
    root.style.removeProperty(name);
  });
}

export async function syncResolvedThemeVars() {
  if (!isBrowser()) return;

  const root = document.documentElement;
  await nextFrame();
  await nextFrame();

  const computedStyle = getComputedStyle(root);
  SYNC_THEME_VAR_NAMES.forEach((name) => {
    const value = computedStyle.getPropertyValue(name).trim();
    if (value) {
      root.style.setProperty(name, value);
    }
  });

  root.style.setProperty("--theme-update-trigger", Date.now().toString());
}

export function toggleDarkMode(isDark: boolean) {
  if (!isBrowser()) return;
  const startTime = performance.now();

  const root = document.documentElement;
  const nextTheme = isDark ? "dark" : "light";

  root.classList.toggle("dark", isDark);
  root.dataset.theme = nextTheme;
  root.style.colorScheme = nextTheme;

  if (document.body) {
    document.body.dataset.theme = nextTheme;
    document.body.style.colorScheme = nextTheme;
  }

  const duration = performance.now() - startTime;
  if (import.meta.env.DEV) {
    console.log(
      `[Theme] Dark mode toggle: ${isDark ? "dark" : "light"} - ${duration.toFixed(2)}ms`
    );
  }
}

export function toggleSidebarColor(isClassic: boolean, isDark = false) {
  if (!isBrowser()) return;
  const startTime = performance.now();
  const root = document.documentElement;
  const shouldUseClassic = !isDark && isClassic;
  root.classList.toggle("sidebar-color-classic", shouldUseClassic);

  const duration = performance.now() - startTime;
  if (import.meta.env.DEV) {
    console.log(
      `[Theme] Sidebar color toggle: ${shouldUseClassic ? "classic" : "minimal"} - ${duration.toFixed(2)}ms`
    );
  }
}

/**
 * 写入 Element Plus 主题变量，例如：--el-color-primary / --el-color-primary-light-3 等。
 */
export function applyTheme(colors: Record<string, string>) {
  if (!isBrowser()) return;

  const root = document.documentElement;
  Object.entries(colors).forEach(([key, value]) => {
    root.style.setProperty(`--el-color-${key}`, value);
  });

  root.style.setProperty("--theme-update-trigger", Date.now().toString());
}

/**
 * 生成 Element Plus 主题色的衍生色。
 *
 * 注意：这里的目标是稳定生成一组可用的主色渐变变量，不依赖外部库。
 */
export function generateThemeColors(primaryColor: string, mode: "LIGHT" | "DARK" | string) {
  const baseColor = resolveThemeColor(primaryColor);
  const baseChannels = parseColorChannels(baseColor) || [64, 158, 255]; // #409EFF
  const normalizedMode = String(mode).trim().toLowerCase();

  const white = [255, 255, 255];
  const black = [0, 0, 0];

  const colors: Record<string, string> = {
    primary: channelsToHex(baseChannels),
  };

  for (let i = 1; i <= 9; i += 1) {
    colors[`primary-light-${i}`] = channelsToHex(mixChannels(baseChannels, white, i / 10));
  }

  const darkAmount = normalizedMode === "dark" ? 0.3 : 0.2;
  colors["primary-dark-2"] = channelsToHex(mixChannels(baseChannels, black, darkAmount));

  return colors;
}
