/**
 * Dept 部门类型定义
 */

/** 部门查询参数 */
export interface DeptQueryParams {
  /** 部门名称 */
  deptName?: string;
  /** 部门状态（0:正常;1:停用） */
  status?: string;
}

/** 部门视图对象 */
export interface DeptItem {
  /** 子部门 */
  children?: DeptItem[];
  /** 创建时间 */
  createTime?: string;
  /** 部门ID */
  deptId?: string;
  /** 部门名称 */
  deptName?: string;
  /** 父部门ID */
  parentId?: string;
  /** 排序 */
  orderNum?: number;
  /** 部门状态（0:正常;1:停用） */
  status?: string;
  /** 父节点ID路径 */
  ancestors?: string;
  /** 负责人 */
  leader?: string;
  /** 联系电话 */
  phone?: string;
  /** 邮箱 */
  email?: string;
  /** 修改时间 */
  updateTime?: string;
}

/** 部门表单对象 */
export interface DeptForm {
  /** 部门ID */
  deptId?: string;
  /** 部门名称 */
  deptName?: string;
  /** 父部门ID */
  parentId?: string;
  /** 排序 */
  orderNum?: number;
  /** 部门状态（0:正常;1:停用） */
  status?: string;
  /** 负责人 */
  leader?: string;
  /** 联系电话 */
  phone?: string;
  /** 邮箱 */
  email?: string;
  /** 备注 */
  remark?: string;
}
