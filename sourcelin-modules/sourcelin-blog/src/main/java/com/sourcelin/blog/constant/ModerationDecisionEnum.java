package com.sourcelin.blog.constant;

import java.util.Locale;

/**
 * 评论机审决策，与表 {@code b_comment_moderation.decision} 存储值、机审管道输出一致。
 * 勿与 {@code b_comment.status}（人工审核状态）混用。
 */
public enum ModerationDecisionEnum
{
    PASS("PASS"),
    REJECT("REJECT"),
    REVIEW("REVIEW"),
    AI_REVIEW("AI_REVIEW");

    private final String code;

    ModerationDecisionEnum(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    /**
     * 按库中或上游字符串解析，无法识别时返回 null。
     */
    public static ModerationDecisionEnum fromCode(String raw)
    {
        if (raw == null || raw.trim().isEmpty())
        {
            return null;
        }
        String key = raw.trim().toUpperCase(Locale.ROOT);
        for (ModerationDecisionEnum e : values())
        {
            if (e.code.equals(key))
            {
                return e;
            }
        }
        return null;
    }
}
