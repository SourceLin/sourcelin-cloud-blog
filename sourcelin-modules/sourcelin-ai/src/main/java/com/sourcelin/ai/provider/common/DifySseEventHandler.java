package com.sourcelin.ai.provider.common;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sourcelin.ai.domain.AiAbilityConfig;
import com.sourcelin.ai.service.AiCallLogService;
import okhttp3.sse.EventSource;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * Dify SSE事件处理器
 */
public class DifySseEventHandler extends SseEventHandler {
    /** 从 Dify SSE 事件中捕获的 conversation_id */
    private String conversationId;

    public DifySseEventHandler(SseEmitter emitter, AiAbilityConfig config, Map<String, Object> inputs,
                               String bizId, String requestId, AiCallLogService aiCallLogService,
                               AiStreamResultCallback callback) {
        super(emitter, config, inputs, bizId, requestId, aiCallLogService, callback);
    }

    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        if (!"[DONE]".equals(data)) {
            try {
                JSONObject json = JSON.parseObject(data);
                if (json != null && json.containsKey("conversation_id")) {
                    this.conversationId = json.getString("conversation_id");
                }
            } catch (Exception e) {
                // 忽略解析失败，super.onEvent 会再解析
            }
        }
        super.onEvent(eventSource, id, type, data);
    }

    @Override
    protected void handleStreamComplete(EventSource eventSource) {
        // 将 conversationId 回写到 inputs，供 AiAdapterService 的 onCompletion 回调读取
        if (conversationId != null && inputs != null) {
            inputs.put("__ai_resp_conv_id", conversationId);
        }
        super.handleStreamComplete(eventSource);
    }

    @Override
    protected String extractText(JSONObject json) {
        String eventType = json.getString("event");
        String text = null;

        if ("text_chunk".equalsIgnoreCase(eventType)) {
            text = json.getString("text");
            if (text == null && json.containsKey("data")) {
                JSONObject dataObj = json.getJSONObject("data");
                if (dataObj != null) {
                    text = dataObj.getString("text");
                }
            }
        } else if ("message".equalsIgnoreCase(eventType)) {
            text = json.getString("answer");
        }

        return text;
    }

    @Override
    protected boolean isStreamFinished(JSONObject json) {
        String eventType = json.getString("event");
        return "workflow_finished".equalsIgnoreCase(eventType) || "message_end".equalsIgnoreCase(eventType);
    }

}
