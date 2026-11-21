package com.sourcelin.ai.api.domain;

import lombok.Data;
import java.io.Serializable;
import java.util.Map;

@Data
public class AiAbilityInvokeRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestId;
    private String appCode;
    private String domainCode;
    private String abilityCode;
    private String bizId;
    private String userId;
    private String tenantId;
    private Map<String, Object> input;
    private Map<String, Object> metadata;
}
