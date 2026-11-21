package com.sourcelin.ai.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AI告警服务
 */
@Service
public class AiAlertService {
    private static final Logger log = LoggerFactory.getLogger(AiAlertService.class);

    /**
     * 错误计数器，用于防止告警风暴
     */
    private final ConcurrentHashMap<String, AtomicInteger> errorCounters = new ConcurrentHashMap<>();

    /**
     * 告警阈值：同一错误在时间窗口内的最大告警次数
     */
    @Value("${ai.alert.max-alerts-in-window:5}")
    private int maxAlertsInWindow;

    /**
     * 告警时间窗口（分钟）
     */
    @Value("${ai.alert.window-minutes:10}")
    private int windowMinutes;

    /**
     * 告警通知方式：log, email, webhook
     */
    @Value("${ai.alert.notification-type:log}")
    private String notificationType;

    /**
     * Webhook URL
     */
    @Value("${ai.alert.webhook-url:}")
    private String webhookUrl;

    @Autowired
    private AiMetricsCollector metricsCollector;

    /**
     * 发送告警
     */
    public void sendAlert(String alertType, String title, String message, String severity) {
        // 检查是否应该发送告警（防止告警风暴）
        if (!shouldSendAlert(alertType)) {
            log.debug("告警被抑制: {} - {}", alertType, title);
            return;
        }

        String formattedMessage = formatAlertMessage(alertType, title, message, severity);

        // 根据配置的告警方式发送
        switch (notificationType.toLowerCase()) {
            case "log":
                sendLogAlert(formattedMessage, severity);
                break;
            case "email":
                sendEmailAlert(title, formattedMessage);
                break;
            case "webhook":
                sendWebhookAlert(title, formattedMessage, severity);
                break;
            default:
                sendLogAlert(formattedMessage, severity);
        }
    }

    /**
     * 发送性能告警
     */
    public void sendPerformanceAlert(String abilityCode, long latencyMs, double thresholdMs) {
        String title = "AI调用性能告警";
        String message = String.format("能力: %s, 延迟: %dms, 阈值: %.0fms",
                abilityCode, latencyMs, thresholdMs);
        sendAlert("PERFORMANCE", title, message, "WARNING");
    }

    /**
     * 发送错误率告警
     */
    public void sendErrorRateAlert(String abilityCode, double errorRate, double threshold) {
        String title = "AI调用错误率告警";
        String message = String.format("能力: %s, 错误率: %.2f%%, 阈值: %.2f%%",
                abilityCode, errorRate * 100, threshold * 100);
        sendAlert("ERROR_RATE", title, message, "CRITICAL");
    }

    /**
     * 发送服务不可用告警
     */
    public void sendServiceUnavailableAlert(String providerType, String reason) {
        String title = "AI服务不可用告警";
        String message = String.format("提供商: %s, 原因: %s", providerType, reason);
        sendAlert("SERVICE_UNAVAILABLE", title, message, "CRITICAL");
    }

    /**
     * 发送速率限制告警
     */
    public void sendRateLimitAlert(String clientIp, String userId, String abilityCode) {
        String title = "AI调用速率限制告警";
        String message = String.format("客户端: %s, 用户: %s, 能力: %s",
                clientIp, userId != null ? userId : "anonymous", abilityCode);
        sendAlert("RATE_LIMIT", title, message, "WARNING");
    }

    /**
     * 检查是否应该发送告警
     */
    private boolean shouldSendAlert(String alertType) {
        LocalDateTime now = LocalDateTime.now();
        String windowKey = alertType + ":" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));

        AtomicInteger counter = errorCounters.computeIfAbsent(windowKey, k -> new AtomicInteger(0));
        int currentCount = counter.incrementAndGet();

        // 清理过期的计数器
        if (errorCounters.size() > 1000) {
            errorCounters.clear();
        }

        return currentCount <= maxAlertsInWindow;
    }

    /**
     * 格式化告警消息
     */
    private String formatAlertMessage(String alertType, String title, String message, String severity) {
        return String.format("[%s] %s\n时间: %s\n类型: %s\n详情: %s",
                severity,
                title,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                alertType,
                message);
    }

    /**
     * 发送日志告警
     */
    private void sendLogAlert(String message, String severity) {
        switch (severity.toUpperCase()) {
            case "CRITICAL":
                log.error("AI告警: {}", message);
                break;
            case "WARNING":
                log.warn("AI告警: {}", message);
                break;
            default:
                log.info("AI告警: {}", message);
        }
    }

    /**
     * 发送邮件告警
     */
    private void sendEmailAlert(String title, String message) {
        // 实际实现需要集成邮件服务
        log.info("邮件告警 - 标题: {}, 内容: {}", title, message);
    }

    /**
     * 发送Webhook告警
     */
    private void sendWebhookAlert(String title, String message, String severity) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            log.warn("Webhook URL未配置，降级为日志告警");
            sendLogAlert(message, severity);
            return;
        }

        // 实际实现需要调用Webhook API
        log.info("Webhook告警 - URL: {}, 标题: {}, 内容: {}", webhookUrl, title, message);
    }
}