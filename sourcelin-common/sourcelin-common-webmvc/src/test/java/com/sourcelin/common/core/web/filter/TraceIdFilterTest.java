package com.sourcelin.common.core.web.filter;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TraceIdFilterTest
{
    private static final String TRACE_ID_HEADER = "X-Request-Id";

    @Test
    void shouldKeepValidTraceIdAndClearMdc() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(TRACE_ID_HEADER, "trace-123_abc");

        new TraceIdFilter().doFilter(request, response, new MockFilterChain());

        assertEquals("trace-123_abc", request.getAttribute("requestId"));
        assertEquals("trace-123_abc", response.getHeader(TRACE_ID_HEADER));
        assertNull(MDC.get("traceId"));
    }

    @Test
    void shouldReplaceInvalidExternalTraceId() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(TRACE_ID_HEADER, "bad trace id");

        new TraceIdFilter().doFilter(request, response, new MockFilterChain());

        String traceId = response.getHeader(TRACE_ID_HEADER);
        assertNotEquals("bad trace id", traceId);
        assertEquals(traceId, request.getAttribute("requestId"));
        assertTrue(traceId.matches("[A-Za-z0-9._-]{1,64}"));
        assertNull(MDC.get("traceId"));
    }

    @org.junit.jupiter.api.Test
    void apiEnvelopeUsesValidatedAttributeInsteadOfInvalidClientHeader() {
        org.springframework.mock.web.MockHttpServletRequest request = new org.springframework.mock.web.MockHttpServletRequest();
        request.addHeader("X-Request-Id", "bad client header");request.setAttribute("requestId", "validated-id");
        org.springframework.web.context.request.RequestContextHolder.setRequestAttributes(new org.springframework.web.context.request.ServletRequestAttributes(request));
        try { org.junit.jupiter.api.Assertions.assertEquals("validated-id", com.sourcelin.common.core.web.domain.response.ApiResponse.success().getRequestId()); }
        finally { org.springframework.web.context.request.RequestContextHolder.resetRequestAttributes(); }
    }
}
