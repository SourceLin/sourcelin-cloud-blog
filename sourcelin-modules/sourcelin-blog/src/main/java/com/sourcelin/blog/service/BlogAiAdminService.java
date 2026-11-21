package com.sourcelin.blog.service;
import com.sourcelin.ai.api.domain.*;
import com.sourcelin.ai.api.service.RemoteAiService;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.domain.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class BlogAiAdminService {
 private final RemoteAiService remote;
 private <T> T unwrap(ApiResponse<T> response) {
  if(response == null || !Integer.valueOf(0).equals(response.getCode())) throw new BusinessException(ResultCode.REMOTE_SERVICE_ERROR, "AI 服务暂不可用");
  return response.getData();
 }
 public ListResult<AiProviderOptionVO> getProviderTypes() { return unwrap(remote.getProviderTypes(SecurityConstants.INNER)); }
 public PageResult<AiAbilityConfigDTO> getAbilityConfigList(AiAdminPageQuery query) { return unwrap(remote.getAbilityConfigList(query, SecurityConstants.INNER)); }
 public Integer createAbilityConfig(AiAbilityConfigDTO config) { return unwrap(remote.createAbilityConfig(config, SecurityConstants.INNER)); }
 public Integer updateAbilityConfig(AiAbilityConfigDTO config) { return unwrap(remote.updateAbilityConfig(config, SecurityConstants.INNER)); }
 public Integer deleteAbilityConfig(Long id) { return unwrap(remote.deleteAbilityConfig(id, SecurityConstants.INNER)); }
 public PageResult<AiCallLogDTO> getCallLogList(AiAdminPageQuery query) { return unwrap(remote.getCallLogList(query, SecurityConstants.INNER)); }
 public PageResult<AiTaskDTO> getTaskList(AiAdminPageQuery query) { return unwrap(remote.getTaskList(query, SecurityConstants.INNER)); }
 public ListResult<AiTaskEventDTO> getTaskEvents(String taskId) { return unwrap(remote.getTaskEvents(taskId, SecurityConstants.INNER)); }
 public String retryTask(String taskId) { return unwrap(remote.retryTask(taskId, SecurityConstants.INNER)); }
}
