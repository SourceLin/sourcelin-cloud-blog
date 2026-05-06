/**
 * User 用户类型定义
 */

import type { BaseQueryParams } from "./common";

/** 登录用户信息 */
export interface UserInfo {
  /** 用户ID */
  userId?: string;
  /** 用户名（canonical） */
  userName?: string;
  /** 用户昵称（canonical） */
  nickName?: string;
  /** 头像URL */
  avatar?: string;
  /** 角色集合 */
  roles: string[];
  /** 权限集合 */
  perms: string[];
}

/** 用户分页查询参数 */
export interface UserQueryParams extends BaseQueryParams {
  /** 用户名 */
  userName?: string;
  /** 手机号 */
  phonenumber?: string;
  /** 用户状态：0=正常,1=停用 */
  status?: string;
  /** 部门ID */
  deptId?: string;
  /** 创建时间 */
  createTime?: [string, string];
}

/** 用户分页对象 */
export interface UserItem {
  /** 用户ID */
  userId: string;
  /** 用户名 */
  userName: string;
  /** 用户昵称 */
  nickName?: string;
  /** 用户头像地址 */
  avatar?: string;
  /** 创建时间 */
  createTime?: string;
  /** 部门名称 */
  deptName?: string;
  /** 用户邮箱 */
  email?: string;
  /** 性别（0-男 1-女 2-未知） */
  sex?: string;
  /** 手机号 */
  phonenumber?: string;
  /** 角色名称（逗号拼接） */
  roleNames?: string;
  /** 用户状态（0:正常;1:停用） */
  status?: string;
}

/** 用户表单对象 */
export interface UserForm {
  /** 用户ID */
  userId?: string;
  /** 用户名 */
  userName?: string;
  /** 用户昵称 */
  nickName?: string;
  /** 初始密码（新增时使用） */
  password?: string;
  /** 用户头像 */
  avatar?: string;
  /** 部门ID */
  deptId?: string;
  /** 用户邮箱 */
  email?: string;
  /** 性别 */
  sex?: string;
  /** 手机号 */
  phonenumber?: string;
  /** 角色ID集合 */
  roleIds?: number[];
  /** 岗位ID集合 */
  postIds?: number[];
  /** 用户状态(0:正常;1:停用) */
  status?: string;
  /** 备注 */
  remark?: string;
}

/** 个人中心用户信息 */
export interface UserProfileDetail {
  /** 用户ID */
  userId?: string;
  /** 用户名 */
  userName?: string;
  /** 用户昵称 */
  nickName?: string;
  /** 头像URL */
  avatar?: string;
  /** 性别 */
  sex?: string;
  /** 手机号 */
  phonenumber?: string;
  /** 邮箱 */
  email?: string;
  /** 部门名称 */
  deptName?: string;
  /** 角色名称 */
  roleNames?: string;
  /** 创建时间 */
  createTime?: string;
}

/** 个人中心用户信息表单 */
export interface UserProfileForm {
  /** 用户昵称 */
  nickName?: string;
  /** 头像URL */
  avatar?: string;
  /** 性别 */
  sex?: string;
}

/** 修改密码表单 */
export interface PasswordChangeForm {
  /** 原密码 */
  oldPassword?: string;
  /** 新密码 */
  newPassword?: string;
  /** 确认新密码 */
  confirmPassword?: string;
}

/** 密码校验表单 */
export interface PasswordVerifyForm {
  /** 当前密码 */
  password?: string;
}

/** 修改手机表单 */
export interface MobileUpdateForm {
  /** 手机号 */
  mobile?: string;
  /** 验证码 */
  code?: string;
  /** 当前密码 */
  password?: string;
}

/** 修改邮箱表单 */
export interface EmailUpdateForm {
  /** 邮箱 */
  email?: string;
  /** 验证码 */
  code?: string;
  /** 当前密码 */
  password?: string;
}
