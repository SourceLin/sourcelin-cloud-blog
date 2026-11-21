package com.sourcelin.common.security.feign;

import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FeignRequestInterceptorTraceIdTest
{
    private static final String TRACE_ID_HEADER = "X-Request-Id";

    @AfterEach
    void tearDown()
    {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldPreferValidatedRequestAttributeTraceId()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(TRACE_ID_HEADER, "bad trace id");
        request.setAttribute("requestId", "trace-123_abc");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        RequestTemplate requestTemplate = new RequestTemplate();
        new FeignRequestInterceptor().apply(requestTemplate);

        assertEquals("trace-123_abc", requestTemplate.headers().get(TRACE_ID_HEADER).iterator().next());
    }

    @Test
    void shouldDropInvalidHeaderWhenRequestAttributeMissing()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(TRACE_ID_HEADER, "bad trace id");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        RequestTemplate requestTemplate = new RequestTemplate();
        new FeignRequestInterceptor().apply(requestTemplate);

        assertFalse(requestTemplate.headers().containsKey(TRACE_ID_HEADER));
    }
}
