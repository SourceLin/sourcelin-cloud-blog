package com.sourcelin.ai.provider.common;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sourcelin.ai.domain.AiAbilityConfig;
import com.sourcelin.ai.domain.AiCallLog;
import com.sourcelin.ai.service.AiCallLogService;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 公共SSE事件处理器
 */
public abstract class SseEventHandler extends EventSourceListener {
    private static final Logger log = LoggerFactory.getLogger(SseEventHandler.class);
    private static final MediaType SSE_JSON_MEDIA_TYPE = new MediaType("application", "json", StandardCharsets.UTF_8);

    protected final SseEmitter emitter;
    protected final AiAbilityConfig config;
    protected final Map<String, Object> inputs;
    protected final String bizId;
    protected final String requestId;
    protected final AiCallLogService aiCallLogService;
    protected final AiStreamResultCallback callback;
    protected final StringBuilder fullResponseAccumulator = new StringBuilder();
    protected final long startTime = System.currentTimeMillis();

    public SseEventHandler(SseEmitter emitter, AiAbilityConfig config, Map<String, Object> inputs,
                          String bizId, String requestId, AiCallLogService aiCallLogService,
                          AiStreamResultCallback callback) {
        this.emitter = emitter;
        this.config = config;
        this.inputs = inputs;
        this.bizId = bizId;
        this.requestId = requestId;
        this.aiCallLogService = aiCallLogService;
        this.callback = callback;
    }

    @Override
    public void onOpen(EventSource eventSource, Response response) {
        log.info("SSE stream connection opened. RequestId: {}, Provider: {}", requestId, config.getProviderType());
    }

    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        // 处理 OpenAI 格式的流结束标记 [DONE]
        if ("[DONE]".equals(data)) {
            handleStreamComplete(eventSource);
            return;
        }
        try {
            JSONObject json = JSON.parseObject(data);
            if (json == null) return;

            String text = extractText(json);
            if (text != null && !text.isEmpty()) {
                fullResponseAccumulator.append(text);
                sendSseEvent("processing", text, null);
            }

            if (isStreamFinished(json)) {
                handleStreamComplete(eventSource);
            }
        } catch (Exception e) {
            log.warn("Failed to parse SSE event data: " + data, e);
        }
    }

    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        String errorMsg = resolveFailureMessage(t, response);
        if (t != null) {
            log.error("SSE stream failed for requestId: " + requestId, t);
        } else {
            log.error("SSE stream failed for requestId: {}, {}", requestId, errorMsg);
        }
        sendSseEventError(errorMsg);
        emitter.complete();

        long latency = System.currentTimeMillis() - startTime;
        triggerAsyncLog(fullResponseAccumulator.toString(), "failed", "STREAM_ERROR", errorMsg, latency);
    }

    private String resolveFailureMessage(Throwable t, Response response) {
        String message = t != null ? t.getMessage() : null;
        if (response == null) {
            return message != null && !message.isEmpty() ? message : "SSE stream failed";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Provider returned HTTP ").append(response.code());
        String responseMessage = response.message();
        if (responseMessage != null && !responseMessage.isEmpty()) {
            builder.append(" ").append(responseMessage);
        }
        String body = readResponseBody(response);
        if (body != null && !body.isEmpty()) {
            builder.append(": ").append(body);
        } else if (message != null && !message.isEmpty()) {
            builder.append(": ").append(message);
        }
        return builder.toString();
    }

    private String readResponseBody(Response response) {
        try {
            if (response.body() == null) {
                return null;
            }
            return response.body().string();
        } catch (IOException e) {
            log.warn("Failed to read SSE failure response body, requestId: {}", requestId);
            return null;
        }
    }

    /**
     * 从JSON中提取文本内容（子类实现）
     */
    protected abstract String extractText(JSONObject json);

    /**
     * 判断流是否结束（子类实现）
     */
    protected abstract boolean isStreamFinished(JSONObject json);

    /**
     * 处理流完成：归档结果、发送完成事件、关闭连接
     */
    protected void handleStreamComplete(EventSource eventSource) {
        String fullText = fullResponseAccumulator.toString();
        Long recordId = archiveResult(fullText);

        sendSseEvent("completed", "", recordId);
        eventSource.cancel();
        emitter.complete();

        long latency = System.currentTimeMillis() - startTime;
        triggerAsyncLog(fullText, "success", null, null, latency);
    }

    /**
     * 归档AI结果
     */
    protected Long archiveResult(String fullText) {
        if (bizId == null || bizId.isEmpty() || fullText.isEmpty()) {
            return null;
        }
        try {
            if (callback != null) {
                return callback.onComplete(fullText);
            }
        } catch (Exception e) {
            log.error("Failed to archive stream record via callback for requestId: " + requestId, e);
        }
        return null;
    }

    /**
     * 发送SSE事件
     */
    protected void sendSseEvent(String status, String content, Long recordId) {
        try {
            JSONObject event = new JSONObject();
            event.put("status", status);
            event.put("content", content);
            if (recordId != null) {
                event.put("recordId", recordId);
            }
            event.put("requestId", requestId);
            emitter.send(SseEmitter.event()
                    .name("message").data(event.toJSONString(), SSE_JSON_MEDIA_TYPE));
        } catch (Exception e) {
            log.warn("Client disconnected during SSE stream pushing, requestId: {}", requestId);
        }
    }

    /**
     * 发送SSE错误事件
     */
    protected void sendSseEventError(String errorMsg) {
        try {
            JSONObject event = new JSONObject();
            event.put("status", "error");
            event.put("content", "");
            event.put("error", errorMsg);
            event.put("requestId", requestId);
            emitter.send(SseEmitter.event()
                    .name("message").data(event.toJSONString(), SSE_JSON_MEDIA_TYPE));
        } catch (Exception e) {
            log.warn("Failed to send SSE error event, requestId: {}", requestId);
        }
    }

    /**
     * 异步记录日志
     */
    protected void triggerAsyncLog(String fullText, String status, String errorCode, String errorMsg, long latency) {
        try {
            AiCallLog callLog = new AiCallLog();
            callLog.setRequestId(requestId);
            callLog.setAppCode(config.getAppCode());
            callLog.setDomainCode(config.getDomainCode());
            callLog.setAbilityCode(config.getAbilityCode());
            callLog.setBizId(bizId);
            callLog.setUserId("sse_user");
            callLog.setInvokeMode("sse");
            callLog.setProviderType(config.getProviderType());
            callLog.setRequestSummary(JSON.toJSONString(inputs));
            callLog.setResponseSummary(fullText);
            callLog.setStatus(status);
            callLog.setErrorCode(errorCode);
            callLog.setErrorMessage(errorMsg);
            callLog.setLatencyMs(latency);
            aiCallLogService.insert(callLog);
        } catch (Exception e) {
            log.error("Failed to write async call log for stream", e);
        }
    }
}
