package com.sourcelin.common.security.feign;

import java.util.Map;
import java.util.regex.Pattern;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.utils.ServletUtils;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.utils.ip.IpUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * feign 请求拦截器
 * 
 * @author sourcelin
 */
@Component
public class FeignRequestInterceptor implements RequestInterceptor
{
    private static final String TRACE_ID_HEADER = "X-Request-Id";
    private static final String REQUEST_ID_ATTRIBUTE = "requestId";
    private static final Pattern TRACE_ID_PATTERN = Pattern.compile("[A-Za-z0-9._-]{1,64}");

    @Override
    public void apply(RequestTemplate requestTemplate)
    {
        HttpServletRequest httpServletRequest = ServletUtils.getRequest();
        if (StringUtils.isNotNull(httpServletRequest))
        {
            Map<String, String> headers = ServletUtils.getHeaders(httpServletRequest);
            // 传递用户信息请求头，防止丢失
            String userId = headers.get(SecurityConstants.DETAILS_USER_ID);
            if (StringUtils.isNotEmpty(userId))
            {
                requestTemplate.header(SecurityConstants.DETAILS_USER_ID, userId);
            }
            String userKey = headers.get(SecurityConstants.USER_KEY);
            if (StringUtils.isNotEmpty(userKey))
            {
                requestTemplate.header(SecurityConstants.USER_KEY, userKey);
            }
            String userName = headers.get(SecurityConstants.DETAILS_USERNAME);
            if (StringUtils.isNotEmpty(userName))
            {
                requestTemplate.header(SecurityConstants.DETAILS_USERNAME, userName);
            }
            String authentication = headers.get(SecurityConstants.AUTHORIZATION_HEADER);
            if (StringUtils.isNotEmpty(authentication))
            {
                requestTemplate.header(SecurityConstants.AUTHORIZATION_HEADER, authentication);
            }

            // 配置客户端IP
            requestTemplate.header("X-Forwarded-For", IpUtils.getIpAddr());

            // 传递 TraceId
            String traceId = resolveTraceId(httpServletRequest, headers);
            if (StringUtils.isNotEmpty(traceId))
            {
                requestTemplate.header(TRACE_ID_HEADER, traceId);
            }
        }
    }

    private String resolveTraceId(HttpServletRequest httpServletRequest, Map<String, String> headers)
    {
        String traceId = normalizeTraceId((String) httpServletRequest.getAttribute(REQUEST_ID_ATTRIBUTE));
        if (StringUtils.isEmpty(traceId))
        {
            traceId = normalizeTraceId(headers.get("x-request-id"));
        }
        if (StringUtils.isEmpty(traceId))
        {
            traceId = normalizeTraceId(httpServletRequest.getHeader(TRACE_ID_HEADER));
        }
        return traceId;
    }

    private String normalizeTraceId(String traceId)
    {
        if (StringUtils.isEmpty(traceId))
        {
            return null;
        }
        String normalizedTraceId = traceId.trim();
        return TRACE_ID_PATTERN.matcher(normalizedTraceId).matches() ? normalizedTraceId : null;
    }
}
