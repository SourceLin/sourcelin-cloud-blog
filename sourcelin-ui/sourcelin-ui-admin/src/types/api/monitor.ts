/**
 * Monitor 系统监控类型定义
 */

import type { BaseQueryParams } from "./common";

/** 在线用户查询参数 */
export interface OnlineUserQueryParams extends BaseQueryParams {
  ipaddr?: string;
  userName?: string;
  loginType?: string;
}

/** 在线用户对象 */
export interface OnlineUserItem {
  loginType?: string;
  tokenId: string;
  userName?: string;
  ipaddr?: string;
  loginLocation?: string;
  browser?: string;
  os?: string;
  loginTime?: number;
}

/** 定时任务查询参数 */
export interface JobQueryParams extends BaseQueryParams {
  jobName?: string;
  jobGroup?: string;
  status?: string;
}

/** 定时任务对象 */
export interface JobItem {
  jobId?: number;
  jobName?: string;
  jobGroup?: string;
  invokeTarget?: string;
  cronExpression?: string;
  misfirePolicy?: string;
  concurrent?: string;
  status?: string;
  createTime?: string;
  nextValidTime?: string;
  remark?: string;
}

/** 定时任务表单 */
export interface JobForm {
  jobId?: number;
  jobName?: string;
  jobGroup?: string;
  invokeTarget?: string;
  cronExpression?: string;
  misfirePolicy?: string;
  concurrent?: string;
  status?: string;
  remark?: string;
}

/** 定时任务日志查询参数 */
export interface JobLogQueryParams extends BaseQueryParams {
  jobName?: string;
  jobGroup?: string;
  status?: string;
  createTime?: [string, string];
}

/** 定时任务日志对象 */
export interface JobLogItem {
  jobLogId: number;
  jobName?: string;
  jobGroup?: string;
  invokeTarget?: string;
  jobMessage?: string;
  status?: string;
  exceptionInfo?: string;
  startTime?: string;
  endTime?: string;
  createTime?: string;
}
