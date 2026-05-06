import request from "@/utils/request";
import type { RoleQueryParams, RoleItem, RoleForm, OptionItem } from "@/types/api";

const ROLE_BASE_URL = "/system/role";

const DATA_SCOPE_LABEL: Record<string, string> = {
  "1": "全部数据",
  "2": "部门及子部门数据",
  "3": "本部门数据",
  "4": "仅本人数据",
  "5": "自定义数据",
};

const toStringValue = (value: unknown): string | undefined => {
  if (value === null || value === undefined || value === "") {
    return undefined;
  }
  return String(value);
};

function mapRoleItem(item: Record<string, any>): RoleItem {
  const dataScope = toStringValue(item.dataScope);
  return {
    roleId: toStringValue(item.roleId),
    roleName: item.roleName,
    roleKey: item.roleKey,
    roleSort: item.roleSort != null ? Number(item.roleSort) : undefined,
    status: toStringValue(item.status),
    dataScope,
    dataScopeLabel: dataScope ? DATA_SCOPE_LABEL[dataScope] : undefined,
    updateTime: item.updateTime,
  };
}

function buildRolePayload(data: RoleForm) {
  return {
    roleId: data.roleId,
    roleName: data.roleName,
    roleKey: data.roleKey,
    roleSort: data.roleSort,
    dataScope: data.dataScope,
    deptIds: data.deptIds?.map((deptId) => Number(deptId)),
    menuIds: data.menuIds?.map((menuId) => Number(menuId)),
    status: data.status,
    remark: data.remark,
  };
}

const RoleAPI = {
  /** 获取角色分页数据 */
  async getPage(queryParams?: RoleQueryParams) {
    const data = await request<any, PageResult<Record<string, any>>>({
      url: `${ROLE_BASE_URL}/list`,
      method: "get",
      params: {
        page: queryParams?.page ?? 1,
        pageSize: queryParams?.pageSize ?? 10,
        roleName: queryParams?.keywords,
      },
    });
    const items = (data.items ?? []).map(mapRoleItem);
    return {
      ...data,
      items,
    } as PageResult<RoleItem>;
  },

  /** 获取角色下拉数据源 */
  async getOptions() {
    const list = await request<any, Array<Record<string, any>>>({
      url: `${ROLE_BASE_URL}/optionselect`,
      method: "get",
    });
    return list.map((item) => ({
      value: String(item.roleId),
      label: item.roleName,
    })) as OptionItem[];
  },

  /** 获取角色的菜单ID集合 */
  async getRoleMenuIds(roleId: string) {
    const data = await request<any, { checkedKeys?: Array<string | number> }>({
      url: `/system/menu/roleMenuTreeselect/${roleId}`,
      method: "get",
    });
    return (data.checkedKeys ?? []).map((id) => String(id));
  },

  /** 分配菜单权限 */
  async updateRoleMenus(roleId: string, menuIds: string[]) {
    const roleInfo = await request<any, Record<string, any>>({
      url: `${ROLE_BASE_URL}/${roleId}`,
      method: "get",
    });
    return request({
      url: `${ROLE_BASE_URL}`,
      method: "put",
      data: {
        ...roleInfo,
        roleId: Number(roleId),
        menuIds: menuIds.map((menuId) => Number(menuId)),
      },
    });
  },

  /** 获取角色表单数据 */
  async getFormData(roleId: string) {
    const [roleInfo, deptTree] = await Promise.all([
      request<any, Record<string, any>>({ url: `${ROLE_BASE_URL}/${roleId}`, method: "get" }),
      request<any, { checkedKeys?: Array<string | number> }>({
        url: `${ROLE_BASE_URL}/deptTree/${roleId}`,
        method: "get",
      }),
    ]);

    return {
      roleId: toStringValue(roleInfo.roleId),
      roleName: roleInfo.roleName,
      roleKey: roleInfo.roleKey,
      roleSort: roleInfo.roleSort != null ? Number(roleInfo.roleSort) : undefined,
      dataScope: toStringValue(roleInfo.dataScope),
      deptIds: (deptTree.checkedKeys ?? []).map((deptId) => String(deptId)),
      menuIds: Array.isArray(roleInfo.menuIds)
        ? roleInfo.menuIds.map((menuId: number | string) => String(menuId))
        : undefined,
      status: toStringValue(roleInfo.status),
      remark: roleInfo.remark,
    } as RoleForm;
  },

  /** 获取角色的部门ID集合(自定义数据权限) */
  async getRoleDeptIds(roleId: string) {
    const data = await request<any, { checkedKeys?: Array<string | number> }>({
      url: `${ROLE_BASE_URL}/deptTree/${roleId}`,
      method: "get",
    });
    return (data.checkedKeys ?? []).map((id) => String(id));
  },

  /** 新增角色 */
  create(data: RoleForm) {
    return request({ url: `${ROLE_BASE_URL}`, method: "post", data: buildRolePayload(data) });
  },

  /** 更新角色 */
  update(roleId: string, data: RoleForm) {
    return request({
      url: `${ROLE_BASE_URL}`,
      method: "put",
      data: {
        ...buildRolePayload(data),
        roleId: Number(roleId),
      },
    });
  },

  /** 批量删除角色，多个以英文逗号(,)分割 */
  deleteByIds(ids: string) {
    return request({ url: `${ROLE_BASE_URL}/${ids}`, method: "delete" });
  },
};

export default RoleAPI;
