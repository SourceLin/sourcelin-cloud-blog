package com.sourcelin.blog.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.constant.ServiceNameConstants;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import com.sourcelin.ai.api.domain.AiAbilityInvokeRequest;
import com.sourcelin.ai.api.domain.AiAbilityInvokeResult;
import com.sourcelin.ai.api.domain.AiTaskCreateRequest;
import com.sourcelin.ai.api.domain.AiTaskStatusVO;
import com.sourcelin.ai.api.service.RemoteAiService;
import com.sourcelin.blog.domain.Article;
import com.sourcelin.blog.domain.BlogAiRecord;
import com.sourcelin.blog.mapper.ArticleMapper;
import com.sourcelin.blog.mapper.BlogAiRecordMapper;
import com.sourcelin.blog.mapper.TagMapper;
import com.sourcelin.blog.config.BlogAiProperties;
import com.sourcelin.blog.service.IBlogAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class BlogAiServiceImpl implements IBlogAiService {
    private static final Logger log = LoggerFactory.getLogger(BlogAiServiceImpl.class);
    private static final MediaType SSE_JSON_MEDIA_TYPE = new MediaType("application", "json", StandardCharsets.UTF_8);

    @Autowired
    private BlogAiProperties blogAiProperties;

    @Autowired
    private RemoteAiService remoteAiService;

    @Autowired
    private BlogAiRecordMapper blogAiRecordMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    @Qualifier("loadBalancedRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private com.sourcelin.blog.service.IUserService userService;

    @Autowired
    private com.sourcelin.blog.support.BlogRichTextSanitizer sanitizer;

    @Autowired
    @Qualifier("blogAiStreamExecutor")
    private java.util.concurrent.Executor streamExecutor;

    @Override
    public AiAbilityInvokeResult invokeAbility(String abilityCode, String bizId, Map<String, Object> input, String userId) {
        requireAiEnabled();

        authorizeAbility(abilityCode, bizId, userId);
        Map<String, Object> finalInput = new HashMap<>(input != null ? input : Collections.emptyMap());
        if (bizId != null && !bizId.isEmpty() && (!finalInput.containsKey("title") || !finalInput.containsKey("content"))) {
            try {
                Long articleId = Long.parseLong(bizId);
                Article article = articleMapper.selectArticleById(articleId);
                if (article != null) {
                    if (!finalInput.containsKey("title")) {
                        finalInput.put("title", article.getTitle());
                    }
                    if (!finalInput.containsKey("content")) {
                        finalInput.put("content", article.getContent());
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to load article context for bizId: " + bizId, e);
            }
        }

        if ("article_tags".equalsIgnoreCase(abilityCode)) {
            try {
                List<com.sourcelin.blog.domain.Tag> tags = tagMapper.selectTagAll();
                List<Map<String, Object>> tagOptionsList = new ArrayList<>();
                if (tags != null) {
                    for (com.sourcelin.blog.domain.Tag tag : tags) {
                        Map<String, Object> option = new HashMap<>();
                        option.put("id", tag.getId());
                        option.put("name", tag.getName());
                        tagOptionsList.add(option);
                    }
                }
                finalInput.put("tagOptions", JSON.toJSONString(tagOptionsList));
            } catch (Exception e) {
                log.warn("Failed to load tag options for article_tags ability", e);
            }
        }

        AiAbilityInvokeRequest invokeRequest = new AiAbilityInvokeRequest();
        invokeRequest.setRequestId("req_blog_" + UUID.randomUUID().toString().replace("-", ""));
        invokeRequest.setAppCode("blog");
        invokeRequest.setDomainCode("article");
        invokeRequest.setAbilityCode(abilityCode);
        invokeRequest.setBizId(bizId);
        invokeRequest.setUserId(userId);
        invokeRequest.setInput(finalInput);

        log.info("Sending Feign request to sourcelin-ai. RequestId: {}, AbilityCode: {}", invokeRequest.getRequestId(), abilityCode);
        ApiResponse<AiAbilityInvokeResult> apiResponse = remoteAiService.invoke(abilityCode, invokeRequest, SecurityConstants.INNER);

        if (apiResponse == null || apiResponse.getCode() != 0 || apiResponse.getData() == null) {
            String errorMsg = apiResponse != null ? apiResponse.getMessage() : "Unknown gateway error";
            throw new RuntimeException("Remote AI service invoke failed: " + errorMsg);
        }

        AiAbilityInvokeResult invokeResult = apiResponse.getData();
        if (invokeResult.isSuccess()) {
            BlogAiRecord record = new BlogAiRecord();
            record.setAbilityCode(abilityCode);
            record.setBizId(bizId);
            record.setAiResult(invokeResult.getContent());
            record.setStatus("pending");
            record.setOwnerId(userId);
            blogAiRecordMapper.insert(record);

            invokeResult.setRecordId(record.getId());
        }

        return invokeResult;
    }

    @Override
    public AiTaskStatusVO createAsyncTask(AiTaskCreateRequest request, String userId) {
        requireAiEnabled();
        if (request == null || request.getAbilityCode() == null || request.getAbilityCode().isEmpty()) {
            throw new IllegalArgumentException("abilityCode is required");
        }

        String bizId = request.getBizId();
        authorizeAbility(request.getAbilityCode(), bizId, userId);
        request.setTenantId(null);
        Map<String, Object> finalInput = new HashMap<>(request.getInput() != null ? request.getInput() : Collections.emptyMap());
        enrichArticleContext(bizId, finalInput);

        if ("article_cover_generate".equalsIgnoreCase(request.getAbilityCode())) {
            if (finalInput.containsKey("content")) {
                finalInput.put("content", plainTextExcerpt(String.valueOf(finalInput.get("content")), 800));
            }
            if (!finalInput.containsKey("size")) {
                finalInput.put("size", "1024x768");
            }
        }

        request.setUserId(userId);
        request.setInput(finalInput);

        log.info("Creating async AI task. AbilityCode: {}, BizId: {}", request.getAbilityCode(), bizId);
        ApiResponse<AiTaskStatusVO> apiResponse = remoteAiService.createTask(request, SecurityConstants.INNER);
        if (apiResponse == null || apiResponse.getCode() != 0 || apiResponse.getData() == null) {
            String errorMsg = apiResponse != null ? apiResponse.getMessage() : "Unknown gateway error";
            throw new RuntimeException("Remote AI async task creation failed: " + errorMsg);
        }
        return apiResponse.getData();
    }

    @Override
    public AiTaskStatusVO getAsyncTaskStatus(String taskId, String userId) {
        requireAiEnabled();
        ApiResponse<AiTaskStatusVO> apiResponse = remoteAiService.getTaskStatus(taskId, SecurityConstants.INNER);
        if (apiResponse == null || apiResponse.getCode() != 0) {
            String errorMsg = apiResponse != null ? apiResponse.getMessage() : "Unknown gateway error";
            throw new RuntimeException("Remote AI task status query failed: " + errorMsg);
        }
        AiTaskStatusVO task = apiResponse.getData();
        if (task == null) throw new com.sourcelin.common.core.exception.BusinessException(com.sourcelin.common.core.enums.ResultCode.NOT_FOUND);
        if (!isAdmin(userId) && !Objects.equals(userId, task.getUserId()))
            throw new com.sourcelin.common.core.exception.BusinessException(com.sourcelin.common.core.enums.ResultCode.FORBIDDEN);
        return task;
    }

    private void enrichArticleContext(String bizId, Map<String, Object> finalInput) {
        if (bizId == null || bizId.isEmpty()) {
            return;
        }
        if (finalInput.containsKey("title") && finalInput.containsKey("content") && finalInput.containsKey("summary")) {
            return;
        }
        try {
            Long articleId = Long.parseLong(bizId);
            Article article = articleMapper.selectArticleById(articleId);
            if (article == null) {
                return;
            }
            if (!finalInput.containsKey("title")) {
                finalInput.put("title", article.getTitle());
            }
            if (!finalInput.containsKey("content")) {
                finalInput.put("content", article.getContent());
            }
            if (!finalInput.containsKey("summary")) {
                finalInput.put("summary", article.getSummary());
            }
        } catch (Exception e) {
            log.warn("Failed to load article context for bizId: " + bizId, e);
        }
    }

    private String plainTextExcerpt(String content, int maxLen) {
        if (content == null) {
            return "";
        }
        String plain = content.replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim();
        if (plain.length() <= maxLen) {
            return plain;
        }
        return plain.substring(0, maxLen) + "...";
    }

    @Override
    public SseEmitter invokeStream(String abilityCode, String bizId, Map<String, Object> input, String userId) {
        requireAiEnabled();

        authorizeAbility(abilityCode, bizId, userId);
        SseEmitter emitter = new SseEmitter(300_000L); // 5 分钟超时

        // 使用 @Async 线程池异步执行 SSE 中继，避免阻塞 Controller 线程
        streamExecutor.execute(() -> relaySseStream(emitter, abilityCode, bizId, input, userId));

        return emitter;
    }

    /**
     * 异步 SSE 中继：向 sourcelin-ai 发起流式请求，逐行透传 SSE 事件给前端
     */
    public void relaySseStream(SseEmitter emitter, String abilityCode, String bizId,
                                Map<String, Object> input, String userId) {
        try {
            String requestId = "req_blog_" + UUID.randomUUID().toString().replace("-", "");

            Map<String, Object> finalInput = new HashMap<>(input != null ? input : Collections.emptyMap());
            Object session = finalInput.get("session_id");
            if (session != null) {
                String value = String.valueOf(session);
                if (value.length() > 150) throw new com.sourcelin.common.core.exception.BusinessException(com.sourcelin.common.core.enums.ResultCode.BAD_REQUEST);
                finalInput.put("session_id", userId + ":" + value);
            }

            AiAbilityInvokeRequest invokeRequest = new AiAbilityInvokeRequest();
            invokeRequest.setRequestId(requestId);
            invokeRequest.setAppCode("blog");
            invokeRequest.setDomainCode("article");
            invokeRequest.setAbilityCode(abilityCode);
            invokeRequest.setBizId(bizId);
            invokeRequest.setUserId(userId);
            invokeRequest.setInput(finalInput);

            String aiServiceUrl = "http://" + ServiceNameConstants.AI_SERVICE + "/abilities/" + abilityCode + "/invoke/stream";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(SecurityConstants.FROM_SOURCE, SecurityConstants.INNER);

            restTemplate.execute(
                aiServiceUrl,
                HttpMethod.POST,
                request -> {
                    request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    request.getHeaders().set(SecurityConstants.FROM_SOURCE, SecurityConstants.INNER);
                    request.getBody().write(JSON.toJSONBytes(invokeRequest));
                },
                response -> {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("data:")) {
                                String data = line.substring(5).trim();
                                if (data.isEmpty() || "[DONE]".equals(data)) {
                                    continue;
                                }
                                try {
                                    com.alibaba.fastjson2.JSONObject event =
                                        JSON.parseObject(data);
                                    emitter.send(SseEmitter.event()
                                        .name("message")
                                        .data(event.toJSONString(), SSE_JSON_MEDIA_TYPE));
                                } catch (Exception e) {
                                    emitter.send(SseEmitter.event()
                                        .name("message")
                                        .data(data, SSE_JSON_MEDIA_TYPE));
                                }
                            }
                        }
                        emitter.complete();
                    } catch (Exception e) {
                        log.error("SSE relay read error", e);
                        emitter.completeWithError(e);
                    }
                    return null;
                }
            );
        } catch (Exception e) {
            log.error("AI stream invoke failed. Code: " + abilityCode, e);
            try {
                com.alibaba.fastjson2.JSONObject errorObj = new com.alibaba.fastjson2.JSONObject();
                errorObj.put("status", "error");
                errorObj.put("content", "");
                errorObj.put("error", e.getMessage());
                emitter.send(SseEmitter.event()
                    .name("message").data(errorObj.toJSONString(), SSE_JSON_MEDIA_TYPE));
                emitter.complete();
            } catch (Exception ex) {
                log.error("Failed to send error SSE event", ex);
                emitter.completeWithError(ex);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean adoptResult(Long recordId, String userId) {
        requireAiEnabled();
        BlogAiRecord record = blogAiRecordMapper.selectById(recordId);
        if (record == null) {
            throw new IllegalArgumentException("AI record not found: " + recordId);
        }
        authorizeRecord(record, userId);
        if (!"pending".equals(record.getStatus())) {
            throw new IllegalStateException("AI record status is " + record.getStatus() + ", cannot adopt.");
        }

        String abilityCode = record.getAbilityCode();
        String bizId = record.getBizId();
        String aiResult = record.getAiResult();

        if (bizId == null || bizId.isEmpty() || aiResult == null || aiResult.isEmpty()) {
            throw new IllegalStateException("Invalid record content, unable to adopt.");
        }

        Long articleId = Long.parseLong(bizId);
        Article article = articleMapper.selectArticleById(articleId);
        if (article == null || Integer.valueOf(1).equals(article.getDeleted())) {
            throw new com.sourcelin.common.core.exception.BusinessException(com.sourcelin.common.core.enums.ResultCode.NOT_FOUND);
        }
        authorizeArticle(article, userId);

        if ("article_summary".equalsIgnoreCase(abilityCode)) {
            Article updateObj = new Article();
            updateObj.setId(articleId);
            updateObj.setSummary(sanitizer.sanitizePlainText(aiResult));
            articleMapper.updateArticle(updateObj);
        } else if ("article_title_generate".equalsIgnoreCase(abilityCode)) {
            Article updateObj = new Article();
            updateObj.setId(articleId);
            updateObj.setTitle(sanitizer.sanitizePlainText(aiResult));
            articleMapper.updateArticle(updateObj);
        } else if ("article_tags".equalsIgnoreCase(abilityCode)) {
            List<Long> tagIds = new ArrayList<>();
            try {
                if (aiResult.trim().startsWith("[")) {
                    JSONArray array = JSON.parseArray(aiResult);
                    for (int i = 0; i < array.size(); i++) {
                        tagIds.add(array.getLong(i));
                    }
                } else {
                    String[] ids = aiResult.split(",");
                    for (String idStr : ids) {
                        if (!idStr.trim().isEmpty()) {
                            tagIds.add(Long.parseLong(idStr.trim()));
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Failed to parse AI tags: " + aiResult, e);
                throw new IllegalArgumentException("Failed to parse tags format. Raw result: " + aiResult);
            }

            if (!tagIds.isEmpty()) {
                tagMapper.deleteByArticleIds(Collections.singletonList(articleId));
                tagMapper.saveArticleTags(articleId, tagIds);
            }
        } else if ("article_cover_generate".equalsIgnoreCase(abilityCode)) {
            Article updateObj = new Article();
            updateObj.setId(articleId);
            updateObj.setAvatar(aiResult.trim());
            articleMapper.updateArticle(updateObj);
        } else if ("article_content_refine".equalsIgnoreCase(abilityCode)) {
            Article updateObj = new Article(); updateObj.setId(articleId);
            updateObj.setContent(sanitizer.sanitizeContent(aiResult)); articleMapper.updateArticle(updateObj);
        } else {
            throw new UnsupportedOperationException("Adopting capability '" + abilityCode + "' is not supported yet.");
        }

        record.setStatus("adopted");
        record.setAdoptUserId(userId);
        record.setAdoptTime(new Date());
        blogAiRecordMapper.update(record);

        return true;
    }

    @Override
    public boolean discardResult(Long recordId, String userId) {
        requireAiEnabled();
        BlogAiRecord record = blogAiRecordMapper.selectById(recordId);
        if (record == null) {
            throw new IllegalArgumentException("AI record not found: " + recordId);
        }
        authorizeRecord(record, userId);
        if (!"pending".equals(record.getStatus())) {
            throw new IllegalStateException("AI record status is " + record.getStatus() + ", cannot discard.");
        }

        record.setStatus("discarded");
        blogAiRecordMapper.update(record);
        return true;
    }

    @Override
    public List<BlogAiRecord> selectRecordList(BlogAiRecord query) {
        requireAiEnabled();
        return blogAiRecordMapper.selectList(query);
    }

    private void requireAiEnabled() {
        if (!blogAiProperties.isEnabled()) {
            throw new com.sourcelin.common.core.exception.BusinessException(com.sourcelin.common.core.enums.ResultCode.CONFLICT, "AI 功能未启用");
        }
    }

    private static boolean isAdmin(String principal) { return principal != null && principal.startsWith("admin:"); }

    private void authorizeRecord(BlogAiRecord record, String principal) {
        if (!isAdmin(principal) && (principal == null || !principal.equals(record.getOwnerId())))
            throw new com.sourcelin.common.core.exception.BusinessException(com.sourcelin.common.core.enums.ResultCode.FORBIDDEN);
    }

    private void authorizeArticle(Article article, String principal) {
        if (!isAdmin(principal) && !Objects.equals("blog:" + article.getUserId(), principal))
            throw new com.sourcelin.common.core.exception.BusinessException(com.sourcelin.common.core.enums.ResultCode.FORBIDDEN, "只能操作自己的文章");
    }

    private void authorizeAbility(String abilityCode, String bizId, String principal) {
        Set<String> allowed = new HashSet<>(Arrays.asList("ai_agent_chat", "article_title_generate", "article_summary", "article_tags", "article_cover_generate", "article_content_refine"));
        if (!isAdmin(principal) && !allowed.contains(abilityCode))
            throw new com.sourcelin.common.core.exception.BusinessException(com.sourcelin.common.core.enums.ResultCode.FORBIDDEN);
        if ("ai_agent_chat".equals(abilityCode)) return;
        if (!isAdmin(principal)) {
            if (principal == null || !principal.startsWith("blog:"))
                throw new com.sourcelin.common.core.exception.BusinessException(com.sourcelin.common.core.enums.ResultCode.UNAUTHORIZED);
            com.sourcelin.blog.api.domain.User user = userService.selectUserById(Long.valueOf(principal.substring(5)));
            if (user == null || !Integer.valueOf(2).equals(user.getUserType()))
                throw new com.sourcelin.common.core.exception.BusinessException(com.sourcelin.common.core.enums.ResultCode.FORBIDDEN, "只有博主可以使用文章创作能力");
        }
        if (bizId != null && !bizId.isEmpty()) {
            Article article;
            try { article = articleMapper.selectArticleById(Long.valueOf(bizId)); }
            catch (NumberFormatException e) { throw new com.sourcelin.common.core.exception.BusinessException(com.sourcelin.common.core.enums.ResultCode.BAD_REQUEST); }
            if (article == null || Integer.valueOf(1).equals(article.getDeleted()))
                throw new com.sourcelin.common.core.exception.BusinessException(com.sourcelin.common.core.enums.ResultCode.NOT_FOUND);
            authorizeArticle(article, principal);
        }
    }
}
