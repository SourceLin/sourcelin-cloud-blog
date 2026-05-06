import request from "@/utils/request";
import type { MenuQueryParams, MenuItem, MenuForm, RouteItem, OptionItem } from "@/types/api";

const MENU_BASE_URL = "/system/menu";

function toStringValue(value: unknown): string | undefined {
  if (value === null || value === undefined || value === "") {
    return undefined;
  }
  return String(value);
}

function transformTreeOptions(nodes: Array<Record<string, any>>): OptionItem[] {
  return nodes.map((node) => ({
    value: String(node.id ?? node.value),
    label: node.label,
    children: Array.isArray(node.children) ? transformTreeOptions(node.children) : undefined,
  }));
}

function mapMenuItem(item: Record<string, any>): MenuItem {
  return {
    menuId: toStringValue(item.menuId),
    menuName: item.menuName,
    parentId: toStringValue(item.parentId),
    path: item.path,
    routeName: item.routeName,
    component: item.component,
    query: item.query != null && item.query !== "" ? String(item.query) : undefined,
    icon: item.icon,
    orderNum: item.orderNum != null ? Number(item.orderNum) : undefined,
    menuType: item.menuType,
    visible: toStringValue(item.visible),
    status: toStringValue(item.status),
    perms: item.perms,
    isFrame: toStringValue(item.isFrame),
    isCache: toStringValue(item.isCache),
    redirect: item.redirect,
    children: Array.isArray(item.children) ? item.children.map(mapMenuItem) : undefined,
  };
}

/** 将表单键值对序列化为 sys_menu.query（JSON 字符串） */
function serializeMenuQuery(data: MenuForm): string | undefined {
  const rows = data.params?.filter((p) => p.key?.trim());
  if (rows?.length) {
    const obj: Record<string, string> = {};
    for (const row of rows) {
      const k = row.key!.trim();
      obj[k] = row.value ?? "";
    }
    return JSON.stringify(obj);
  }
  if (data.query?.trim()) {
    return data.query.trim();
  }
  return undefined;
}

function buildMenuPayload(data: MenuForm) {
  const keepAliveCacheFlag =
    typeof data.keepAlive === "boolean"
      ? data.keepAlive
        ? "0"
        : "1"
      : data.keepAlive != null
        ? String(data.keepAlive)
        : undefined;

  const isFrame =
    data.isFrame ??
    (data.path && /^https?:\/\//.test(data.path)
      ? "0" // 外链
      : "1"); // 内链

  const query = serializeMenuQuery(data);

  return {
    menuId: data.menuId ? Number(data.menuId) : undefined,
    parentId: data.parentId ? Number(data.parentId) : 0,
    menuName: data.menuName,
    menuType: data.menuType,
    path: data.path,
    routeName: data.routeName,
    component: data.component,
    icon: data.icon,
    orderNum: data.orderNum,
    visible: data.visible ?? "0",
    status: data.status ?? "0",
    perms: data.perms,
    isCache: keepAliveCacheFlag ?? data.isCache ?? "0",
    isFrame,
    ...(query !== undefined ? { query } : {}),
  };
}

const MenuAPI = {
  /** 获取当前用户的路由列表 */
  getRoutes() {
    return request<any, RouteItem[]>({ url: `${MENU_BASE_URL}/getRouters`, method: "get" });
  },

  /** 获取菜单树形列表 */
  async getList(queryParams: MenuQueryParams) {
    const data = await request<any, Array<Record<string, any>>>({
      url: `${MENU_BASE_URL}/list`,
      method: "get",
      params: {
        menuName: queryParams.keywords,
      },
    });
    return data.map(mapMenuItem);
  },

  /** 获取菜单下拉数据源 */
  async getOptions() {
    const data = await request<any, Array<Record<string, any>>>({
      url: `${MENU_BASE_URL}/treeselect`,
      method: "get",
    });
    return transformTreeOptions(data);
  },

  /** 获取菜单表单数据 */
  async getFormData(menuId: string) {
    const data = await request<any, Record<string, any>>({
      url: `${MENU_BASE_URL}/${menuId}`,
      method: "get",
    });
    return mapMenuItem(data) as MenuForm;
  },

  /** 新增菜单 */
  create(data: MenuForm) {
    return request({ url: `${MENU_BASE_URL}`, method: "post", data: buildMenuPayload(data) });
  },

  /** 修改菜单 */
  update(menuId: string, data: MenuForm) {
    return request({
      url: `${MENU_BASE_URL}`,
      method: "put",
      data: {
        ...buildMenuPayload(data),
        menuId: Number(menuId),
      },
    });
  },

  /** 删除菜单 */
  deleteById(menuId: string) {
    return request({ url: `${MENU_BASE_URL}/${menuId}`, method: "delete" });
  },
};

export default MenuAPI;
