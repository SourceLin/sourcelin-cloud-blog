/**
 * Vue Router 类型扩展
 */
import "vue-router";

declare module "vue-router" {
  interface RouteMeta {
    title?: string;
    icon?: string;
    hidden?: boolean;
    alwaysShow?: boolean;
    affix?: boolean;
    keepAlive?: boolean;
    breadcrumb?: boolean;
    activeMenu?: string;
    /** 与后端 RouterVo.query JSON 合并后的路由参数（如嵌入页 url） */
    params?: Record<string, unknown>;
  }
}
