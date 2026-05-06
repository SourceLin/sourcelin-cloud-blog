/**
 * 通用 API 类型定义
 */

/** API 响应结构 */
export interface ApiResponse<T = unknown> {
  /** 响应码 */
  code: number;
  /** 响应消息 */
  message: string;
  /** 响应数据 */
  data: T;
  /** 请求追踪ID */
  requestId?: string;
  /** 响应时间 */
  timestamp?: string;
}

/** 基础查询参数 */
export interface BaseQueryParams {
  /** 页码（统一字段） */
  page: number;
  /** 每页记录数 */
  pageSize: number;

  /** 排序字段 */
  sortBy?: string;

  /** 排序方式（统一字段：asc/desc） */
  sortOrder?: string;
}

/** 分页数据结构（仅分页接口） */
export interface PageResult<T> {
  /** 数据列表（统一字段） */
  items: T[];
  /** 总记录数 */
  total: number;
  /** 当前页码 */
  page: number;
  /** 每页记录数 */
  pageSize: number;
  /** 总页数 */
  totalPages: number;
}

/** ID 响应结构 */
export interface IdResponse {
  /** 资源 ID */
  id: number;
}

/** 下拉选项 */
export interface OptionItem {
  /** 选项值 */
  value: string | number;
  /** 选项标签 */
  label: string;
  /** 子选项 */
  children?: OptionItem[];
}

/** Excel 导入结果 */
export interface ExcelResult {
  /** 响应码 */
  code: number;
  /** 无效数据数量 */
  invalidCount: number;
  /** 有效数据数量 */
  validCount: number;
  /** 错误信息列表 */
  messageList: string[];
}
