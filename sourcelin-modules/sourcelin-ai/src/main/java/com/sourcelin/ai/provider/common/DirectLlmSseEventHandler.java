package com.sourcelin.ai.provider.common;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.sourcelin.ai.domain.AiAbilityConfig;
import com.sourcelin.ai.service.AiCallLogService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * DirectLlm SSE事件处理器
 */
public class DirectLlmSseEventHandler extends SseEventHandler {

    public DirectLlmSseEventHandler(SseEmitter emitter, AiAbilityConfig config, Map<String, Object> inputs,
                                    String bizId, String requestId, AiCallLogService aiCallLogService,
                                    AiStreamResultCallback callback) {
        super(emitter, config, inputs, bizId, requestId, aiCallLogService, callback);
    }

    @Override
    protected String extractText(JSONObject json) {
        JSONArray choices = json.getJSONArray("choices");
        if (choices != null && !choices.isEmpty()) {
            JSONObject choice = choices.getJSONObject(0);
            JSONObject delta = choice.getJSONObject("delta");
            if (delta != null && delta.containsKey("content")) {
                return delta.getString("content");
            }
        }
        return null;
    }

    @Override
    protected boolean isStreamFinished(JSONObject json) {
        // 检查 choices[0].finish_reason 是否为非空值（如 "stop"）
        JSONArray choices = json.getJSONArray("choices");
        if (choices != null && !choices.isEmpty()) {
            JSONObject choice = choices.getJSONObject(0);
            String finishReason = choice.getString("finish_reason");
            return finishReason != null && !finishReason.isEmpty();
        }
        return false;
    }
}
