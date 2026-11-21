package com.sourcelin.ai.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sourcelin.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiAbilityConfigDTO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String abilityCode;
    private String abilityName;
    private String appCode;
    private String domainCode;
    private String providerType;
    private String providerAppId;
    private String providerWorkflowId;
    private String endpointUrl;
    private String authConfigRef;
    private String apiKey;
    private String promptTemplate;
    private String invokeMode;
    private String enabled;
    private Integer version;
    private String chatMode;

}
