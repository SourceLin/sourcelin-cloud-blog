import axios, { type InternalAxiosRequestConfig, type AxiosResponse } from "axios";
import qs from "qs";
import { ApiCodeEnum } from "@/enums/api";
import { usePermissionStoreHook } from "@/store/modules/permission";
import { useUserStoreHook } from "@/store/modules/user";
import { AuthStorage, redirectToLogin } from "@/utils/auth";

interface RetriableRequestConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
}

const AUTH_REFRESH_URL = "/auth/refresh";
const AUTH_LOGIN_URL = "/auth/login";
const AUTH_LOGOUT_URL = "/auth/logout";

function canRetryAfterRefresh(config?: InternalAxiosRequestConfig) {
  if (!config) return false;
  const retriableConfig = config as RetriableRequestConfig;
  if (retriableConfig._retry) return false;

  const requestUrl = String(config.url || "");
  if (
    requestUrl.includes(AUTH_REFRESH_URL) ||
    requestUrl.includes(AUTH_LOGIN_URL) ||
    requestUrl.includes(AUTH_LOGOUT_URL)
  ) {
    return false;
  }

  return true;
}

// HTTP 请求实例
const http = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 50000,
  headers: { "Content-Type": "application/json;charset=utf-8" },
  paramsSerializer: (params) => qs.stringify(params, { arrayFormat: "repeat" }),
});

// 请求拦截器
http.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = AuthStorage.getAccessToken();

    if (config.headers.Authorization === "no-auth") {
      delete config.headers.Authorization;
    } else if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// 响应拦截器
http.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { responseType } = response.config;

    // 二进制数据直接返回
    if (responseType === "blob" || responseType === "arraybuffer") {
      return response;
    }

    const { code, data, message } = response.data || {};

    if (code === ApiCodeEnum.SUCCESS) {
      return data;
    }

    ElMessage.error(message || "系统出错");
    return Promise.reject(new Error(message || "系统出错"));
  },

  async (error) => {
    const { response, config } = error as {
      response?: AxiosResponse<Partial<ApiResponse>>;
      config?: InternalAxiosRequestConfig;
    };

    if (!response) {
      ElMessage.error("网络连接失败");
      return Promise.reject(error);
    }

    const payload = (response.data || {}) as Partial<ApiResponse>;
    const code = payload.code;
    const message = payload.message || error.message;

    if (code === ApiCodeEnum.UNAUTHORIZED) {
      if (canRetryAfterRefresh(config)) {
        try {
          const retriableConfig = config as RetriableRequestConfig;
          retriableConfig._retry = true;

          const userStore = useUserStoreHook();
          await userStore.refreshTokenOnce();

          const latestToken = AuthStorage.getAccessToken();
          if (latestToken) {
            retriableConfig.headers = retriableConfig.headers || {};
            retriableConfig.headers.Authorization = `Bearer ${latestToken}`;
          }

          return http.request(retriableConfig);
        } catch {
          // refresh 或重放失败时，统一走登录重定向
        }
      }
      await redirectToLogin("登录已过期，请重新登录");
      return Promise.reject(new Error(message || "Unauthorized"));
    }

    // 权限不足：刷新权限快照后提示
    if (code === ApiCodeEnum.PERMISSION_DENIED) {
      const permissionStore = usePermissionStoreHook();
      await permissionStore.reloadPermissionSnapshotOnce();
      ElMessage.error(message || "权限不足");
      return Promise.reject(new Error(message || "权限不足"));
    }

    ElMessage.error(message || "请求失败");
    return Promise.reject(new Error(message || "请求失败"));
  }
);

export default http;
