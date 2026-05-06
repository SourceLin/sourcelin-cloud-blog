package com.sourcelin.common.core.exception;

import com.sourcelin.common.core.enums.ResultCode;
import lombok.Getter;

/**
 * 平台统一业务异常。
 */
@Getter
public class BusinessException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    private final ResultCode resultCode;

    private final Object errorData;

    public BusinessException(ResultCode resultCode)
    {
        this(resultCode, resultCode.getMessage(), null);
    }

    public BusinessException(ResultCode resultCode, String message)
    {
        this(resultCode, message, null);
    }

    public BusinessException(ResultCode resultCode, String message, Object errorData)
    {
        super(message);
        this.resultCode = resultCode;
        this.errorData = errorData;
    }
}
