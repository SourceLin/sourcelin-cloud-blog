package com.sourcelin.ai.provider.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.sourcelin.ai.domain.AiAbilityConfig;
import com.sourcelin.ai.domain.ProviderTypeEnum;
import com.sourcelin.ai.provider.AiProviderClient;
import com.sourcelin.ai.provider.ProviderConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 直连 OpenAI 兼容图像生成 API（如 Agnes Image /v1/images/generations）
 */
@Component
public class DirectImageProviderClient implements AiProviderClient {
    private static final Logger log = LoggerFactory.getLogger(DirectImageProviderClient.class);
    private static final String DEFAULT_SIZE = "1024x768";

    private final RestTemplate restTemplate = com.sourcelin.ai.config.AiHttpClients.create();

    @Override
    public boolean supports(ProviderTypeEnum providerType) {
        return providerType == ProviderTypeEnum.DIRECT_IMAGE;
    }

    @Override
    public String invoke(AiAbilityConfig config, Map<String, Object> inputs, String userId) throws Exception {
        String requestUrl = buildRequestUrl(config);
        String apiKey = requireApiKey(config);
        String modelName = requireModelName(config);
        String prompt = renderPrompt(config, inputs);
        String size = resolveSize(config, inputs);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey.trim());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelName);
        requestBody.put("prompt", prompt);
        requestBody.put("size", size);

        Map<String, Object> extraBody = new HashMap<>();
        extraBody.put("response_format", "url");
        requestBody.put("extra_body", extraBody);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        log.info("Calling Direct Image API. Endpoint: {}, Model: {}, Ability: {}", requestUrl, modelName, config.getAbilityCode());

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(requestUrl, requestEntity, String.class);
        String responseStr = responseEntity.getBody();
        log.debug("Direct Image API raw response: {}", responseStr);

        if (responseStr == null || responseStr.isEmpty()) {
            throw new RuntimeException("Direct Image API returned empty response");
        }

        JSONObject responseJson = JSON.parseObject(responseStr);
        if (responseJson.containsKey("error")) {
            JSONObject errorObj = responseJson.getJSONObject("error");
            String errorMsg = errorObj != null ? errorObj.getString("message") : "Unknown API error";
            throw new RuntimeException("Direct Image API error: " + errorMsg);
        }

        JSONArray data = responseJson.getJSONArray("data");
        if (data == null || data.isEmpty()) {
            throw new RuntimeException("No image data returned: " + responseStr);
        }

        JSONObject first = data.getJSONObject(0);
        String url = first.getString("url");
        if (url == null || url.trim().isEmpty()) {
            String b64 = first.getString("b64_json");
            if (b64 != null && !b64.trim().isEmpty()) {
                return "data:image/png;base64," + b64.trim();
            }
            throw new RuntimeException("No image url or base64 in response: " + responseStr);
        }
        return url.trim();
    }

    private String buildRequestUrl(AiAbilityConfig config) {
        String baseUrl = config.getEndpointUrl();
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new ProviderConfigurationException("Direct Image Endpoint URL is not configured for ability: " + config.getAbilityCode());
        }
        String url = baseUrl.trim();
        if (url.contains("/images/generations")) {
            return url;
        }
        if (url.endsWith("/v1")) {
            return url + "/images/generations";
        }
        if (url.endsWith("/")) {
            return url + "v1/images/generations";
        }
        return url + "/v1/images/generations";
    }

    private String requireApiKey(AiAbilityConfig config) {
        String apiKey = config.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new ProviderConfigurationException("Direct Image API Key is not configured for ability: " + config.getAbilityCode());
        }
        return apiKey;
    }

    private String requireModelName(AiAbilityConfig config) {
        String modelName = config.getProviderAppId();
        if (modelName == null || modelName.trim().isEmpty()) {
            throw new ProviderConfigurationException("Direct Image Model is not configured for ability: " + config.getAbilityCode());
        }
        return modelName.trim();
    }

    private String resolveSize(AiAbilityConfig config, Map<String, Object> inputs) {
        if (inputs != null && inputs.get("size") != null) {
            String size = String.valueOf(inputs.get("size")).trim();
            if (!size.isEmpty()) {
                return size;
            }
        }
        String workflowSize = config.getProviderWorkflowId();
        if (workflowSize != null && workflowSize.matches("\\d+x\\d+")) {
            return workflowSize.trim();
        }
        return DEFAULT_SIZE;
    }

    private String renderPrompt(AiAbilityConfig config, Map<String, Object> inputs) {
        String template = config.getPromptTemplate();
        if (template == null || template.trim().isEmpty()) {
            throw new ProviderConfigurationException("Prompt template is not configured for ability: " + config.getAbilityCode());
        }
        return replacePlaceholders(template, inputs);
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
}
