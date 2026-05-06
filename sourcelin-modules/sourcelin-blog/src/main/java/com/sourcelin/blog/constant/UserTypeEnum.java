package com.sourcelin.blog.constant;

/**
 * 博客用户类型枚举
 */
public enum UserTypeEnum {
    NORMAL(1, "普通用户"),
    BLOGGER(2, "博主");

    private final int code;
    private final String desc;

    UserTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

