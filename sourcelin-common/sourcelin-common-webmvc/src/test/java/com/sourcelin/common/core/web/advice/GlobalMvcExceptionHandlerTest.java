package com.sourcelin.common.core.web.advice;

import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalMvcExceptionHandlerTest
{
    @Test
    void shouldProvideCommonMvcExceptionHandlerInWebmvcModule() throws Exception
    {
        Class<?> handlerClass = Class.forName("com.sourcelin.common.core.web.advice.GlobalMvcExceptionHandler");

        assertNotNull(handlerClass);
    }

    @Test
    void shouldHandleBusinessExceptionAsUnifiedApiResponse() throws Exception
    {
        Class<?> handlerClass = Class.forName("com.sourcelin.common.core.web.advice.GlobalMvcExceptionHandler");
        Object handler = handlerClass.getDeclaredConstructor().newInstance();
        Method method = handlerClass.getDeclaredMethod("handleBusinessException", BusinessException.class);

        @SuppressWarnings("unchecked")
        ResponseEntity<ApiResponse<Object>> response = (ResponseEntity<ApiResponse<Object>>) method.invoke(
            handler,
            new BusinessException(ResultCode.NOT_FOUND, "文章不存在")
        );

        assertEquals(404, response.getStatusCodeValue());
        assertEquals(ResultCode.NOT_FOUND.getCode(), response.getBody().getCode());
        assertEquals("文章不存在", response.getBody().getMessage());
    }

    @Test
    void shouldNotHandleSaTokenExceptionsInCommonWebmvc() throws Exception
    {
        Class<?> handlerClass = Class.forName("com.sourcelin.common.core.web.advice.GlobalMvcExceptionHandler");

        for (Method method : handlerClass.getDeclaredMethods())
        {
            ExceptionHandler exceptionHandler = method.getAnnotation(ExceptionHandler.class);
            if (exceptionHandler == null)
            {
                continue;
            }
            for (Class<?> exceptionType : exceptionHandler.value())
            {
                assertFalse(
                    exceptionType.getName().startsWith("cn.dev33.satoken."),
                    "common-webmvc must not depend on Sa-Token exception types"
                );
            }
        }
    }
}
