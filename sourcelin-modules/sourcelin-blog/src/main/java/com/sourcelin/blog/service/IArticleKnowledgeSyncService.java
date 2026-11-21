package com.sourcelin.blog.service;

import com.sourcelin.blog.vo.ArticleKnowledgeBatchSyncVO;
import com.sourcelin.blog.vo.ArticleKnowledgeSyncVO;

/**
 * 文章知识库同步服务。
 */
public interface IArticleKnowledgeSyncService {

    /**
     * 同步单篇文章。
     *
     * @param articleId 文章 ID
     * @return 同步结果
     */
    ArticleKnowledgeSyncVO syncArticle(Long articleId);

    /**
     * 同步所有公开已发布文章。
     *
     * @return 批量同步结果
     */
    ArticleKnowledgeBatchSyncVO syncPublishedArticles();
}
