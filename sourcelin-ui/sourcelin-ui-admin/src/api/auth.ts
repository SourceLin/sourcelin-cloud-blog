import request from "@/utils/request";
import type { LoginRequest, LoginResponse, CaptchaInfo } from "@/types/api/auth";
import { encryptLoginPassword } from "@/utils/rsa";

const AUTH_BASE_URL = "/auth";

const AuthAPI = {
  /** 登录接口*/
  login(data: LoginRequest) {
    const payload = {
      username: data.username,
      password: encryptLoginPassword(data.password),
      loginType: data.loginType ?? "admin",
      captchaUuid: data.captchaUuid,
      captchaCode: data.captchaCode,
    };

    return request<any, LoginResponse>({
      url: `${AUTH_BASE_URL}/login`,
      method: "post",
      data: payload,
    });
  },

  /** 刷新 token 接口*/
  refreshToken() {
    return request<any, number>({
      url: `${AUTH_BASE_URL}/refresh`,
      method: "post",
    });
  },

  /** 退出登录接口 */
  logout() {
    return request({
      url: `${AUTH_BASE_URL}/logout`,
      method: "delete",
    });
  },

  /** 获取验证码接口*/
  getCaptcha() {
    return request<any, CaptchaInfo>({
      url: `${AUTH_BASE_URL}/captcha`,
      method: "get",
      params: { loginType: "admin" },
    });
  },
};

export default AuthAPI;
