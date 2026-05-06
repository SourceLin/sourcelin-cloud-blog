package com.sourcelin.common.core.web.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sourcelin.common.core.domain.R;
import com.sourcelin.common.core.web.annotation.IgnoreApiResponseWrap;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * 新协议响应包装器。
 *
 * 统一将业务响应收敛为 ApiResponse：
 * 1. 业务代码直接返回业务对象 / PageResult / ListResult
 * 2. 微服务内部 Feign 协议保持 R 结构直通
 */
@Component
@RequiredArgsConstructor
@RestControllerAdvice
public class ApiResponseBodyAdvice implements ResponseBodyAdvice<Object>
{
    private static final String BUSINESS_PACKAGE_PREFIX = "com.sourcelin.";

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType)
    {
        // 仅对业务 Controller 做统一包装，第三方框架端点（如 Spring Boot Admin）保持原生协议
        String className = returnType.getContainingClass().getName();
        if (!className.startsWith(BUSINESS_PACKAGE_PREFIX))
        {
            return false;
        }
        if (returnType.getContainingClass().isAnnotationPresent(IgnoreApiResponseWrap.class))
        {
            return false;
        }
        return !returnType.hasMethodAnnotation(IgnoreApiResponseWrap.class);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  org.springframework.http.MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response)
    {
        if (body == null)
        {
            return ApiResponse.success();
        }

        if (body instanceof ApiResponse)
        {
            return body;
        }

        // 微服务内部 Feign 协议仍使用 R 结构，避免被二次包装
        if (body instanceof R)
        {
            return body;
        }

        if (body instanceof ResponseEntity || body instanceof Resource || body instanceof StreamingResponseBody
            || body instanceof byte[] || body instanceof HttpServletResponse)
        {
            return body;
        }

        if (ByteArrayHttpMessageConverter.class.isAssignableFrom(selectedConverterType))
        {
            return body;
        }

        ApiResponse<Object> wrapped = ApiResponse.success(body);
        if (body instanceof String || StringHttpMessageConverter.class.isAssignableFrom(selectedConverterType))
        {
            try
            {
                return objectMapper.writeValueAsString(wrapped);
            }
            catch (JsonProcessingException ex)
            {
                throw new IllegalStateException("统一响应包装失败", ex);
            }
        }

        return wrapped;
    }
}
