package com.sourcelin.ai.api.factory;
import com.sourcelin.ai.api.service.RemoteAiService;
import com.sourcelin.ai.api.domain.*;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.web.domain.response.*;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
@Component
public class RemoteAiFallbackFactory implements FallbackFactory<RemoteAiService> {
 public RemoteAiService create(Throwable cause) { return new RemoteAiService() {
  @Override public ApiResponse<AiAbilityInvokeResult> invoke(String abilityCode,
        AiAbilityInvokeRequest request,
        String source) { return ApiResponse.fail(ResultCode.REMOTE_SERVICE_ERROR, "AI 服务暂不可用"); }
  @Override public ApiResponse<AiTaskStatusVO> createTask(AiTaskCreateRequest request,
        String source) { return ApiResponse.fail(ResultCode.REMOTE_SERVICE_ERROR, "AI 服务暂不可用"); }
  @Override public ApiResponse<AiTaskStatusVO> getTaskStatus(String taskId,
        String source) { return ApiResponse.fail(ResultCode.REMOTE_SERVICE_ERROR, "AI 服务暂不可用"); }
  @Override public ApiResponse<DifyKnowledgeDocumentSyncResult> syncDifyKnowledgeDocument(DifyKnowledgeDocumentSyncRequest request,
        String source) { return ApiResponse.fail(ResultCode.REMOTE_SERVICE_ERROR, "AI 服务暂不可用"); }
  @Override public ApiResponse<ListResult<AiProviderOptionVO>> getProviderTypes(String source) { return ApiResponse.fail(ResultCode.REMOTE_SERVICE_ERROR, "AI 服务暂不可用"); }
  @Override public ApiResponse<PageResult<AiAbilityConfigDTO>> getAbilityConfigList(AiAdminPageQuery query,
        String source) { return ApiResponse.fail(ResultCode.REMOTE_SERVICE_ERROR, "AI 服务暂不可用"); }
  @Override public ApiResponse<Integer> createAbilityConfig(AiAbilityConfigDTO config,
        String source) { return ApiResponse.fail(ResultCode.REMOTE_SERVICE_ERROR, "AI 服务暂不可用"); }
  @Override public ApiResponse<Integer> updateAbilityConfig(AiAbilityConfigDTO config,
        String source) { return ApiResponse.fail(ResultCode.REMOTE_SERVICE_ERROR, "AI 服务暂不可用"); }
  @Override public ApiResponse<Integer> deleteAbilityConfig(Long id,
        String source) { return ApiResponse.fail(ResultCode.REMOTE_SERVICE_ERROR, "AI 服务暂不可用"); }
  @Override public ApiResponse<PageResult<AiCallLogDTO>> getCallLogList(AiAdminPageQuery query,
        String source) { return ApiResponse.fail(ResultCode.REMOTE_SERVICE_ERROR, "AI 服务暂不可用"); }
  @Override public ApiResponse<PageResult<AiTaskDTO>> getTaskList(AiAdminPageQuery query,
        String source) { return ApiResponse.fail(ResultCode.REMOTE_SERVICE_ERROR, "AI 服务暂不可用"); }
  @Override public ApiResponse<ListResult<AiTaskEventDTO>> getTaskEvents(String taskId,
        String source) { return ApiResponse.fail(ResultCode.REMOTE_SERVICE_ERROR, "AI 服务暂不可用"); }
  @Override public ApiResponse<String> retryTask(String taskId,
        String source) { return ApiResponse.fail(ResultCode.REMOTE_SERVICE_ERROR, "AI 服务暂不可用"); }
 }; }
}
