package com.sourcelin.ai.monitor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * AI监控指标收集器
 */
@Component
public class AiMetricsCollector {

    private final MeterRegistry meterRegistry;

    @Autowired
    public AiMetricsCollector(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * 记录AI调用成功
     */
    public void recordSuccess(String abilityCode, String providerType, long latencyMs) {
        Counter.builder("ai.calls.total")
                .tag("status", "success")
                .tag("ability", abilityCode)
                .tag("provider", providerType)
                .description("Total AI calls")
                .register(meterRegistry)
                .increment();

        Timer.builder("ai.call.duration")
                .tag("ability", abilityCode)
                .tag("provider", providerType)
                .description("AI call duration")
                .register(meterRegistry)
                .record(latencyMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 记录AI调用失败
     */
    public void recordFailure(String abilityCode, String providerType, String errorCode, long latencyMs) {
        Counter.builder("ai.calls.total")
                .tag("status", "failed")
                .tag("ability", abilityCode)
                .tag("provider", providerType)
                .tag("error", errorCode)
                .description("Total AI calls")
                .register(meterRegistry)
                .increment();

        Counter.builder("ai.calls.errors")
                .tag("ability", abilityCode)
                .tag("provider", providerType)
                .tag("error", errorCode)
                .description("AI call errors")
                .register(meterRegistry)
                .increment();

        Timer.builder("ai.call.duration")
                .tag("ability", abilityCode)
                .tag("provider", providerType)
                .tag("status", "failed")
                .description("AI call duration")
                .register(meterRegistry)
                .record(latencyMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 记录流式调用开始
     */
    public void recordStreamStart(String abilityCode, String providerType) {
        Counter.builder("ai.stream.starts")
                .tag("ability", abilityCode)
                .tag("provider", providerType)
                .description("AI stream starts")
                .register(meterRegistry)
                .increment();
    }

    /**
     * 记录流式调用完成
     */
    public void recordStreamComplete(String abilityCode, String providerType, long durationMs) {
        Counter.builder("ai.stream.completions")
                .tag("ability", abilityCode)
                .tag("provider", providerType)
                .description("AI stream completions")
                .register(meterRegistry)
                .increment();

        Timer.builder("ai.stream.duration")
                .tag("ability", abilityCode)
                .tag("provider", providerType)
                .description("AI stream duration")
                .register(meterRegistry)
                .record(durationMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 记录流式调用失败
     */
    public void recordStreamFailure(String abilityCode, String providerType, String errorCode) {
        Counter.builder("ai.stream.failures")
                .tag("ability", abilityCode)
                .tag("provider", providerType)
                .tag("error", errorCode)
                .description("AI stream failures")
                .register(meterRegistry)
                .increment();
    }

    /**
     * 记录缓存命中
     */
    public void recordCacheHit(String cacheType) {
        Counter.builder("ai.cache.hits")
                .tag("cache", cacheType)
                .description("AI cache hits")
                .register(meterRegistry)
                .increment();
    }

    /**
     * 记录缓存未命中
     */
    public void recordCacheMiss(String cacheType) {
        Counter.builder("ai.cache.misses")
                .tag("cache", cacheType)
                .description("AI cache misses")
                .register(meterRegistry)
                .increment();
    }

    /**
     * 记录速率限制触发
     */
    public void recordRateLimit(String clientIp, String userId, String abilityCode) {
        Counter.builder("ai.rate_limit.triggers")
                .tag("client", clientIp)
                .tag("user", userId != null ? userId : "anonymous")
                .tag("ability", abilityCode)
                .description("AI rate limit triggers")
                .register(meterRegistry)
                .increment();
    }
}