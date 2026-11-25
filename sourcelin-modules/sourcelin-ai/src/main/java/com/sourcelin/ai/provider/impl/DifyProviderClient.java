package com.sourcelin.ai.provider.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sourcelin.ai.domain.AiAbilityConfig;
import com.sourcelin.ai.domain.ProviderTypeEnum;
import com.sourcelin.ai.provider.AiProviderClient;
import com.sourcelin.ai.provider.ProviderConfigurationException;
import com.sourcelin.ai.provider.common.DifySseEventHandler;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class DifyProviderClient implements AiProviderClient {
    private static final Logger log = LoggerFactory.getLogger(DifyProviderClient.class);
    private static final okhttp3.MediaType JSON_UTF8 = okhttp3.MediaType.parse("application/json; charset=utf-8");
    private final RestTemplate restTemplate = com.sourcelin.ai.config.AiHttpClients.create();

    @Autowired
    private com.sourcelin.ai.service.AiCallLogService aiCallLogService;

    @Autowired
    private OkHttpClient okHttpClient;

    @Override
    public boolean supports(ProviderTypeEnum providerType) {
        return providerType == ProviderTypeEnum.DIFY;
    }

    @Override
    public String invoke(AiAbilityConfig config, Map<String, Object> inputs, String userId) throws Exception {
        String endpointUrl = config.getEndpointUrl();
        if (endpointUrl == null || endpointUrl.trim().isEmpty()) {
            throw new ProviderConfigurationException("Dify Endpoint URL is not configured for ability: " + config.getAbilityCode());
        }

        String apiKey = config.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new ProviderConfigurationException("Dify API Key is not configured for ability: " + config.getAbilityCode());
        }

        if ("chat".equals(config.getChatMode())) {
            // --- Chat 模式 ---
            String[] q = {null}, sk = {null}, cid = {null}, uid = {null};
            Map<String, Object> difyInputs = extractChatParams(inputs, q, sk, cid, uid);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query", q[0] != null ? q[0] : "");
            requestBody.put("inputs", difyInputs);
            requestBody.put("response_mode", "blocking");
            requestBody.put("user", userId != null ? userId : "anonymous");
            if (cid[0] != null && !cid[0].isEmpty()) {
                requestBody.put("conversation_id", cid[0]);
            }

            HttpHeaders chatHeaders = new HttpHeaders();
            chatHeaders.setContentType(MediaType.APPLICATION_JSON);
            chatHeaders.set("Authorization", "Bearer " + apiKey.trim());

            HttpEntity<Map<String, Object>> chatEntity = new HttpEntity<>(requestBody, chatHeaders);
            log.info("Calling Dify chat. Endpoint: {}, Request: {}", endpointUrl, JSON.toJSONString(requestBody));

            try {
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(endpointUrl, chatEntity, String.class);
                String responseStr = responseEntity.getBody();
                log.info("Dify chat raw response: {}", responseStr);

                if (responseStr == null || responseStr.isEmpty()) {
                    throw new RuntimeException("Dify returned empty response");
                }

                JSONObject responseJson = JSON.parseObject(responseStr);
                if (responseJson.containsKey("code") && responseJson.getIntValue("status", 200) >= 400) {
                    throw new RuntimeException("Dify API error: " + responseJson.getString("message"));
                }

                return responseJson.getString("answer");
            } catch (Exception e) {
                log.error("Failed to call Dify chat", e);
                throw e;
            }
        } else {
            // --- Workflow 模式 ---
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey.trim());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", inputs != null ? inputs : new HashMap<>());
            requestBody.put("response_mode", "blocking");
            requestBody.put("user", userId != null ? userId : "anonymous");

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            log.info("Calling Dify workflow. Endpoint: {}, Request: {}", endpointUrl, JSON.toJSONString(requestBody));

            try {
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(endpointUrl, requestEntity, String.class);
                String responseStr = responseEntity.getBody();
                log.info("Dify workflow raw response: {}", responseStr);

                if (responseStr == null || responseStr.isEmpty()) {
                    throw new RuntimeException("Dify returned empty response");
                }

                JSONObject responseJson = JSON.parseObject(responseStr);
                if (responseJson.containsKey("code") && responseJson.getIntValue("status", 200) >= 400) {
                    throw new RuntimeException("Dify API error: " + responseJson.getString("message"));
                }

                JSONObject dataObj = responseJson.getJSONObject("data");
                if (dataObj == null) {
                    dataObj = responseJson;
                }

                JSONObject outputsObj = dataObj.getJSONObject("outputs");
                if (outputsObj == null || outputsObj.isEmpty()) {
                    if (responseJson.containsKey("answer")) {
                        return responseJson.getString("answer");
                    }
                    throw new RuntimeException("No ''outputs'' field found in Dify response: " + responseStr);
                }

                if (outputsObj.containsKey("text")) {
                    return outputsObj.getString("text");
                }
                if (outputsObj.containsKey("result")) {
                    return outputsObj.getString("result");
                }

                for (String key : outputsObj.keySet()) {
                    Object val = outputsObj.get(key);
                    if (val != null) {
                        return val.toString();
                    }
                }

                return outputsObj.toJSONString();
            } catch (Exception e) {
                log.error("Failed to call Dify workflow", e);
                throw e;
            }
        }
    }

    @Override
    public void invokeStream(AiAbilityConfig config, Map<String, Object> inputs,
                              org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter,
                              String bizId, String requestId,
                              com.sourcelin.ai.provider.common.AiStreamResultCallback callback) throws Exception {

        String endpointUrl = config.getEndpointUrl();
        if (endpointUrl == null || endpointUrl.trim().isEmpty()) {
            throw new ProviderConfigurationException("Dify Endpoint URL is not configured for ability: " + config.getAbilityCode());
        }

        String apiKey = config.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new ProviderConfigurationException("Dify API Key is not configured for ability: " + config.getAbilityCode());
        }

        if ("chat".equals(config.getChatMode())) {
            // --- Chat 模式 ---
            String[] q = {null}, sk = {null}, cid = {null}, uid = {null};
            Map<String, Object> difyInputs = extractChatParams(inputs, q, sk, cid, uid);

            String difyUser = uid[0];
            if (difyUser == null || "anonymous".equals(difyUser)) {
                difyUser = sk[0] != null ? sk[0] : "anonymous";
            }

            Map<String, Object> chatRequestBody = new HashMap<>();
            chatRequestBody.put("query", q[0] != null ? q[0] : "");
            chatRequestBody.put("inputs", difyInputs);
            chatRequestBody.put("response_mode", "streaming");
            chatRequestBody.put("user", difyUser);
            if (cid[0] != null && !cid[0].isEmpty()) {
                chatRequestBody.put("conversation_id", cid[0]);
            }

            Request chatRequest = new Request.Builder()
                    .url(endpointUrl.trim())
                    .post(RequestBody.create(JSON.toJSONString(chatRequestBody), JSON_UTF8))
                    .header("Authorization", "Bearer " + apiKey.trim())
                    .header("Accept", "text/event-stream")
                    .header("User-Agent", "Mozilla/5.0")
                    .build();

            EventSource.Factory chatFactory = EventSources.createFactory(okHttpClient);
            chatFactory.newEventSource(chatRequest, new DifySseEventHandler(emitter, config, inputs, bizId, requestId, aiCallLogService, callback));
        } else {
            // --- Workflow 模式（保持现有逻辑不变）---
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", inputs != null ? inputs : new HashMap<>());
            requestBody.put("response_mode", "streaming");
            requestBody.put("user", "sse_user");

            Request request = new Request.Builder()
                    .url(endpointUrl.trim())
                    .post(RequestBody.create(JSON.toJSONString(requestBody), JSON_UTF8))
                    .header("Authorization", "Bearer " + apiKey.trim())
                    .header("Accept", "text/event-stream")
                    .header("User-Agent", "Mozilla/5.0")
                    .build();

            EventSource.Factory factory = EventSources.createFactory(okHttpClient);
            factory.newEventSource(request, new DifySseEventHandler(emitter, config, inputs, bizId, requestId, aiCallLogService, callback));
        }
    }

    /**
     * 从 inputs 中提取 Chat 模式内部参数并移除 __ 前缀键和 query 字段，
     * 返回干净的 Dify inputs（不含内部约定键）
     */
    private Map<String, Object> extractChatParams(Map<String, Object> inputs,
            String[] outQuery, String[] outSessionKey,
            String[] outConversationId, String[] outUserId) {
        Map<String, Object> difyInputs = new HashMap<>(inputs != null ? inputs : Collections.emptyMap());
        outQuery[0] = (String) difyInputs.remove("question");
        outSessionKey[0] = (String) difyInputs.remove("__ai_session_key");
        outConversationId[0] = (String) difyInputs.remove("__ai_conv_id");
        outUserId[0] = (String) difyInputs.remove("__ai_user_id");
        difyInputs.remove("session_id");
        return difyInputs;
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
            log.warn("Client disconnected during Dify SSE pushing, requestId: {}", requestId);
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
            log.warn("Failed to send Dify SSE error event, requestId: {}", requestId);
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
            log.error("Failed to write async call log for Dify stream", e);
        }
    }
}
