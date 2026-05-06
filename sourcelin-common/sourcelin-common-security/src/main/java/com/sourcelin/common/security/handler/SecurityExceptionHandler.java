package com.sourcelin.common.security.handler;

import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 安全框架异常处理器。
 *
 * 仅处理 Sa-Token 相关异常；通用 MVC 异常由 common-webmvc 处理。
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class SecurityExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(SecurityExceptionHandler.class);

    @ExceptionHandler(cn.dev33.satoken.exception.NotLoginException.class)
    public ResponseEntity<ApiResponse<Object>> handleSaNotLoginException(
        cn.dev33.satoken.exception.NotLoginException e,
        HttpServletRequest request
    )
    {
        log.warn("请求地址'{}',登录校验失败'{}'", request.getRequestURI(), e.getMessage());
        return buildResponse(ResultCode.UNAUTHORIZED, "未登录或登录已过期", null);
    }

    @ExceptionHandler(cn.dev33.satoken.exception.NotPermissionException.class)
    public ResponseEntity<ApiResponse<Object>> handleSaNotPermissionException(
        cn.dev33.satoken.exception.NotPermissionException e,
        HttpServletRequest request
    )
    {
        log.warn("请求地址'{}',Sa-Token 权限校验失败'{}'", request.getRequestURI(), e.getMessage());
        return buildResponse(ResultCode.FORBIDDEN, "没有访问权限，请联系管理员授权", null);
    }

    @ExceptionHandler(cn.dev33.satoken.exception.NotRoleException.class)
    public ResponseEntity<ApiResponse<Object>> handleSaNotRoleException(
        cn.dev33.satoken.exception.NotRoleException e,
        HttpServletRequest request
    )
    {
        log.warn("请求地址'{}',Sa-Token 角色校验失败'{}'", request.getRequestURI(), e.getMessage());
        return buildResponse(ResultCode.FORBIDDEN, "没有访问权限，请联系管理员授权", null);
    }

    @ExceptionHandler(cn.dev33.satoken.exception.SaTokenException.class)
    public ResponseEntity<ApiResponse<Object>> handleSaTokenException(
        cn.dev33.satoken.exception.SaTokenException e,
        HttpServletRequest request
    )
    {
        log.error("请求地址'{}',Sa-Token 异常.", request.getRequestURI(), e);
        return buildResponse(ResultCode.UNAUTHORIZED, "登录态校验失败", null);
    }

    private ResponseEntity<ApiResponse<Object>> buildResponse(ResultCode resultCode, String message, Object data)
    {
        return ResponseEntity.status(resultCode.getHttpStatus()).body(ApiResponse.fail(resultCode, message, data));
    }
}
