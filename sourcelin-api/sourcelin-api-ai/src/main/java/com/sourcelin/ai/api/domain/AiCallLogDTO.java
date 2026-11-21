package com.sourcelin.ai.api.domain;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class AiCallLogDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String requestId;
    private String appCode;
    private String domainCode;
    private String abilityCode;
    private String bizId;
    private String userId;
    private String tenantId;
    private String providerType;
    private String providerTraceId;
    private String invokeMode;
    private String requestSummary;
    private String responseSummary;
    private String status;
    private String errorCode;
    private String errorMessage;
    private Long latencyMs;
    private Long tokenUsage;
    private BigDecimal costAmount;
    private Date createTime;
}
