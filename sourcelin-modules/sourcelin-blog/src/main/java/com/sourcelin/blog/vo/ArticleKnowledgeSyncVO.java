package com.sourcelin.blog.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 文章知识库同步结果。
 */
@Data
public class ArticleKnowledgeSyncVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long articleId;
    private String title;
    private String action;
    private String syncStatus;
    private String indexingStatus;
    private String documentId;
    private String batch;
    private String message;
}
