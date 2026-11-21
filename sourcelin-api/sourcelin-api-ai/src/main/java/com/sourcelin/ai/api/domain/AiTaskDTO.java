package com.sourcelin.ai.api.domain;

import com.sourcelin.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class AiTaskDTO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String taskId;
    private String requestId;
    private String appCode;
    private String domainCode;
    private String abilityCode;
    private String bizId;
    private String userId;
    private String tenantId;
    private String status;
    private Integer progress;
    private String inputSnapshot;
    private String resultSnapshot;
    private String errorMessage;
    private Integer retryCount;
    private String callbackUrl;
    private Date startedTime;
    private Date finishedTime;
}
