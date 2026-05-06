/**
 * API 相关枚举
 *
 * @description
 * 包含 API 响应码、请求状态等枚举定义
 */

/**
 * API 响应码枚举
 */
export const enum ApiCodeEnum {
  /**
   * 成功
   */
  SUCCESS = 0,

  /**
   * 未登录或登录失效
   */
  UNAUTHORIZED = 40100,

  /**
   * 权限不足
   */
  PERMISSION_DENIED = 40300,
}
