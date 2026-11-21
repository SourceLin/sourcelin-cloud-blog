package com.sourcelin.ai.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sourcelin.ai.api.domain.DifyKnowledgeDocumentSyncRequest;
import com.sourcelin.ai.api.domain.DifyKnowledgeDocumentSyncResult;
import com.sourcelin.ai.domain.AiAbilityConfig;
import com.sourcelin.ai.service.AiAbilityRouter;
import com.sourcelin.ai.service.DifyKnowledgeService;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Dify Knowledge API 文档同步实现。
 */
@Service
public class DifyKnowledgeServiceImpl implements DifyKnowledgeService {
    private static final Logger log = LoggerFactory.getLogger(DifyKnowledgeServiceImpl.class);
    private static final String DEFAULT_ABILITY_CODE = "dify_knowledge_article_sync";
    private static final String ACTION_DELETE = "DELETE";
    private static final String ACTION_UPSERT = "UPSERT";

    private final AiAbilityRouter aiAbilityRouter;
    private final RestTemplate restTemplate = com.sourcelin.ai.config.AiHttpClients.create();

    public DifyKnowledgeServiceImpl(AiAbilityRouter aiAbilityRouter) {
        this.aiAbilityRouter = aiAbilityRouter;
    }

    @Override
    public DifyKnowledgeDocumentSyncResult syncDocument(DifyKnowledgeDocumentSyncRequest request) {
        DifyKnowledgeDocumentSyncResult result = new DifyKnowledgeDocumentSyncResult();
        if (request != null) {
            result.setAction(request.getAction());
            result.setSourceType(request.getSourceType());
            result.setSourceId(request.getSourceId());
            result.setDocumentId(request.getDocumentId());
            result.setContentHash(request.getContentHash());
        }

        try {
            validateRequest(request);
            AiAbilityConfig config = resolveConfig(request);
            String baseUrl = normalizeDifyApiBaseUrl(config.getEndpointUrl());
            String datasetId = config.getProviderAppId();
            String apiKey = config.getApiKey();

            if (ACTION_DELETE.equalsIgnoreCase(request.getAction())) {
                return deleteDocument(request, result, baseUrl, datasetId, apiKey);
            }
            return upsertDocument(request, result, baseUrl, datasetId, apiKey);
        } catch (Exception e) {
            log.error("Dify knowledge document sync failed, sourceType={}, sourceId={}",
                    request == null ? null : request.getSourceType(),
                    request == null ? null : request.getSourceId(), e);
            result.setSuccess(false);
            result.setErrorCode("DIFY_KNOWLEDGE_SYNC_FAILED");
            result.setErrorMessage(e.getMessage());
            return result;
        }
    }

    private DifyKnowledgeDocumentSyncResult upsertDocument(DifyKnowledgeDocumentSyncRequest request,
                                                           DifyKnowledgeDocumentSyncResult result,
                                                           String baseUrl,
                                                           String datasetId,
                                                           String apiKey) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", request.getName());
        body.put("text", request.getText());
        body.put("indexing_technique", "high_quality");
        body.put("doc_form", "text_model");
        body.put("doc_language", "Chinese");

        String path;
        if (StringUtils.isNotBlank(request.getDocumentId())) {
            path = baseUrl + "/datasets/" + datasetId + "/documents/" + request.getDocumentId() + "/update-by-text";
        } else {
            path = baseUrl + "/datasets/" + datasetId + "/document/create-by-text";
        }

        ResponseEntity<String> response = restTemplate.postForEntity(path, new HttpEntity<>(body, buildHeaders(apiKey)), String.class);
        JSONObject responseJson = JSON.parseObject(response.getBody());
        JSONObject document = responseJson == null ? null : responseJson.getJSONObject("document");
        if (document == null) {
            throw new BusinessException(ResultCode.REMOTE_SERVICE_ERROR, "Dify Knowledge API 未返回 document 信息");
        }

        result.setSuccess(true);
        result.setAction(ACTION_UPSERT);
        result.setDocumentId(document.getString("id"));
        result.setBatch(responseJson.getString("batch"));
        result.setIndexingStatus(document.getString("indexing_status"));
        return result;
    }

    private DifyKnowledgeDocumentSyncResult deleteDocument(DifyKnowledgeDocumentSyncRequest request,
                                                           DifyKnowledgeDocumentSyncResult result,
                                                           String baseUrl,
                                                           String datasetId,
                                                           String apiKey) {
        if (StringUtils.isBlank(request.getDocumentId())) {
            result.setSuccess(true);
            result.setAction(ACTION_DELETE);
            result.setIndexingStatus("skipped");
            return result;
        }

        String path = baseUrl + "/datasets/" + datasetId + "/documents/" + request.getDocumentId();
        try {
            restTemplate.exchange(path, HttpMethod.DELETE, new HttpEntity<>(buildHeaders(apiKey)), String.class);
        } catch (HttpClientErrorException.NotFound e) {
            log.info("Dify document already missing, documentId={}", request.getDocumentId());
        }
        result.setSuccess(true);
        result.setAction(ACTION_DELETE);
        result.setDocumentId(request.getDocumentId());
        result.setIndexingStatus("deleted");
        return result;
    }

    private void validateRequest(DifyKnowledgeDocumentSyncRequest request) {
        if (request == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "Dify 知识库同步请求不能为空");
        }
        if (StringUtils.isBlank(request.getAction())) {
            request.setAction(ACTION_UPSERT);
        }
        if (!ACTION_UPSERT.equalsIgnoreCase(request.getAction()) && !ACTION_DELETE.equalsIgnoreCase(request.getAction())) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "不支持的 Dify 知识库同步动作: " + request.getAction());
        }
        if (ACTION_UPSERT.equalsIgnoreCase(request.getAction())) {
            if (StringUtils.isBlank(request.getName())) {
                throw new BusinessException(ResultCode.VALIDATION_ERROR, "Dify 知识库文档名称不能为空");
            }
            if (StringUtils.isBlank(request.getText())) {
                throw new BusinessException(ResultCode.VALIDATION_ERROR, "Dify 知识库文档内容不能为空");
            }
        }
    }

    private AiAbilityConfig resolveConfig(DifyKnowledgeDocumentSyncRequest request) {
        String abilityCode = StringUtils.isNotBlank(request.getAbilityCode())
                ? request.getAbilityCode()
                : DEFAULT_ABILITY_CODE;
        AiAbilityConfig config = aiAbilityRouter.getAbilityConfig(abilityCode);
        if (!"1".equals(config.getEnabled())) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "Dify 知识库同步能力未启用: " + abilityCode);
        }
        if (StringUtils.isBlank(config.getEndpointUrl())) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "Dify Knowledge API 地址未配置: " + abilityCode);
        }
        if (StringUtils.isBlank(config.getProviderAppId())) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "Dify 知识库 ID 未配置: " + abilityCode);
        }
        if (StringUtils.isBlank(config.getApiKey())) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "Dify Knowledge API Key 未配置: " + abilityCode);
        }
        return config;
    }

    private HttpHeaders buildHeaders(String apiKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "Sourcelin-Blog/1.0 Dify-Knowledge-Sync");
        headers.set("Authorization", "Bearer " + apiKey.trim());
        return headers;
    }

    private String normalizeDifyApiBaseUrl(String endpointUrl) {
        String baseUrl = endpointUrl.trim();
        while (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        if (baseUrl.endsWith("/chat-messages")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - "/chat-messages".length());
        }
        int v1Index = baseUrl.indexOf("/v1/");
        if (v1Index >= 0) {
            baseUrl = baseUrl.substring(0, v1Index + 3);
        }
        return baseUrl;
    }
}
