/**
 * 认证相关类型定义
 */

/**
 * 登录请求参数
 */
export interface LoginRequest {
  /** 用户名 */
  username: string;
  /** 密码 */
  password: string;
  /** 登录端：admin/blog */
  loginType?: "admin" | "blog";
  /** 验证码缓存key */
  captchaUuid?: string;
  /** 验证码 */
  captchaCode?: string;
  /** 记住我 */
  rememberMe?: boolean;
}

/**
 * 登录响应
 */
export interface LoginResponse {
  /** 令牌值 */
  token: string;
  /** Token Header 名称 */
  tokenName: string;
  /** Token 前缀 */
  tokenPrefix: string;
  /** 过期时间(单位:秒) */
  expiresIn: number;
  /** 登录端 */
  loginType: "admin" | "blog";
}

/**
 * 验证码响应
 */
export interface CaptchaInfo {
  /** 是否开启验证码 */
  captchaEnabled: boolean;
  /** 验证码缓存 key（关闭验证码时通常不存在） */
  uuid?: string;
  /** 验证码图片 Base64（关闭验证码时通常不存在；开启时为纯 base64，不含 data: 前缀） */
  img?: string;
}
