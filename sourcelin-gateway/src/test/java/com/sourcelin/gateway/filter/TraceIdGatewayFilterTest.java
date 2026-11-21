package com.sourcelin.gateway.filter;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TraceIdGatewayFilterTest
{
    private static final String TRACE_ID_HEADER = "X-Request-Id";

    @Test
    void shouldKeepValidTraceIdForDownstreamAndResponse() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/test").header(TRACE_ID_HEADER, "trace-123_abc")
        );
        AtomicReference<String> downstreamTraceId = new AtomicReference<>();

        new TraceIdGatewayFilter().filter(exchange, serverWebExchange -> {
            downstreamTraceId.set(serverWebExchange.getRequest().getHeaders().getFirst(TRACE_ID_HEADER));
            return serverWebExchange.getResponse().setComplete();
        }).block();

        assertEquals("trace-123_abc", downstreamTraceId.get());
        assertEquals("trace-123_abc", exchange.getResponse().getHeaders().getFirst(TRACE_ID_HEADER));
        assertNull(MDC.get("traceId"));
    }

    @Test
    void shouldReplaceInvalidExternalTraceId() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/test").header(TRACE_ID_HEADER, "bad trace id")
        );
        AtomicReference<String> downstreamTraceId = new AtomicReference<>();

        new TraceIdGatewayFilter().filter(exchange, serverWebExchange -> {
            downstreamTraceId.set(serverWebExchange.getRequest().getHeaders().getFirst(TRACE_ID_HEADER));
            return serverWebExchange.getResponse().setComplete();
        }).block();

        String traceId = downstreamTraceId.get();
        assertNotEquals("bad trace id", traceId);
        assertEquals(traceId, exchange.getResponse().getHeaders().getFirst(TRACE_ID_HEADER));
        assertTrue(traceId.matches("[A-Za-z0-9._-]{1,64}"));
        assertNull(MDC.get("traceId"));
    }

    @Test void cannotForgeInternalSourceHeaderThroughPublicGateway() {
        MockServerWebExchange exchange=MockServerWebExchange.from(MockServerHttpRequest.get("/ai/tasks").header("from-source","inner"));
        new TraceIdGatewayFilter().filter(exchange, downstream -> {
            assertNull(downstream.getRequest().getHeaders().getFirst("from-source"));
            return downstream.getResponse().setComplete();
        }).block();
        assertNull(MDC.get("traceId"));
    }
}
