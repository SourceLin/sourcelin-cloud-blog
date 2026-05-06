package com.sourcelin.blog.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

/**
 * 评论归属类型，与表 {@code b_comment.source} 存储的小写英文一致。
 */
public enum CommentSourceEnum {
    /** 博客文章 */
    ARTICLE("article"),
    /** 说说 */
    SAY("say"),
    /** 树洞 */
    TREEHOLE("treehole");

    private final String code;

    CommentSourceEnum(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    /**
     * 反序列化与兼容库中 null/空串（历史数据视为文章评论）。
     */
    @JsonCreator
    public static CommentSourceEnum fromCode(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return ARTICLE;
        }
        String key = raw.trim().toLowerCase(Locale.ROOT);
        for (CommentSourceEnum s : values()) {
            if (s.code.equals(key)) {
                return s;
            }
        }
        return ARTICLE;
    }

    public boolean isTreehole() {
        return this == TREEHOLE;
    }

    public boolean isSay() {
        return this == SAY;
    }
}
