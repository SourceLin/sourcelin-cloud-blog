package com.sourcelin.common.core.web.advice;

import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.exception.DemoModeException;
import com.sourcelin.common.core.exception.InnerAuthException;
import com.sourcelin.common.core.exception.ServiceException;
import com.sourcelin.common.core.exception.auth.NotPermissionException;
import com.sourcelin.common.core.exception.auth.NotRoleException;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.DataTruncation;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * MVC 通用异常处理器。
 *
 * 仅处理 WebMVC 和 common-core 中的通用异常；安全框架异常由 common-security 单独处理。
 */
@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalMvcExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GlobalMvcExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex)
    {
        log.warn("业务异常: code={}, message={}", ex.getResultCode().getCode(), ex.getMessage());
        return buildResponse(ex.getResultCode(), ex.getMessage(), ex.getErrorData());
    }

    @ExceptionHandler(NotPermissionException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotPermissionException(NotPermissionException e, HttpServletRequest request)
    {
        log.error("请求地址'{}',权限码校验失败'{}'", request.getRequestURI(), e.getMessage());
        return buildResponse(ResultCode.FORBIDDEN, "没有访问权限，请联系管理员授权", null);
    }

    @ExceptionHandler(NotRoleException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotRoleException(NotRoleException e, HttpServletRequest request)
    {
        log.error("请求地址'{}',角色权限校验失败'{}'", request.getRequestURI(), e.getMessage());
        return buildResponse(ResultCode.FORBIDDEN, "没有访问权限，请联系管理员授权", null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                                                  HttpServletRequest request)
    {
        log.error("请求地址'{}',不支持'{}'请求", request.getRequestURI(), e.getMethod());
        return buildResponse(ResultCode.BAD_REQUEST, e.getMessage(), null);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiResponse<Object>> handleServiceException(ServiceException e, HttpServletRequest request)
    {
        log.error("请求地址'{}',业务异常.", request.getRequestURI(), e);
        Integer code = e.getCode();
        if (StringUtils.isNotNull(code))
        {
            if (code.intValue() == ResultCode.UNAUTHORIZED.getCode().intValue())
            {
                return buildResponse(ResultCode.UNAUTHORIZED, e.getMessage(), null);
            }
            return buildResponse(ResultCode.BUSINESS_ERROR, e.getMessage(), null);
        }
        return buildResponse(ResultCode.BUSINESS_ERROR, e.getMessage(), null);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingPathVariableException(MissingPathVariableException e,
                                                                                 HttpServletRequest request)
    {
        log.error("请求路径中缺少必需的路径变量'{}',发生系统异常.", request.getRequestURI(), e);
        return buildResponse(ResultCode.BAD_REQUEST, String.format("请求路径中缺少必需的路径变量[%s]", e.getVariableName()), null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e)
    {
        return buildResponse(ResultCode.VALIDATION_ERROR, String.format("缺少必填参数[%s]", e.getParameterName()), null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e,
                                                                                        HttpServletRequest request)
    {
        log.error("请求参数类型不匹配'{}',发生系统异常.", request.getRequestURI(), e);
        String requiredType = e.getRequiredType() == null ? "" : e.getRequiredType().getName();
        String message = String.format("请求参数类型不匹配，参数[%s]要求类型为：'%s'，但输入值为：'%s'", e.getName(), requiredType, e.getValue());
        return buildResponse(ResultCode.VALIDATION_ERROR, message, null);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Object>> handleBindException(BindException e)
    {
        log.error(e.getMessage(), e);
        return buildResponse(ResultCode.VALIDATION_ERROR, ResultCode.VALIDATION_ERROR.getMessage(), buildFieldErrorData(e.getBindingResult().getFieldErrors()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        log.error(e.getMessage(), e);
        return buildResponse(ResultCode.VALIDATION_ERROR, ResultCode.VALIDATION_ERROR.getMessage(), buildFieldErrorData(e.getBindingResult().getFieldErrors()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException e)
    {
        log.error(e.getMessage(), e);
        Map<String, String> fieldErrors = new LinkedHashMap<String, String>();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations)
        {
            fieldErrors.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        Map<String, Object> errorData = new LinkedHashMap<String, Object>();
        errorData.put("fieldErrors", fieldErrors);
        return buildResponse(ResultCode.VALIDATION_ERROR, ResultCode.VALIDATION_ERROR.getMessage(), errorData);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e,
                                                                                    HttpServletRequest request)
    {
        log.warn("请求地址'{}',请求体解析失败: {}", request.getRequestURI(), e.getMessage());
        String message = "请求数据格式错误";
        if (e.getMessage() != null)
        {
            if (e.getMessage().contains("JSON parse error"))
            {
                message = "请求数据JSON格式错误，请检查输入内容";
            }
            else if (e.getMessage().contains("Required request body is missing"))
            {
                message = "缺少请求体数据";
            }
        }
        return buildResponse(ResultCode.VALIDATION_ERROR, message, null);
    }

    @ExceptionHandler(DataTruncation.class)
    public ResponseEntity<ApiResponse<Object>> handleDataTruncation(DataTruncation e, HttpServletRequest request)
    {
        log.warn("请求地址'{}',数据截断: {}", request.getRequestURI(), e.getMessage());
        return buildResponse(ResultCode.VALIDATION_ERROR, "输入数据过长，请缩短内容后重试", null);
    }

    @ExceptionHandler(InnerAuthException.class)
    public ResponseEntity<ApiResponse<Object>> handleInnerAuthException(InnerAuthException e)
    {
        return buildResponse(ResultCode.UNAUTHORIZED, e.getMessage(), null);
    }

    @ExceptionHandler(DemoModeException.class)
    public ResponseEntity<ApiResponse<Object>> handleDemoModeException(DemoModeException e)
    {
        return buildResponse(ResultCode.BUSINESS_ERROR, "演示模式，不允许操作", null);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException e, HttpServletRequest request)
    {
        log.error("请求地址'{}',发生未知异常.", request.getRequestURI(), e);
        return buildResponse(ResultCode.SYSTEM_ERROR, "系统异常，请联系管理员", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e, HttpServletRequest request)
    {
        log.error("请求地址'{}',发生系统异常.", request.getRequestURI(), e);
        return buildResponse(ResultCode.SYSTEM_ERROR, "系统异常，请联系管理员", null);
    }

    private ResponseEntity<ApiResponse<Object>> buildResponse(ResultCode resultCode, String message, Object data)
    {
        return ResponseEntity.status(resultCode.getHttpStatus()).body(ApiResponse.fail(resultCode, message, data));
    }

    private Map<String, Object> buildFieldErrorData(Iterable<FieldError> fieldErrors)
    {
        Map<String, String> errorMap = new LinkedHashMap<String, String>();
        for (FieldError fieldError : fieldErrors)
        {
            if (!errorMap.containsKey(fieldError.getField()))
            {
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
        }

        Map<String, Object> errorData = new LinkedHashMap<String, Object>();
        errorData.put("fieldErrors", errorMap);
        return errorData;
    }
}
