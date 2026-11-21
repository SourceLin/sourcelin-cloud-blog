package com.sourcelin.blog.service;

import com.sourcelin.ai.api.domain.AiAbilityInvokeResult;
import com.sourcelin.ai.api.domain.AiTaskCreateRequest;
import com.sourcelin.ai.api.domain.AiTaskStatusVO;
import com.sourcelin.blog.domain.BlogAiRecord;
import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface IBlogAiService {
    /**
     * 同步调用 AI 能力
     */
    AiAbilityInvokeResult invokeAbility(String abilityCode, String bizId, Map<String, Object> input, String userId);

    /**
     * 流式调用 AI 能力 (SSE)
     */
    SseEmitter invokeStream(String abilityCode, String bizId, Map<String, Object> input, String userId);

    /**
     * 创建 AI 异步任务
     */
    AiTaskStatusVO createAsyncTask(AiTaskCreateRequest request, String userId);

    /**
     * 查询 AI 异步任务状态
     */
    AiTaskStatusVO getAsyncTaskStatus(String taskId, String userId);

    /**
     * 采纳 AI 结果并回写到正式表
     */
    boolean adoptResult(Long recordId, String userId);

    /**
     * 放弃 AI 结果
     */
    boolean discardResult(Long recordId, String userId);

    /**
     * 查询记录列表
     */
    List<BlogAiRecord> selectRecordList(BlogAiRecord query);
}
