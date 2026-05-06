package com.sourcelin.common.security.handler;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityExceptionHandlerBoundaryTest
{
    @Test
    void shouldProvideSecurityOnlyExceptionHandler() throws Exception
    {
        Class<?> handlerClass = Class.forName("com.sourcelin.common.security.handler.SecurityExceptionHandler");

        assertNotNull(handlerClass);
    }

    @Test
    void shouldOnlyHandleSaTokenExceptionTypes() throws Exception
    {
        Class<?> handlerClass = Class.forName("com.sourcelin.common.security.handler.SecurityExceptionHandler");
        boolean hasSecurityExceptionHandler = false;

        for (Method method : handlerClass.getDeclaredMethods())
        {
            ExceptionHandler exceptionHandler = method.getAnnotation(ExceptionHandler.class);
            if (exceptionHandler == null)
            {
                continue;
            }
            for (Class<?> exceptionType : exceptionHandler.value())
            {
                hasSecurityExceptionHandler = true;
                assertTrue(
                    exceptionType.getName().startsWith("cn.dev33.satoken."),
                    "common-security exception advice must only handle Sa-Token exception types"
                );
                assertFalse(RuntimeException.class.equals(exceptionType));
                assertFalse(Exception.class.equals(exceptionType));
            }
        }

        assertTrue(hasSecurityExceptionHandler, "security handler must expose at least one @ExceptionHandler method");
    }
}
