import * as ElementPlusIconsVue from "@element-plus/icons-vue";

const elementPlusIconNameSet = new Set(Object.keys(ElementPlusIconsVue));

/**
 * 判断图标名称是否为 Element Plus 图标组件名
 */
export function isElementPlusIconName(icon?: string | null): icon is string {
  return Boolean(icon) && elementPlusIconNameSet.has(icon as string);
}
