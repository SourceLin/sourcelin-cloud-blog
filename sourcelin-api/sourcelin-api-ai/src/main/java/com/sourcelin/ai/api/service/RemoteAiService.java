package com.sourcelin.ai.api.service;

import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.constant.ServiceNameConstants;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import com.sourcelin.ai.api.domain.AiAbilityInvokeRequest;
import com.sourcelin.ai.api.domain.AiAbilityInvokeResult;
import com.sourcelin.ai.api.domain.AiTaskCreateRequest;
import com.sourcelin.ai.api.domain.AiTaskStatusVO;
import com.sourcelin.ai.api.domain.DifyKnowledgeDocumentSyncRequest;
import com.sourcelin.ai.api.domain.DifyKnowledgeDocumentSyncResult;
import com.sourcelin.ai.api.factory.RemoteAiFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.cloud.openfeign.SpringQueryMap;
import com.sourcelin.ai.api.domain.*;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.core.web.domain.response.ListResult;
import java.util.Map;

@FeignClient(
    contextId = "remoteAiService",
    value = ServiceNameConstants.AI_SERVICE,
    fallbackFactory = RemoteAiFallbackFactory.class
)
public interface RemoteAiService {

    /**
     * 同步调用 AI 能力
     */
    @PostMapping("/abilities/{abilityCode}/invoke")
    ApiResponse<AiAbilityInvokeResult> invoke(
        @PathVariable("abilityCode") String abilityCode,
        @RequestBody AiAbilityInvokeRequest request,
        @RequestHeader(SecurityConstants.FROM_SOURCE) String source
    );

    /**
     * 创建 AI 异步任务
     */
    @PostMapping("/tasks")
    ApiResponse<AiTaskStatusVO> createTask(
        @RequestBody AiTaskCreateRequest request,
        @RequestHeader(SecurityConstants.FROM_SOURCE) String source
    );

    /**
     * 查询 AI 异步任务状态
     */
    @GetMapping("/tasks/{taskId}")
    ApiResponse<AiTaskStatusVO> getTaskStatus(
        @PathVariable("taskId") String taskId,
        @RequestHeader(SecurityConstants.FROM_SOURCE) String source
    );

    /**
     * 同步 Dify 知识库文档。
     */
    @PostMapping("/knowledge/dify/documents/sync")
    ApiResponse<DifyKnowledgeDocumentSyncResult> syncDifyKnowledgeDocument(
        @RequestBody DifyKnowledgeDocumentSyncRequest request,
        @RequestHeader(SecurityConstants.FROM_SOURCE) String source
    );

    // ---- 管理后台配置管理接口 ----

    @GetMapping("/ability-configs/provider-types")
    ApiResponse<ListResult<AiProviderOptionVO>> getProviderTypes(
        @RequestHeader(SecurityConstants.FROM_SOURCE) String source
    );

    @GetMapping("/ability-configs")
    ApiResponse<PageResult<AiAbilityConfigDTO>> getAbilityConfigList(
        @SpringQueryMap AiAdminPageQuery query,
        @RequestHeader(SecurityConstants.FROM_SOURCE) String source
    );

    @PostMapping("/ability-configs")
    ApiResponse<Integer> createAbilityConfig(
        @RequestBody AiAbilityConfigDTO config,
        @RequestHeader(SecurityConstants.FROM_SOURCE) String source
    );

    @PutMapping("/ability-configs")
    ApiResponse<Integer> updateAbilityConfig(
        @RequestBody AiAbilityConfigDTO config,
        @RequestHeader(SecurityConstants.FROM_SOURCE) String source
    );

    @DeleteMapping("/ability-configs/{id}")
    ApiResponse<Integer> deleteAbilityConfig(
        @PathVariable("id") Long id,
        @RequestHeader(SecurityConstants.FROM_SOURCE) String source
    );

    // ---- 管理后台调用日志 ----

    @GetMapping("/call-logs")
    ApiResponse<PageResult<AiCallLogDTO>> getCallLogList(
        @SpringQueryMap AiAdminPageQuery query,
        @RequestHeader(SecurityConstants.FROM_SOURCE) String source
    );

    // ---- 管理后台异步任务 ----

    @GetMapping("/admin/tasks")
    ApiResponse<PageResult<AiTaskDTO>> getTaskList(
        @SpringQueryMap AiAdminPageQuery query,
        @RequestHeader(SecurityConstants.FROM_SOURCE) String source
    );

    @GetMapping("/admin/tasks/{taskId}/events")
    ApiResponse<ListResult<AiTaskEventDTO>> getTaskEvents(
        @PathVariable("taskId") String taskId,
        @RequestHeader(SecurityConstants.FROM_SOURCE) String source
    );

    @PostMapping("/admin/tasks/{taskId}/retry")
    ApiResponse<String> retryTask(
        @PathVariable("taskId") String taskId,
        @RequestHeader(SecurityConstants.FROM_SOURCE) String source
    );
}
