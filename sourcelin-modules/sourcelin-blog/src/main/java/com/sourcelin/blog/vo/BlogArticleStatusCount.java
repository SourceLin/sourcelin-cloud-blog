package com.sourcelin.blog.vo;

import lombok.Data;

/**
 * 文章按状态聚合（b_article.status）
 */
@Data
public class BlogArticleStatusCount
{
    private Long status;
    /** 聚合条数（列名避免使用 SQL 保留字 count） */
    private Long cnt;
}
