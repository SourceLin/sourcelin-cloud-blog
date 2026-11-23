package com.sourcelin.ai.provider.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sourcelin.ai.domain.AiAbilityConfig;
import com.sourcelin.ai.domain.ProviderTypeEnum;
import com.sourcelin.ai.provider.AiProviderClient;
import com.sourcelin.ai.provider.common.DirectLlmSseEventHandler;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DirectLlmProviderClient implements AiProviderClient {
    private static final Logger log = LoggerFactory.getLogger(DirectLlmProviderClient.class);
    private static final double DEFAULT_TEMPERATURE = 0.7;

    private final RestTemplate restTemplate = com.sourcelin.ai.config.AiHttpClients.create();

    @Autowired
    private com.sourcelin.ai.service.AiCallLogService aiCallLogService;

    @Autowired
    private OkHttpClient okHttpClient;

    @Override
    public boolean supports(ProviderTypeEnum providerType) {
        return providerType == ProviderTypeEnum.DIRECT_LLM;
    }

    @Override
    public String invoke(AiAbilityConfig config, Map<String, Object> inputs, String userId) throws Exception {
        String requestUrl = buildRequestUrl(config);
        String apiKey = requireApiKey(config);
        String modelName = requireModelName(config);
        RenderedPrompt rendered = renderPrompt(config, inputs);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey.trim());

        String sessionId = (inputs != null && inputs.get("session_id") != null) ? String.valueOf(inputs.get("session_id")) : null;
        Map<String, Object> requestBody = buildRequestBody(modelName, rendered.systemPrompt, rendered.userContent, DEFAULT_TEMPERATURE, false, sessionId);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        log.info("Calling Direct LLM API. Endpoint: {}, Model: {}, Ability: {}", requestUrl, modelName, config.getAbilityCode());

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(requestUrl, requestEntity, String.class);
            String responseStr = responseEntity.getBody();
            log.debug("Direct LLM API raw response: {}", responseStr);

            if (responseStr == null || responseStr.isEmpty()) {
                throw new RuntimeException("Direct LLM API returned empty response");
            }

            JSONObject responseJson = JSON.parseObject(responseStr);
            if (responseJson.containsKey("error")) {
                JSONObject errorObj = responseJson.getJSONObject("error");
                String errorMsg = errorObj != null ? errorObj.getString("message") : "Unknown API error";
                throw new RuntimeException("Direct LLM API error: " + errorMsg);
            }

            com.alibaba.fastjson2.JSONArray choices = responseJson.getJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("No choices returned from LLM: " + responseStr);
            }

            JSONObject choice = choices.getJSONObject(0);
            JSONObject message = choice.getJSONObject("message");
            if (message == null || !message.containsKey("content")) {
                throw new RuntimeException("No message content returned from LLM: " + responseStr);
            }

            return message.getString("content");
        } catch (Exception e) {
            log.error("Failed to call Direct LLM API for ability " + config.getAbilityCode(), e);
            throw e;
        }
    }

    @Override
    public void invokeStream(AiAbilityConfig config, Map<String, Object> inputs,
                              org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter,
                              String bizId, String requestId,
                              com.sourcelin.ai.provider.common.AiStreamResultCallback callback) throws Exception {
        String requestUrl = buildRequestUrl(config);
        String apiKey = requireApiKey(config);
        String modelName = requireModelName(config);
        RenderedPrompt rendered = renderPrompt(config, inputs);

        String sessionId = (inputs != null && inputs.get("session_id") != null) ? String.valueOf(inputs.get("session_id")) : null;
        Map<String, Object> requestBody = buildRequestBody(modelName, rendered.systemPrompt, rendered.userContent, DEFAULT_TEMPERATURE, true, sessionId);

        Request request = new Request.Builder()
                .url(requestUrl)
                .post(RequestBody.create(JSON.toJSONString(requestBody), MediaType.parse("application/json")))
                .header("Authorization", "Bearer " + apiKey.trim())
                .header("Accept", "text/event-stream")
                .build();

        EventSource.Factory factory = EventSources.createFactory(okHttpClient);

        factory.newEventSource(request, new DirectLlmSseEventHandler(emitter, config, inputs, bizId, requestId, aiCallLogService, callback));
    }

    // ---- 内部工具方法 ----

    private String buildRequestUrl(AiAbilityConfig config) {
        String baseUrl = config.getEndpointUrl();
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("DirectLLM Endpoint URL is not configured for ability: " + config.getAbilityCode());
        }
        String url = baseUrl.trim().replaceAll("/+$", "");
        if (!url.endsWith("/chat/completions")) {
            url += url.matches(".*/v[0-9]+") ? "/chat/completions" : "/v1/chat/completions";
        }
        return url;
    }

    private String requireApiKey(AiAbilityConfig config) {
        String apiKey = config.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("DirectLLM API Key is not configured for ability: " + config.getAbilityCode());
        }
        return apiKey;
    }

    private String requireModelName(AiAbilityConfig config) {
        String modelName = config.getProviderAppId();
        if (modelName == null || modelName.trim().isEmpty()) {
            throw new IllegalArgumentException("DirectLLM Model is not configured for ability: " + config.getAbilityCode());
        }
        return modelName.trim();
    }

    /**
     * 渲染 Prompt 模板，支持自动拆分 system prompt 和 user content。
     * 当模板中包含 {question} 占位符时，{question} 之前的部分作为 system prompt，
     * {question} 本身被替换为用户输入；否则整个模板作为 user content。
     */
    private RenderedPrompt renderPrompt(AiAbilityConfig config, Map<String, Object> inputs) {
        String template = config.getPromptTemplate();
        if (template == null || template.trim().isEmpty()) {
            throw new IllegalArgumentException("Prompt template is not configured for ability: " + config.getAbilityCode());
        }

        // 如果模板包含 {question}，拆分出 system prompt 和 user content
        if (template.contains("{question}")) {
            int idx = template.indexOf("{question}");
            String systemPrompt = template.substring(0, idx).trim();
            String userContent = template.substring(idx);
            userContent = replacePlaceholders(userContent, inputs);
            return new RenderedPrompt(systemPrompt, userContent);
        }

        // 否则整个模板作为 user content
        String result = replacePlaceholders(template, inputs);
        return new RenderedPrompt("", result);
    }

    private String replacePlaceholders(String template, Map<String, Object> inputs) {
        String result = template;
        if (inputs != null) {
            for (Map.Entry<String, Object> entry : inputs.entrySet()) {
                result = result.replace("{" + entry.getKey() + "}", entry.getValue() != null ? entry.getValue().toString() : "");
            }
        }
        return result;
    }

    private Map<String, Object> buildRequestBody(String modelName, String systemPrompt, String userContent, double temperature, boolean stream, String sessionId) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", modelName);
        body.put("stream", stream);
        body.put("temperature", temperature);
        if (sessionId != null && !sessionId.isEmpty()) {
            body.put("session_id", sessionId);
        }

        List<Map<String, String>> messages = new ArrayList<>();

        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);
        }

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userContent);
        messages.add(userMsg);

        body.put("messages", messages);
        return body;
    }

    /**
     * 渲染结果：拆分为 system prompt 和 user content
     */
    private static class RenderedPrompt {
        final String systemPrompt;
        final String userContent;
        RenderedPrompt(String systemPrompt, String userContent) {
            this.systemPrompt = systemPrompt;
            this.userContent = userContent;
        }
    }

    // ---- SSE 事件发送（供 EventHandler 回调使用） ----

    void sendSseEvent(org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter,
                      String status, String content, Long recordId, String requestId) {
        try {
            JSONObject event = new JSONObject();
            event.put("status", status);
            event.put("content", content);
            if (recordId != null) {
                event.put("recordId", recordId);
            }
            event.put("requestId", requestId);
            emitter.send(org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event()
                    .name("message").data(event.toJSONString()));
        } catch (Exception e) {
            log.warn("Client disconnected during SSE stream pushing, requestId: {}", requestId);
        }
    }

    void sendSseEventError(org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter,
                           String errorMsg, String requestId) {
        try {
            JSONObject event = new JSONObject();
            event.put("status", "error");
            event.put("content", "");
            event.put("error", errorMsg);
            event.put("requestId", requestId);
            emitter.send(org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event()
                    .name("message").data(event.toJSONString()));
        } catch (Exception e) {
            log.warn("Failed to send SSE error event, requestId: {}", requestId);
        }
    }

    @Async("aiLogExecutor")
    public void triggerAsyncLog(String requestId, AiAbilityConfig config, Map<String, Object> inputs,
                                 String fullText, String status, String errorCode, String errorMsg, long latency, String bizId) {
        try {
            com.sourcelin.ai.domain.AiCallLog callLog = new com.sourcelin.ai.domain.AiCallLog();
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
