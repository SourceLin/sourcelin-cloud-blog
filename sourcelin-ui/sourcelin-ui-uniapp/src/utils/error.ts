// src/utils/error.ts
// 业务错误统一类型与提示策略
// 与 rules/api-contract.md 对齐：成功码为 0

export type { ApiResponse, PageResult } from '@/shared/types/api';

/** 业务错误：HTTP 200 但 code !== 0，或 HTTP 非 2xx */
export class BusinessError extends Error {
  code: number;
  requestId?: string;
  data?: unknown;
  httpStatus?: number;

  constructor(params: {
    code: number;
    message?: string;
    requestId?: string;
    data?: unknown;
    httpStatus?: number;
  }) {
    super(params.message || '请求失败');
    this.name = 'BusinessError';
    this.code = params.code;
    this.requestId = params.requestId;
    this.data = params.data;
    this.httpStatus = params.httpStatus;
  }
}

/** 网络异常：无法连通、超时、被中断 */
export class NetworkError extends Error {
  raw?: unknown;
  constructor(message?: string, raw?: unknown) {
    super(message || '网络异常，请稍后再试');
    this.name = 'NetworkError';
    this.raw = raw;
  }
}

/**
 * 默认错误提示策略
 * - 401：交由 request 层做登录引导，不在此弹窗
 * - 403：权限不足
 * - 429：限流
 * - 5xx：服务异常
 */
export function toastError(err: unknown): void {
  if (!err) return;
  let title = '';
  if (err instanceof NetworkError) {
    title = err.message || '网络异常';
  } else if (err instanceof BusinessError) {
    if (err.httpStatus === 401) return;
    if (err.httpStatus === 403) title = '没有权限执行该操作';
    else if (err.httpStatus === 429) title = '操作过于频繁，请稍后再试';
    else if (typeof err.httpStatus === 'number' && err.httpStatus >= 500) title = '服务异常，请稍后再试';
    else title = err.message || '请求失败';
  } else if (err instanceof Error) {
    title = err.message || '操作失败';
  }
  if (!title) return;
  showInfoToast(title, 2000);
}
import { showInfoToast } from './feedback';
