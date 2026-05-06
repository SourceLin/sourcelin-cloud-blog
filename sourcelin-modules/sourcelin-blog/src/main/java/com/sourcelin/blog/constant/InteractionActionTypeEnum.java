package com.sourcelin.blog.constant;

/**
 * 互动行为类型。
 */
public enum InteractionActionTypeEnum
{
    LIKE("like"),
    COLLECT("collect");

    private final String code;

    InteractionActionTypeEnum(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }
}

