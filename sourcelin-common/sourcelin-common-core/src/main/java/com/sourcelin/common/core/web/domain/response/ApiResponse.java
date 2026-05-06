package com.sourcelin.common.core.web.domain.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.utils.ServletUtils;
import com.sourcelin.common.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 平台统一接口响应体。
 *
 * @param <T> 业务数据类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;

    private T data;

    private String requestId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success()
    {
        return success(null);
    }

    public static <T> ApiResponse<T> success(T data)
    {
        return success(ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> ApiResponse<T> success(String message, T data)
    {
        return ApiResponse.<T>builder()
            .code(ResultCode.SUCCESS.getCode())
            .message(message)
            .data(data)
            .requestId(resolveRequestId())
            .timestamp(LocalDateTime.now())
            .build();
    }

    public static <T> ApiResponse<T> fail(ResultCode resultCode)
    {
        return fail(resultCode, resultCode.getMessage(), null);
    }

    public static <T> ApiResponse<T> fail(ResultCode resultCode, String message)
    {
        return fail(resultCode, message, null);
    }

    public static <T> ApiResponse<T> fail(ResultCode resultCode, String message, T data)
    {
        return ApiResponse.<T>builder()
            .code(resultCode.getCode())
            .message(StringUtils.isBlank(message) ? resultCode.getMessage() : message)
            .data(data)
            .requestId(resolveRequestId())
            .timestamp(LocalDateTime.now())
            .build();
    }

    private static String resolveRequestId()
    {
        HttpServletRequest request = ServletUtils.getRequest();
        if (request == null)
        {
            return UUID.randomUUID().toString().replace("-", "");
        }

        String requestId = request.getHeader("X-Request-Id");
        if (StringUtils.isBlank(requestId))
        {
            requestId = request.getHeader("requestId");
        }
        if (StringUtils.isBlank(requestId))
        {
            Object requestIdAttr = request.getAttribute("requestId");
            if (requestIdAttr != null)
            {
                requestId = String.valueOf(requestIdAttr);
            }
        }
        return StringUtils.isBlank(requestId) ? UUID.randomUUID().toString().replace("-", "") : requestId;
    }
}
