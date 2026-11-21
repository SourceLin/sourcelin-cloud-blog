package com.sourcelin.blog.service.event;

/**
 * 文章知识库同步事件。
 */
public class ArticleKnowledgeSyncEvent {
    private final Long articleId;

    public ArticleKnowledgeSyncEvent(Long articleId) {
        this.articleId = articleId;
    }

    public Long getArticleId() {
        return articleId;
    }
}
