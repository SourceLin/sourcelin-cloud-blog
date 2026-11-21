package com.sourcelin.ai.api.domain;

import lombok.Data;
import java.io.Serializable;

@Data
public class AiAbilityInvokeResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestId;
    private String abilityCode;
    private String bizId;
    private String resultType;
    private String content;
    private String adoptStatus;
    private Long recordId;
    private boolean success;
    private String errorCode;
    private String errorMessage;
}
