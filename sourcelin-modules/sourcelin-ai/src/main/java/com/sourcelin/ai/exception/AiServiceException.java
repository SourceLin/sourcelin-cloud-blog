package com.sourcelin.ai.exception;

/**
 * AI服务异常
 */
public class AiServiceException extends com.sourcelin.common.core.exception.BusinessException {

    private final String errorCode;
    private final String abilityCode;

    public AiServiceException(String message) {
        super(com.sourcelin.common.core.enums.ResultCode.REMOTE_SERVICE_ERROR, message);
        this.errorCode = "AI_SERVICE_ERROR";
        this.abilityCode = null;
    }

    public AiServiceException(String message, Throwable cause) {
        super(com.sourcelin.common.core.enums.ResultCode.REMOTE_SERVICE_ERROR, message);
        initCause(cause);
        this.errorCode = "AI_SERVICE_ERROR";
        this.abilityCode = null;
    }

    public AiServiceException(String abilityCode, String message) {
        super(com.sourcelin.common.core.enums.ResultCode.REMOTE_SERVICE_ERROR, message);
        this.errorCode = "AI_SERVICE_ERROR";
        this.abilityCode = abilityCode;
    }

    public AiServiceException(String abilityCode, String message, Throwable cause) {
        super(com.sourcelin.common.core.enums.ResultCode.REMOTE_SERVICE_ERROR, message);
        initCause(cause);
        this.errorCode = "AI_SERVICE_ERROR";
        this.abilityCode = abilityCode;
    }

    public AiServiceException(String errorCode, String abilityCode, String message) {
        super(com.sourcelin.common.core.enums.ResultCode.REMOTE_SERVICE_ERROR, message);
        this.errorCode = errorCode;
        this.abilityCode = abilityCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getAbilityCode() {
        return abilityCode;
    }
}