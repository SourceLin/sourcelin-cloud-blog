package com.sourcelin.ai.api.domain;
import lombok.Data;
@Data
public class AiAdminPageQuery {
 private Integer page = 1;
 private Integer pageSize = 10;
 private String abilityCode;
 private String abilityName;
 private String providerType;
 private String enabled;
 private String status;
 private String requestId;
 private String taskId;
 private String bizId;
 private String invokeMode;
}
