package com.sourcelin.common.core.web.filter;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * MDC TraceId 过滤器，生成/透传 traceId 并将其存入 MDC 与 request attribute 中
 */
public class TraceIdFilter implements Filter {

    private static final String TRACE_ID_HEADER = "X-Request-Id";
    private static final String MDC_KEY = "traceId";
    private static final Pattern TRACE_ID_PATTERN = Pattern.compile("[A-Za-z0-9._-]{1,64}");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String traceId = resolveTraceId(request);

        // 设置到 request attribute 中，供 ApiResponse.java 使用
        request.setAttribute("requestId", traceId);

        // 设置到 SLF4J MDC 中，供日志输出包含 traceId
        MDC.put(MDC_KEY, traceId);

        // 响应头回写 traceId
        response.setHeader(TRACE_ID_HEADER, traceId);

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }

    @Override
    public void destroy() {
    }

    private String resolveTraceId(HttpServletRequest request) {
        // 优先从 HTTP Header 中获取 traceId，非法外部值不进入 MDC 或响应头
        String traceId = normalizeTraceId(request.getHeader(TRACE_ID_HEADER));
        if (StringUtils.isEmpty(traceId)) {
            traceId = normalizeTraceId(request.getParameter("traceId"));
        }
        if (StringUtils.isEmpty(traceId)) {
            traceId = normalizeTraceId((String) request.getAttribute(MDC_KEY));
        }
        return traceId != null ? traceId : newTraceId();
    }

    private static String normalizeTraceId(String traceId) {
        if (StringUtils.isEmpty(traceId)) {
            return null;
        }
        String normalizedTraceId = traceId.trim();
        return TRACE_ID_PATTERN.matcher(normalizedTraceId).matches() ? normalizedTraceId : null;
    }

    private static String newTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
