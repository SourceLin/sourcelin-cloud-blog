package com.sourcelin.ai.service;

import com.alibaba.fastjson2.JSON;
import com.sourcelin.ai.api.domain.AiAbilityInvokeRequest;
import com.sourcelin.ai.api.domain.AiAbilityInvokeResult;
import com.sourcelin.ai.api.domain.AiTaskCreateRequest;
import com.sourcelin.ai.api.domain.AiTaskStatusVO;
import com.sourcelin.ai.domain.AiAbilityConfig;
import com.sourcelin.ai.domain.AiCallLog;
import com.sourcelin.ai.domain.AiTask;
import com.sourcelin.ai.domain.AiTaskEvent;
import com.sourcelin.ai.monitor.AiMetricsCollector;
import com.sourcelin.ai.provider.AiProviderClient;
import com.sourcelin.ai.util.SensitiveDataMasker;
import com.sourcelin.ai.feign.RemoteBlogAiCallbackService;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.domain.R;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.RejectedExecutionException;

@Service
public class AiAdapterService {
    private static final Logger log = LoggerFactory.getLogger(AiAdapterService.class);

    @Autowired
    private AiAbilityRouter aiAbilityRouter;

    @Autowired
    private AiCallLogService aiCallLogService;

    @Autowired
    private AiTaskService aiTaskService;

    @Autowired
    private AiMetricsCollector metricsCollector;

    @Autowired
    private AiConversationService aiConversationService;

    @Autowired
    private RemoteBlogAiCallbackService remoteBlogAiCallbackService;

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("aiTaskExecutor")
    private java.util.concurrent.Executor taskExecutor;

    /**
     * 同步调用 AI 能力
     */
    public AiAbilityInvokeResult invoke(String abilityCode, AiAbilityInvokeRequest request) {
        long startTime = System.currentTimeMillis();
        String requestId = request.getRequestId();
        if (requestId == null || requestId.isEmpty()) {
            requestId = "req_" + UUID.randomUUID().toString().replace("-", "");
        }

        AiAbilityInvokeResult result = new AiAbilityInvokeResult();
        result.setRequestId(requestId);
        result.setAbilityCode(abilityCode);
        result.setBizId(request.getBizId());
        result.setAdoptStatus("pending");

        AiAbilityConfig config = null;
        long latency = 0;
        AiCallLog callLog = new AiCallLog();
        callLog.setRequestId(requestId);
        callLog.setAppCode(request.getAppCode());
        callLog.setDomainCode(request.getDomainCode());
        callLog.setAbilityCode(abilityCode);
        callLog.setBizId(request.getBizId());
        callLog.setUserId(request.getUserId());
        callLog.setTenantId(request.getTenantId());
        callLog.setInvokeMode("sync");
        // 对请求摘要进行脱敏处理
        String requestSummary = JSON.toJSONString(request.getInput());
        callLog.setRequestSummary(SensitiveDataMasker.truncate(SensitiveDataMasker.maskText(requestSummary), 1000));

        try {
            config = aiAbilityRouter.getAbilityConfig(abilityCode);
            callLog.setProviderType(config.getProviderType());

            if (!"1".equals(config.getEnabled())) {
                throw new IllegalStateException("AI ability has been disabled: " + abilityCode);
            }

            AiProviderClient providerClient = aiAbilityRouter.route(config.getProviderType());
            String responseText = providerClient.invoke(config, request.getInput(), request.getUserId());

            result.setSuccess(true);
            result.setResultType("text");
            result.setContent(responseText);

            callLog.setStatus("success");
            // 对响应摘要进行脱敏处理
            callLog.setResponseSummary(SensitiveDataMasker.truncate(SensitiveDataMasker.maskText(responseText), 1000));

            // 记录成功监控指标
            metricsCollector.recordSuccess(abilityCode, config.getProviderType(), latency);
        } catch (Exception e) {
            log.error("AI ability invoke failed. Code: " + abilityCode, e);
            result.setSuccess(false);
            result.setErrorCode("INVOKE_FAILED");
            result.setErrorMessage(e.getMessage());

            callLog.setStatus("failed");
            callLog.setErrorCode("INVOKE_FAILED");
            callLog.setErrorMessage(e.getMessage());
            if (config != null) {
                callLog.setProviderType(config.getProviderType());
                // 记录失败监控指标
                metricsCollector.recordFailure(abilityCode, config.getProviderType(), "INVOKE_FAILED", latency);
            } else {
                callLog.setProviderType("unknown");
                // 记录失败监控指标
                metricsCollector.recordFailure(abilityCode, "unknown", "INVOKE_FAILED", latency);
            }
        } finally {
            latency = System.currentTimeMillis() - startTime;
            callLog.setLatencyMs(latency);
            try {
                aiCallLogService.insert(callLog);
            } catch (Exception ex) {
                log.error("Failed to save AI call log", ex);
            }
        }

        return result;
    }

    /**
     * 创建异步任务
     */
    public AiTaskStatusVO createTask(AiTaskCreateRequest request) {
        String taskId = "task_" + UUID.randomUUID().toString().replace("-", "");
        String requestId = "req_" + UUID.randomUUID().toString().replace("-", "");

        AiTask task = new AiTask();
        task.setTaskId(taskId);
        task.setRequestId(requestId);
        task.setAppCode("blog");
        task.setAbilityCode(request.getAbilityCode());
        task.setBizId(request.getBizId());
        task.setUserId(request.getUserId());
        task.setTenantId(request.getTenantId());
        task.setStatus("pending");
        task.setProgress(0);
        task.setInputSnapshot(JSON.toJSONString(request.getInput()));
        aiTaskService.insert(task);
        recordTaskEvent(taskId, "CREATED", "Task created successfully", null);
        AiTaskStatusVO accepted = convertToVO(task);
        return submitTask(task, request) ? accepted : convertToVO(task);
    }

    /**
     * 获取异步任务状态
     */
    public AiTaskStatusVO getTaskStatus(String taskId) {
        AiTask task = aiTaskService.selectByTaskId(taskId);
        if (task == null) {
            return null;
        }
        return convertToVO(task);
    }

    /**
     * 重试失败的 AI 异步任务。
     */
    public String retryTask(String taskId) {
        AiTask task = aiTaskService.selectByTaskId(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "任务不存在");
        }
        if (!"failed".equals(task.getStatus())) {
            throw new BusinessException(ResultCode.CONFLICT, "只有失败状态的任务才能重试");
        }
        if (task.getInputSnapshot() == null || task.getInputSnapshot().trim().isEmpty()) {
            throw new BusinessException(ResultCode.CONFLICT, "任务缺少输入快照，无法重试");
        }

        Map<String, Object> input;
        try {
            input = new HashMap<>(JSON.parseObject(task.getInputSnapshot()));
        } catch (Exception e) {
            throw new BusinessException(ResultCode.CONFLICT, "任务输入快照格式无效，无法重试");
        }

        int retryCount = task.getRetryCount() == null ? 0 : task.getRetryCount();
        if (aiTaskService.resetFailedTask(taskId, retryCount) == 0) {
            throw new BusinessException(ResultCode.CONFLICT, "任务状态已变化，请刷新后重试");
        }
        task.setStatus("pending");
        task.setProgress(0);
        task.setErrorMessage(null);
        task.setResultSnapshot(null);
        task.setStartedTime(null);
        task.setFinishedTime(null);
        recordTaskEvent(taskId, "RETRY", "Task retry requested", null);

        AiTaskCreateRequest request = new AiTaskCreateRequest();
        request.setAbilityCode(task.getAbilityCode());
        request.setBizId(task.getBizId());
        request.setUserId(task.getUserId());
        request.setTenantId(task.getTenantId());
        request.setInput(input);
        if (!submitTask(task, request)) {
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS, task.getErrorMessage());
        }

        return "任务已重新执行，taskId: " + taskId;
    }

    private boolean submitTask(AiTask task, AiTaskCreateRequest request) {
        try {
            taskExecutor.execute(() -> executeAiTaskAsync(task, task.getTaskId(), request));
            return true;
        } catch (RejectedExecutionException e) {
            failTask(task, "AI 任务队列暂不可用，请稍后重试");
            return false;
        }
    }

    private void failTask(AiTask task, String message) {
        task.setStatus("failed");
        task.setErrorMessage(message);
        task.setFinishedTime(new Date());
        aiTaskService.updateByTaskId(task);
        recordTaskEvent(task.getTaskId(), "FAILED", message, null);
    }

    private void recordTaskEvent(String taskId, String type, String message, String payload) {
        AiTaskEvent event = new AiTaskEvent();
        event.setTaskId(taskId);
        event.setEventType(type);
        event.setEventMessage(message);
        event.setEventPayload(payload);
        try {
            aiTaskService.insertEvent(event);
        } catch (RuntimeException e) {
            log.warn("Failed to save AI task event, taskId={}, type={}", taskId, type, e);
        }
    }

    private AiTaskStatusVO convertToVO(AiTask task) {
        AiTaskStatusVO vo = new AiTaskStatusVO();
        vo.setTaskId(task.getTaskId());
        vo.setRequestId(task.getRequestId());
        vo.setAbilityCode(task.getAbilityCode());
        vo.setBizId(task.getBizId());
        vo.setUserId(task.getUserId());
        vo.setStatus(task.getStatus());
        vo.setProgress(task.getProgress());
        vo.setResultSnapshot(task.getResultSnapshot());
        vo.setErrorMessage(task.getErrorMessage());
        vo.setStartedTime(task.getStartedTime());
        vo.setFinishedTime(task.getFinishedTime());
        return vo;
    }

    /**
     * 异步执行AI任务
     */
    public void executeAiTaskAsync(AiTask task, String taskId, AiTaskCreateRequest request) {
        try {
            task.setStatus("running");
            task.setProgress(20);
            task.setStartedTime(new Date());
            aiTaskService.updateByTaskId(task);

            recordTaskEvent(taskId, "RUNNING", "Task started running", null);

            AiAbilityConfig config = aiAbilityRouter.getAbilityConfig(request.getAbilityCode());
            AiProviderClient providerClient = aiAbilityRouter.route(config.getProviderType());

            task.setProgress(50);
            aiTaskService.updateByTaskId(task);

            if (!"1".equals(config.getEnabled())) throw new IllegalStateException("AI 能力未启用");
            String responseText = providerClient.invoke(config, request.getInput(), request.getUserId());

            task.setStatus("succeeded");
            task.setProgress(100);
            task.setResultSnapshot(responseText);
            task.setFinishedTime(new Date());
            aiTaskService.updateByTaskId(task);

            recordTaskEvent(taskId, "SUCCEEDED", "Task completed successfully", responseText);

            archiveBlogAiRecord(request, responseText);
        } catch (Exception e) {
            log.error("Async task execution failed: " + taskId, e);
            task.setResultSnapshot(null);
            failTask(task, e.getMessage() == null ? "AI 任务执行失败" : e.getMessage());
        }
    }

    /**
     * 异步任务成功后，为博客侧创建待采纳 AI 记录（封面生成等）
     */
    private void archiveBlogAiRecord(AiTaskCreateRequest request, String aiResult) {
        if (request == null || request.getBizId() == null || request.getBizId().isEmpty()) {
            return;
        }
        if (aiResult == null || aiResult.isEmpty()) {
            return;
        }
        if (!"article_cover_generate".equalsIgnoreCase(request.getAbilityCode())) {
            return;
        }
        try {
            R<Long> recordResult = remoteBlogAiCallbackService.createRecord(
                    request.getAbilityCode(),
                    request.getBizId(),
                    aiResult,
                    request.getUserId(),
                    SecurityConstants.INNER);
            if (recordResult == null || recordResult.getCode() != R.SUCCESS) {
                log.warn("Failed to archive blog AI record for async task, ability={}, bizId={}",
                        request.getAbilityCode(), request.getBizId());
            }
        } catch (Exception e) {
            log.error("Failed to archive blog AI record for async task", e);
        }
    }

    /**
     * 流式调用 AI 能力 (SSE)
     */
    public org.springframework.web.servlet.mvc.method.annotation.SseEmitter invokeStream(String abilityCode, AiAbilityInvokeRequest request) {
        String requestId = request.getRequestId();
        if (requestId == null || requestId.isEmpty()) {
            requestId = "req_" + UUID.randomUUID().toString().replace("-", "");
        }

        // 实例化 SseEmitter，并设置超时时间（例如 5 分钟）
        org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter =
                new org.springframework.web.servlet.mvc.method.annotation.SseEmitter(300_000L);

        try {
            AiAbilityConfig config = aiAbilityRouter.getAbilityConfig(abilityCode);

            if (!"1".equals(config.getEnabled())) {
                throw new IllegalStateException("AI ability has been disabled: " + abilityCode);
            }

            Map<String, Object> input = request.getInput();
            if (input == null) {
                input = new HashMap<>();
                request.setInput(input);
            }

            // --- Chat 模式编排 ---
            if ("chat".equals(config.getChatMode())) {
                String sessionKey = (String) input.get("session_id");
                // 1. 查找已有的 Dify conversation_id
                String conversationId = null;
                if (sessionKey != null && !sessionKey.isEmpty()) {
                    conversationId = aiConversationService.lookupConversationId(sessionKey, abilityCode);
                }
                // 2. 将内部参数写入 inputs（DifyProviderClient 会提取并清理）
                input.put("__ai_session_key", sessionKey);
                input.put("__ai_conv_id", conversationId);
                input.put("__ai_user_id", request.getUserId());
            }

            com.sourcelin.ai.provider.common.AiStreamResultCallback callback = (fullText) -> {
                if (request.getBizId() == null || request.getBizId().isEmpty() || fullText.isEmpty()) {
                    return null;
                }
                try {
                    if (remoteBlogAiCallbackService != null) {
                        R<Long> r = remoteBlogAiCallbackService.createRecord(
                                abilityCode, request.getBizId(), fullText, request.getUserId(),
                                SecurityConstants.INNER);
                        if (r != null && r.getCode() == R.SUCCESS) {
                            return r.getData();
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed to archive stream record via callback for requestId: " + request.getRequestId(), e);
                }
                return null;
            };

            AiProviderClient providerClient = aiAbilityRouter.route(config.getProviderType());
            providerClient.invokeStream(config, input, emitter, request.getBizId(), requestId, callback);

            // --- Chat 模式：流完成后保存 conversation_id ---
            if ("chat".equals(config.getChatMode())) {
                final String sessionKey = (String) input.get("session_id");
                final Map<String, Object> finalInput = input;
                emitter.onCompletion(() -> {
                    String responseConvId = (String) finalInput.get("__ai_resp_conv_id");
                    if (responseConvId != null && sessionKey != null) {
                        aiConversationService.saveConversationId(
                            sessionKey, abilityCode, responseConvId, request.getUserId());
                    }
                });
            }

        } catch (Exception e) {
            log.error("AI ability stream invoke failed. Code: " + abilityCode, e);
            try {
                com.alibaba.fastjson2.JSONObject errorObj = new com.alibaba.fastjson2.JSONObject();
                errorObj.put("status", "error");
                errorObj.put("content", "");
                errorObj.put("requestId", requestId);
                errorObj.put("error", e.getMessage());
                emitter.send(org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event()
                        .name("message").data(errorObj.toJSONString()));
                emitter.completeWithError(e);
            } catch (Exception ex) {
                log.error("Failed to send error SSE event", ex);
            }
        }

        return emitter;
    }
}
