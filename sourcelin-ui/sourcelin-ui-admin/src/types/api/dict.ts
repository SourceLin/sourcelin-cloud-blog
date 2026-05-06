/**
 * Dict 字典类型定义
 */

import type { BaseQueryParams } from "./common";

/** 字典分页查询参数 */

export interface DictTypeQueryParams extends BaseQueryParams {
  /** 字典名称 */
  dictName?: string;
  /** 字典类型 */
  dictType?: string;
  /** 字典状态（0:正常;1:停用） */
  status?: string;
}

/** 字典分页对象 */
export interface DictTypeItem {
  /** 字典ID */
  dictId: string;
  /** 字典名称 */
  dictName: string;
  /** 字典类型 */
  dictType: string;
  /** 状态（0:正常;1:停用） */
  status: string;
  /** 备注 */
  remark?: string;
  /** 创建时间 */
  createTime?: string;
}

/** 字典表单对象 */
export interface DictTypeForm {
  /** 字典ID */
  dictId?: string;
  /** 字典名称 */
  dictName?: string;
  /** 字典类型 */
  dictType?: string;
  /** 状态（0:正常;1:停用） */
  status?: string;
  /** 备注 */
  remark?: string;
}

/** 字典项分页查询参数 */
export interface DictItemQueryParams extends BaseQueryParams {
  /** 字典标签 */
  dictLabel?: string;
  /** 字典类型 */
  dictType?: string;
  /** 字典状态（0:正常;1:停用） */
  status?: string;
}

/** 字典项分页对象 */
export interface DictItem {
  /** 字典编码（主键） */
  dictCode: string;
  /** 字典类型 */
  dictType: string;
  /** 字典项标签 */
  dictLabel: string;
  /** 字典项值 */
  dictValue: string;
  /** 状态（0:正常;1:停用） */
  status: string;
  /** 排序 */
  dictSort?: number;
  /** 标签样式编码（N/P/S/W/I/D） */
  listClass?: "N" | "P" | "S" | "W" | "I" | "D";
  /** 是否默认（Y/N） */
  isDefault?: "Y" | "N";
  /** 备注 */
  remark?: string;
}

/** 字典项表单对象 */
export interface DictItemForm {
  /** 字典编码（主键） */
  dictCode?: string;
  /** 字典类型 */
  dictType?: string;
  /** 字典标签 */
  dictLabel?: string;
  /** 字典项值 */
  dictValue?: string;
  /** 状态（0:正常;1:停用） */
  status?: string;
  /** 排序 */
  dictSort?: number;
  /** 标签样式编码（N/P/S/W/I/D） */
  listClass?: "N" | "P" | "S" | "W" | "I" | "D";
  /** 是否默认（Y/N） */
  isDefault?: "Y" | "N";
  /** 备注 */
  remark?: string;
}

/** 字典项选项 */
export interface DictItemOption {
  /** 字典项值 */
  value: number | string;
  /** 字典项标签 */
  label: string;
  /** 标签样式编码（N/P/S/W/I/D） */
  listClass?: "N" | "P" | "S" | "W" | "I" | "D";
  /** Element 标签类型（展示衍生值） */
  tagType?: "primary" | "success" | "warning" | "info" | "danger";
}
