package com.sourcelin.common.core.enums;

import lombok.Getter;

/**
 * 平台统一业务码定义。
 */
@Getter
public enum ResultCode
{
    SUCCESS(0, "OK", 200),

    BAD_REQUEST(40000, "请求错误", 400),
    VALIDATION_ERROR(40001, "参数校验失败", 400),

    UNAUTHORIZED(40100, "未登录或登录已失效", 401),
    FORBIDDEN(40300, "无权限访问", 403),
    NOT_FOUND(40400, "资源不存在", 404),
    CONFLICT(40900, "资源状态冲突", 409),

    BUSINESS_ERROR(42200, "业务处理失败", 422),
    TOO_MANY_REQUESTS(42900, "请求过于频繁", 429),

    SYSTEM_ERROR(50000, "系统异常", 500),
    REMOTE_SERVICE_ERROR(50001, "下游服务异常", 502);

    private final Integer code;
    private final String message;
    private final Integer httpStatus;

    ResultCode(Integer code, String message, Integer httpStatus)
    {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
