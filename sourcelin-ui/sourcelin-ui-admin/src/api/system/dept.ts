import request from "@/utils/request";
import type { DeptQueryParams, DeptItem, DeptForm, OptionItem } from "@/types/api";

const DEPT_BASE_URL = "/system/dept";

function normalizeDept(item: Record<string, any>): DeptItem {
  return {
    deptId: item.deptId != null ? String(item.deptId) : undefined,
    deptName: item.deptName,
    parentId: item.parentId != null ? String(item.parentId) : undefined,
    orderNum: item.orderNum != null ? Number(item.orderNum) : undefined,
    status: item.status != null ? String(item.status) : undefined,
    ancestors: item.ancestors,
    leader: item.leader,
    phone: item.phone,
    email: item.email,
    createTime: item.createTime,
    updateTime: item.updateTime,
    children: Array.isArray(item.children) ? item.children.map(normalizeDept) : undefined,
  };
}

function normalizeDeptForm(item: Record<string, any>): DeptForm {
  return {
    deptId: item.deptId != null ? String(item.deptId) : undefined,
    deptName: item.deptName,
    parentId: item.parentId != null ? String(item.parentId) : "0",
    orderNum: item.orderNum != null ? Number(item.orderNum) : 0,
    status: item.status != null ? String(item.status) : "0",
    leader: item.leader,
    phone: item.phone,
    email: item.email,
    remark: item.remark,
  };
}

function buildDeptPayload(data: DeptForm) {
  return {
    deptId: data.deptId ? Number(data.deptId) : undefined,
    parentId: data.parentId ? Number(data.parentId) : 0,
    deptName: data.deptName,
    orderNum: data.orderNum,
    status: data.status,
    leader: data.leader,
    phone: data.phone,
    email: data.email,
    remark: data.remark,
  };
}

function transformDeptOptions(nodes: DeptItem[]): OptionItem[] {
  return nodes.map((node) => ({
    value: node.deptId ?? "",
    label: node.deptName ?? "",
    children: Array.isArray(node.children) ? transformDeptOptions(node.children) : undefined,
  }));
}

const DeptAPI = {
  /** 获取部门树形列表 */
  async getList(queryParams?: DeptQueryParams) {
    const list = await request<any, Array<Record<string, any>>>({
      url: `${DEPT_BASE_URL}/list`,
      method: "get",
      params: {
        deptName: queryParams?.deptName,
        status: queryParams?.status,
      },
    });
    return list.map(normalizeDept);
  },

  /** 获取部门下拉数据源 */
  async getOptions() {
    const list = await this.getList();
    return transformDeptOptions(list);
  },

  /** 获取部门表单数据 */
  async getFormData(deptId: string) {
    const data = await request<any, Record<string, any>>({
      url: `${DEPT_BASE_URL}/${deptId}`,
      method: "get",
    });
    return normalizeDeptForm(data);
  },

  /** 新增部门 */
  create(data: DeptForm) {
    return request({
      url: `${DEPT_BASE_URL}`,
      method: "post",
      data: buildDeptPayload(data),
    });
  },

  /** 修改部门 */
  update(deptId: string, data: DeptForm) {
    return request({
      url: `${DEPT_BASE_URL}`,
      method: "put",
      data: {
        ...buildDeptPayload(data),
        deptId: Number(deptId),
      },
    });
  },

  /** 批量删除部门，多个以英文逗号(,)分割 */
  async deleteByIds(ids: string) {
    const deptIds = ids
      .split(",")
      .map((id) => id.trim())
      .filter(Boolean);
    await Promise.all(
      deptIds.map((deptId) =>
        request({
          url: `${DEPT_BASE_URL}/${deptId}`,
          method: "delete",
        })
      )
    );
  },
};

export default DeptAPI;
