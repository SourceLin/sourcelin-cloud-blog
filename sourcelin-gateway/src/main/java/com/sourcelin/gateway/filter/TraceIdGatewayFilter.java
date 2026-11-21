package com.sourcelin.gateway.filter;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 网关全局 traceId 过滤器，生成/透传 traceId 到下游服务并回写响应头。
 */
@Component
public class TraceIdGatewayFilter implements GlobalFilter, Ordered {

    private static final String TRACE_ID_HEADER = "X-Request-Id";
    private static final String MDC_KEY = "traceId";
    private static final Pattern TRACE_ID_PATTERN = Pattern.compile("[A-Za-z0-9._-]{1,64}");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = resolveTraceId(exchange);

        // 往下游服务传递 traceId 头
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(headers -> headers.remove("from-source"))
                .header(TRACE_ID_HEADER, traceId)
                .build();

        // 网关响应头回写 traceId
        exchange.getResponse().getHeaders().add(TRACE_ID_HEADER, traceId);

        return Mono.defer(() -> {
            String previous = MDC.get(MDC_KEY);
            MDC.put(MDC_KEY, traceId);
            try { return chain.filter(exchange.mutate().request(request).build()); }
            finally { if (previous == null) MDC.remove(MDC_KEY); else MDC.put(MDC_KEY, previous); }
        }).contextWrite(context -> context.put(MDC_KEY, traceId));
    }

    private String resolveTraceId(ServerWebExchange exchange) {
        String traceId = normalizeTraceId(exchange.getRequest().getHeaders().getFirst(TRACE_ID_HEADER));
        return traceId != null ? traceId : newTraceId();
    }

    private static String normalizeTraceId(String traceId) {
        if (traceId == null) {
            return null;
        }
        String normalizedTraceId = traceId.trim();
        return TRACE_ID_PATTERN.matcher(normalizedTraceId).matches() ? normalizedTraceId : null;
    }

    private static String newTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
