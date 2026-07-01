// src/utils/request.ts
// 统一网络请求层（Uniapp 跨端）
// 设计目标：
//   1. 与后端 ApiResponse<T> 契约严格对齐（rules/api-contract.md）
//   2. 提供请求/响应拦截、错误统一处理
//   3. 支持 Token 自动注入、401 登录引导、可中断请求
//   4. 业务页面/composables 禁止直连 uni.request / uni.uploadFile

import { resolveUrl } from '@/config/env';
import { buildAuthHeader, handle401, type PendingAction } from './auth';
import {
  BusinessError,
  NetworkError,
  toastError,
  type ApiResponse
} from './error';

const SUCCESS_CODE = 0;

export type Method = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH' | 'OPTIONS' | 'HEAD';

export interface RequestConfig<TData = unknown> {
  url: string;
  method?: Method;
  /** query 参数 */
  params?: Record<string, unknown>;
  /** body */
  data?: TData;
  header?: Record<string, string>;
  timeout?: number;
  /** 是否注入 Token，默认 true */
  isToken?: boolean;
  /** 是否跳过默认错误 toast */
  skipErrorToast?: boolean;
  /** 401 时是否跳过登录引导 */
  skipAuthRedirect?: boolean;
  /** 是否显示 loading */
  showLoading?: boolean;
  loadingText?: string;
  /** 直接返回原始响应（不解 ApiResponse） */
  raw?: boolean;
  /** 401 后续做动作 */
  pendingAction?: PendingAction;
  /** 取消信号 */
  signal?: AbortSignal;
}

export interface ResponseShape<T = unknown> {
  data: ApiResponse<T> | T;
  statusCode: number;
  header?: Record<string, string>;
}

/**
 * 简易拦截器链
 */
type Interceptor<T> = {
  fulfilled?: (value: T) => T | Promise<T>;
  rejected?: (err: unknown) => unknown;
};

class InterceptorManager<T> {
  private handlers: (Interceptor<T> | null)[] = [];

  use(fulfilled?: Interceptor<T>['fulfilled'], rejected?: Interceptor<T>['rejected']): number {
    this.handlers.push({ fulfilled, rejected });
    return this.handlers.length - 1;
  }

  eject(id: number): void {
    if (this.handlers[id]) this.handlers[id] = null;
  }

  forEach(fn: (h: Interceptor<T>) => void): void {
    this.handlers.forEach((h) => h && fn(h));
  }
}

const requestInterceptors = new InterceptorManager<RequestConfig>();
const responseInterceptors = new InterceptorManager<{
  res: ResponseShape;
  config: RequestConfig;
}>();

/**
 * 请求拦截器：注入 baseURL、Token、公共 header
 */
requestInterceptors.use((config) => {
  const finalConfig: RequestConfig = { ...config };
  finalConfig.url = resolveUrl(config.url);
  finalConfig.method = (config.method || 'GET').toUpperCase() as Method;
  finalConfig.timeout = config.timeout || 15000;

  const headers: Record<string, string> = {
    'Content-Type': 'application/json;charset=UTF-8',
    Accept: 'application/json',
    ...(config.header || {})
  };

  if (config.isToken !== false) {
    const authHeader = buildAuthHeader();
    if (authHeader && !headers[authHeader.name]) {
      headers[authHeader.name] = authHeader.value;
    }
  }

  finalConfig.header = headers;
  return finalConfig;
});

/**
 * 响应拦截器：解 ApiResponse，HTTP 与业务异常归一
 */
responseInterceptors.use(({ res, config }) => {
  const httpStatus = res.statusCode;

  if (httpStatus === 401) {
    if (!config.skipAuthRedirect) {
      handle401(config.pendingAction);
    }
    throw new BusinessError({ code: 401, message: '登录已失效', httpStatus });
  }

  if (httpStatus < 200 || httpStatus >= 300) {
    const body = res.data as ApiResponse | undefined;
    throw new BusinessError({
      code: httpStatus,
      message: body?.message || `HTTP ${httpStatus}`,
      httpStatus,
      data: body
    });
  }

  if (config.raw === true) {
    return { res, config };
  }

  const body = res.data;
  if (!body || typeof body !== 'object') {
    return { res: { ...res, data: body as never }, config };
  }

  const apiResp = body as ApiResponse;
  if (typeof apiResp.code !== 'undefined') {
    if (apiResp.code === SUCCESS_CODE) {
      return {
        res: { ...res, data: (apiResp.data ?? null) as never },
        config
      };
    }
    if (apiResp.code === 401) {
      if (!config.skipAuthRedirect) {
        handle401(config.pendingAction);
      }
      throw new BusinessError({
        code: 401,
        message: apiResp.message || '登录已失效',
        requestId: apiResp.requestId,
        data: apiResp.data,
        httpStatus
      });
    }
    throw new BusinessError({
      code: apiResp.code,
      message: apiResp.message,
      requestId: apiResp.requestId,
      data: apiResp.data,
      httpStatus
    });
  }

  return { res, config };
});

function appendQuery(url: string, params: Record<string, unknown>): string {
  const parts: string[] = [];
  Object.keys(params).forEach((key) => {
    const v = params[key];
    if (v === undefined || v === null) return;
    if (Array.isArray(v)) {
      v.forEach((item) => parts.push(`${encodeURIComponent(key)}=${encodeURIComponent(String(item))}`));
    } else {
      parts.push(`${encodeURIComponent(key)}=${encodeURIComponent(String(v))}`);
    }
  });
  if (!parts.length) return url;
  return url + (url.indexOf('?') >= 0 ? '&' : '?') + parts.join('&');
}

function runRequestChain(config: RequestConfig): Promise<RequestConfig> {
  let promise: Promise<RequestConfig> = Promise.resolve(config);
  requestInterceptors.forEach((h) => {
    promise = promise.then(h.fulfilled, h.rejected as never);
  });
  return promise;
}

function runResponseChain(payload: { res: ResponseShape; config: RequestConfig }): Promise<{
  res: ResponseShape;
  config: RequestConfig;
}> {
  let promise: Promise<{ res: ResponseShape; config: RequestConfig }> = Promise.resolve(payload);
  responseInterceptors.forEach((h) => {
    promise = promise.then(h.fulfilled, h.rejected as never);
  });
  return promise;
}

/**
 * 核心请求方法
 */
export function request<T = unknown>(config: RequestConfig): Promise<T> {
  if (!config || !config.url) {
    return Promise.reject(new BusinessError({ code: -1, message: '请求路径缺失' }));
  }

  let task: UniApp.RequestTask | null = null;
  let loadingShown = false;
  let abortHandler: (() => void) | undefined;

  const promise = runRequestChain(config)
    .then((finalConfig) => {
      if (finalConfig.params) {
        finalConfig.url = appendQuery(finalConfig.url, finalConfig.params);
      }
      if (finalConfig.showLoading) {
        loadingShown = true;
        uni.showLoading({ title: finalConfig.loadingText || '加载中', mask: true });
      }

      return new Promise<{ res: ResponseShape; config: RequestConfig }>((resolve, reject) => {
        task = uni.request({
          url: finalConfig.url,
          // mp-weixin 不支持 PATCH，运行时由 uni-app 在 H5/APP 端正确处理；类型上做兼容断言
          method: finalConfig.method as unknown as 'GET',
          data: finalConfig.data as never,
          header: finalConfig.header,
          timeout: finalConfig.timeout,
          dataType: 'json',
          success: (res) => {
            resolve({
              res: {
                data: res.data as ApiResponse,
                statusCode: res.statusCode,
                header: res.header as Record<string, string>
              },
              config: finalConfig
            });
          },
          fail: (err) => reject(new NetworkError(err && (err as { errMsg?: string }).errMsg, err))
        }) as unknown as UniApp.RequestTask;

        if (finalConfig.signal) {
          abortHandler = () => task && task.abort();
          finalConfig.signal.addEventListener?.('abort', abortHandler);
        }
      });
    })
    .then((payload) => runResponseChain(payload))
    .then(({ res }) => res.data as T)
    .catch((err) => {
      if (!config.skipErrorToast) toastError(err);
      throw err;
    })
    .finally(() => {
      if (abortHandler && config.signal) {
        config.signal.removeEventListener?.('abort', abortHandler);
      }
      if (loadingShown) uni.hideLoading();
    });

  return promise;
}

/**
 * 文件上传
 */
export interface UploadConfig {
  url: string;
  filePath: string;
  name?: string;
  formData?: Record<string, unknown>;
  header?: Record<string, string>;
  timeout?: number;
  isToken?: boolean;
  skipErrorToast?: boolean;
  skipAuthRedirect?: boolean;
}

export function upload<T = unknown>(config: UploadConfig): Promise<T> {
  if (!config || !config.url || !config.filePath) {
    return Promise.reject(new BusinessError({ code: -1, message: '上传参数缺失' }));
  }
  const url = resolveUrl(config.url);
  const header: Record<string, string> = { ...(config.header || {}) };
  if (config.isToken !== false) {
    const authHeader = buildAuthHeader();
    if (authHeader && !header[authHeader.name]) {
      header[authHeader.name] = authHeader.value;
    }
  }

  return new Promise<T>((resolve, reject) => {
    uni.uploadFile({
      url,
      filePath: config.filePath,
      name: config.name || 'file',
      formData: config.formData,
      header,
      timeout: config.timeout || 30000,
      success: (res) => {
        let body: ApiResponse<T> | string | null = null;
        try {
          body = res.data ? (JSON.parse(res.data) as ApiResponse<T>) : null;
        } catch {
          body = res.data;
        }
        const httpStatus = res.statusCode;
        if (httpStatus === 401) {
          if (!config.skipAuthRedirect) handle401();
          return reject(new BusinessError({ code: 401, message: '登录已失效', httpStatus }));
        }
        if (httpStatus < 200 || httpStatus >= 300) {
          return reject(
            new BusinessError({
              code: httpStatus,
              message: (body && typeof body === 'object' && body.message) || `HTTP ${httpStatus}`,
              httpStatus,
              data: body
            })
          );
        }
        if (body && typeof body === 'object' && typeof body.code !== 'undefined') {
          if (body.code === SUCCESS_CODE) return resolve((body.data ?? null) as T);
          if (body.code === 401) {
            if (!config.skipAuthRedirect) handle401();
            return reject(
              new BusinessError({
                code: 401,
                message: body.message || '登录已失效',
                requestId: body.requestId,
                data: body.data,
                httpStatus
              })
            );
          }
          return reject(
            new BusinessError({
              code: body.code,
              message: body.message,
              requestId: body.requestId,
              data: body.data,
              httpStatus
            })
          );
        }
        resolve(body as T);
      },
      fail: (err) => reject(new NetworkError((err as { errMsg?: string }).errMsg, err))
    });
  }).catch((err) => {
    if (!config.skipErrorToast) toastError(err);
    throw err;
  });
}

/** 快捷方法 */
export const http = {
  get<T = unknown>(url: string, params?: Record<string, unknown>, options?: Partial<RequestConfig>): Promise<T> {
    return request<T>({ ...options, url, method: 'GET', params });
  },
  post<T = unknown>(url: string, data?: unknown, options?: Partial<RequestConfig>): Promise<T> {
    return request<T>({ ...options, url, method: 'POST', data });
  },
  put<T = unknown>(url: string, data?: unknown, options?: Partial<RequestConfig>): Promise<T> {
    return request<T>({ ...options, url, method: 'PUT', data });
  },
  delete<T = unknown>(url: string, params?: Record<string, unknown>, options?: Partial<RequestConfig>): Promise<T> {
    return request<T>({ ...options, url, method: 'DELETE', params });
  },
  patch<T = unknown>(url: string, data?: unknown, options?: Partial<RequestConfig>): Promise<T> {
    return request<T>({ ...options, url, method: 'PATCH', data });
  }
};

export const interceptors = {
  request: requestInterceptors,
  response: responseInterceptors
};

export { SUCCESS_CODE };
