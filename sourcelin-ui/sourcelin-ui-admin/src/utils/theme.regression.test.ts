/**
 * Theme Regression Tests
 *
 * 手动验证测试 - 在浏览器控制台执行以下测试用例
 */

import { resolveThemeColor, withAlpha, toggleDarkMode, toggleSidebarColor } from "./theme";

const TestResults: { passed: number; failed: number; logs: string[] } = {
  passed: 0,
  failed: 0,
  logs: [],
};

function logResult(name: string, passed: boolean, message = "") {
  if (passed) {
    TestResults.passed++;
    TestResults.logs.push(`[✓] ${name}`);
  } else {
    TestResults.failed++;
    TestResults.logs.push(`[✗] ${name}: ${message}`);
  }
}

function assertEqual(actual: unknown, expected: unknown, name: string) {
  const passed = actual === expected;
  logResult(name, passed, passed ? "" : `expected ${expected}, got ${actual}`);
  return passed;
}

function assertTruthy(value: unknown, name: string) {
  const passed = !!value;
  logResult(name, passed, passed ? "" : `expected truthy, got ${value}`);
  return passed;
}

console.log("=== 主题回归测试开始 ===\n");

// Test 1: resolveThemeColor 基础功能
const testColor = resolveThemeColor("#409EFF");
assertTruthy(testColor, "resolveThemeColor should resolve hex color");
assertEqual(testColor, "#409EFF", "resolveThemeColor should return resolved color");

// Test 2: resolveThemeColor with CSS variable fallback
const cssVarColor = resolveThemeColor("var(--el-color-primary)", "#FF0000");
assertTruthy(cssVarColor, "resolveThemeColor should handle CSS variable");

// Test 3: withAlpha 透明度处理
const rgbaColor = withAlpha("#409EFF", 0.5);
assertTruthy(rgbaColor, "withAlpha should generate rgba");
assertEqual(rgbaColor.includes("rgba"), true, "withAlpha should include rgba string");

// Test 4: 主题切换函数存在性
assertTruthy(typeof toggleDarkMode === "function", "toggleDarkMode should be a function");
assertTruthy(typeof toggleSidebarColor === "function", "toggleSidebarColor should be a function");

// Test 5: Dark 模式切换
toggleDarkMode(false);
assertEqual(
  document.documentElement.classList.contains("dark"),
  false,
  "Dark mode should be disabled"
);

toggleDarkMode(true);
assertEqual(
  document.documentElement.classList.contains("dark"),
  true,
  "Dark mode should be enabled after toggle"
);

toggleDarkMode(false);
assertEqual(
  document.documentElement.classList.contains("dark"),
  false,
  "Dark mode should be disabled after toggle back"
);

// Test 6: Sidebar 颜色切换（亮色下才生效）
toggleDarkMode(false);
toggleSidebarColor(false, false); // 极简白
const isClassicAfterFalse = document.documentElement.classList.contains("sidebar-color-classic");
assertEqual(isClassicAfterFalse, false, "Sidebar should not be classic after false");

toggleSidebarColor(true, false); // 经典蓝
const isClassicAfterTrue = document.documentElement.classList.contains("sidebar-color-classic");
assertEqual(isClassicAfterTrue, true, "Sidebar should be classic after true");

toggleSidebarColor(false, false); // 恢复极简白
const isClassicReset = document.documentElement.classList.contains("sidebar-color-classic");
assertEqual(isClassicReset, false, "Sidebar should be reset to minimal");

// Test 7: 暗色主题忽略侧边栏风格
toggleDarkMode(true);
toggleSidebarColor(true, true);
const isClassicIgnoredInDark = document.documentElement.classList.contains("sidebar-color-classic");
assertEqual(isClassicIgnoredInDark, false, "Sidebar classic style should be ignored in dark theme");
toggleDarkMode(false);

// Test 8: CSS 变量验证
function checkCssVariable(name: string): boolean {
  const value = getComputedStyle(document.documentElement).getPropertyValue(name).trim();
  return value.length > 0;
}

// 关键 CSS 变量应该存在
const criticalVars = [
  "--menu-background",
  "--menu-text",
  "--menu-active-text",
  "--sidebar-logo-background",
  "--sidebar-logo-text-color",
];

criticalVars.forEach((varName) => {
  const exists = checkCssVariable(varName);
  logResult(
    `CSS variable ${varName} should be defined`,
    exists,
    exists ? "" : `${varName} is empty or missing`
  );
});

console.log("\n=== 测试结果 ===");
console.log(`通过: ${TestResults.passed} | 失败: ${TestResults.failed}`);
console.log("\n详细日志:");
TestResults.logs.forEach((log) => console.log(log));
