/**
 * Menu 菜单类型定义
 */

/** 菜单查询参数 */
export interface MenuQueryParams {
  /** 搜索关键字 */
  keywords?: string;
}

/** 菜单视图对象 */
export interface MenuItem {
  /** 子菜单 */
  children?: MenuItem[];
  /** 组件路径 */
  component?: string;
  /** ICON */
  icon?: string;
  /** 菜单ID */
  menuId?: string;
  /** 菜单名称 */
  menuName?: string;
  /** 父菜单ID */
  parentId?: string;
  /** 菜单类型（M-目录 C-菜单 F-按钮） */
  menuType?: string;
  /** 排序 */
  orderNum?: number;
  /** 路由路径 */
  path?: string;
  /** 路由名称 */
  routeName?: string;
  /** 跳转路径 */
  redirect?: string;
  /** 按钮权限标识 */
  perms?: string;
  /** 是否外链（0是 1否） */
  isFrame?: string;
  /** 是否缓存（0缓存 1不缓存） */
  isCache?: string;
  /** 菜单是否可见(1:显示;0:隐藏) */
  visible?: string;
  /** 菜单状态(0:正常;1:停用) */
  status?: string;
  /** 菜单范围(1=平台 2=业务) */
  scope?: number;
  /** 路由参数 JSON（对应 sys_menu.query） */
  query?: string;
}

/** 菜单表单对象 */
export interface MenuForm {
  /** 菜单ID */
  menuId?: string;
  /** 父菜单ID */
  parentId?: string;
  /** 菜单名称 */
  menuName?: string;
  /** 菜单类型（M-目录 C-菜单 F-按钮） */
  menuType?: string;
  /** 路由路径 */
  path?: string;
  /** 路由名称（用于前端路由名） */
  routeName?: string;
  /** 跳转路径 */
  redirect?: string;
  /** 组件路径 */
  component?: string;
  /** ICON */
  icon?: string;
  /** 排序 */
  orderNum?: number;
  /** 菜单是否可见 */
  visible?: string;
  /** 菜单状态 */
  status?: string;
  /** 菜单范围(1=平台 2=业务) */
  scope?: number;
  /** 按钮权限标识 */
  perms?: string;
  /** 是否外链（0是 1否） */
  isFrame?: string;
  /** 是否缓存（0缓存 1不缓存） */
  isCache?: string;
  /** 路由参数（用于表单编辑；提交时序列化为 query） */
  params?: { key?: string; value?: string }[];
  /** 路由参数 JSON（后端 sys_menu.query，与 params 二选一维护即可） */
  query?: string;
  /** 是否始终显示（仅对目录生效） */
  alwaysShow?: string;
  /** 是否缓存（仅用于页面切换态） */
  keepAlive?: number | boolean;
}

/** 菜单选项 */
export interface MenuOption {
  key: string;
  value: string;
}

/** 路由对象 */
export interface RouteItem {
  /** 子路由列表 */
  children?: RouteItem[];
  /** 组件路径 */
  component?: string;
  /** 路由名称 */
  name?: string;
  /** 路由路径 */
  path?: string;
  /** 后端 RouterVo.query，JSON 字符串 */
  query?: string;
  /** 路由属性 */
  meta?: Meta;
  /** 跳转链接 */
  redirect?: string;
}

/** 路由属性 */
export interface Meta {
  /** 【目录】只有一个子路由是否始终显示 */
  alwaysShow?: boolean;
  /** 是否隐藏(true-是 false-否) */
  hidden?: boolean;
  /** ICON */
  icon?: string;
  /** 【菜单】是否开启页面缓存 */
  keepAlive?: boolean;
  /** 路由参数 */
  params?: Record<string, any>;
  /** 角色集合 */
  roles?: string[];
  /** 路由title */
  title?: string;
}
