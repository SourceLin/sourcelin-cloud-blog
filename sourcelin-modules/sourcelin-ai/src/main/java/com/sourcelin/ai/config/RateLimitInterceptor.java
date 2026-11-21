package com.sourcelin.ai.config;

import com.sourcelin.common.core.constant.CacheConstants;
import com.sourcelin.common.redis.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * AI调用速率限制拦截器
 */
public class RateLimitInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(RateLimitInterceptor.class);

    @Autowired
    private RedisService redisService;

    /**
     * 每分钟最大请求数
     */
    @Value("${ai.rate-limit.requests-per-minute:60}")
    private int requestsPerMinute;

    /**
     * 每小时最大请求数
     */
    @Value("${ai.rate-limit.requests-per-hour:1000}")
    private int requestsPerHour;

    /**
     * 每天最大请求数
     */
    @Value("${ai.rate-limit.requests-per-day:10000}")
    private int requestsPerDay;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = getClientIp(request);
        String userId = getUserId(request);
        String abilityCode = extractAbilityCode(request);

        // 检查每分钟限制
        if (!checkRateLimit("minute:" + clientIp, requestsPerMinute, TimeUnit.MINUTES)) {
            log.warn("AI调用速率限制: 每分钟请求超限, IP: {}, 用户: {}, 能力: {}", clientIp, userId, abilityCode);
            throw new com.sourcelin.common.core.exception.BusinessException(com.sourcelin.common.core.enums.ResultCode.TOO_MANY_REQUESTS);
        }

        // 检查每小时限制
        if (!checkRateLimit("hour:" + clientIp, requestsPerHour, TimeUnit.HOURS)) {
            log.warn("AI调用速率限制: 每小时请求超限, IP: {}, 用户: {}, 能力: {}", clientIp, userId, abilityCode);
            throw new com.sourcelin.common.core.exception.BusinessException(com.sourcelin.common.core.enums.ResultCode.TOO_MANY_REQUESTS);
        }

        // 检查每天限制
        if (!checkRateLimit("day:" + clientIp, requestsPerDay, TimeUnit.DAYS)) {
            log.warn("AI调用速率限制: 每日请求超限, IP: {}, 用户: {}, 能力: {}", clientIp, userId, abilityCode);
            throw new com.sourcelin.common.core.exception.BusinessException(com.sourcelin.common.core.enums.ResultCode.TOO_MANY_REQUESTS);
        }

        // 如果有用户ID，也检查用户级别的限制
        if (userId != null && !userId.isEmpty()) {
            if (!checkRateLimit("user:minute:" + userId, requestsPerMinute, TimeUnit.MINUTES)) {
                log.warn("AI调用速率限制: 用户每分钟请求超限, 用户: {}, 能力: {}", userId, abilityCode);
                throw new com.sourcelin.common.core.exception.BusinessException(com.sourcelin.common.core.enums.ResultCode.TOO_MANY_REQUESTS);
            }
        }

        return true;
    }

    /**
     * 检查速率限制（使用 Redis INCR 原子操作避免并发竞态）
     */
    private boolean checkRateLimit(String key, int maxRequests, TimeUnit timeUnit) {
        String redisKey = CacheConstants.AI_RATE_LIMIT_KEY + key;

        try {
            Long count = redisService.redisTemplate.opsForValue().increment(redisKey);
            if (count == null) {
                return true; // 降级放行
            }
            // 首次创建 key 时设置过期时间
            if (count == 1L) {
                redisService.expire(redisKey, timeUnit.toSeconds(1), TimeUnit.SECONDS);
            }
            return count <= maxRequests;
        } catch (Exception e) {
            log.error("速率限制检查失败", e);
            // 出错时允许通过，避免阻塞正常请求
            return true;
        }
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 多个代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    /**
     * 获取用户ID
     */
    private String getUserId(HttpServletRequest request) {
        // 从请求头或会话中获取用户ID
        String userId = request.getHeader("X-User-Id");
        if (userId == null || userId.isEmpty()) {
            userId = request.getHeader("Authorization");
            // 这里可以添加JWT解析逻辑
        }
        return userId;
    }

    /**
     * 从请求路径中提取能力编码
     */
    private String extractAbilityCode(HttpServletRequest request) {
        String path = request.getRequestURI();
        // 假设路径格式: /ai/abilities/{abilityCode}/invoke
        String[] parts = path.split("/");
        if (parts.length >= 3 && "abilities".equals(parts[1])) {
            return parts[2];
        }
        return "unknown";
    }
}
