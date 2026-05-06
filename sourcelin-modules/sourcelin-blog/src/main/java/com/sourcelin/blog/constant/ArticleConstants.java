package com.sourcelin.blog.constant;

public final class ArticleConstants
{
    private ArticleConstants()
    {
    }

    public static final long STATUS_UNDER_REVIEW = 1L;

    public static final long STATUS_PUBLISHED = 2L;

    public static final long STATUS_DRAFT = 3L;

    public static final int DELETED_YES = 1;

    public static final int READ_AUTH_PUBLIC = 1;

    public static final int READ_AUTH_LOGIN = 2;

    public static final int READ_AUTH_AFTER_COMMENT = 3;

    public static final int READ_AUTH_VERIFY = 4;

    public static final int READ_AUTH_VIP = 5;

    public static final int READ_AUTH_PAID = 6;

    public static final int READ_AUTH_AFTER_LIKE = 7;

    public static final int READ_AUTH_AFTER_COLLECT = 8;

    public static final int READ_AUTH_AFTER_FOLLOW = 9;

    public static final String REDIS_ARTICLE_LIKED_PREFIX = "blog:article:liked:";
}
