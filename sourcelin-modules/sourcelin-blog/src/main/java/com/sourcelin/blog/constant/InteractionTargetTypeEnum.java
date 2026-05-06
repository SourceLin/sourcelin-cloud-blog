package com.sourcelin.blog.constant;

import java.util.Locale;

/**
 * 互动目标类型。
 */
public enum InteractionTargetTypeEnum
{
    ARTICLE("article"),
    SAY("say"),
    TREEHOLE("treehole");

    private final String code;

    InteractionTargetTypeEnum(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    public static InteractionTargetTypeEnum fromCode(String raw)
    {
        if (raw == null || raw.trim().isEmpty())
        {
            return null;
        }
        String key = raw.trim().toLowerCase(Locale.ROOT);
        for (InteractionTargetTypeEnum value : values())
        {
            if (value.code.equals(key))
            {
                return value;
            }
        }
        return null;
    }
}

