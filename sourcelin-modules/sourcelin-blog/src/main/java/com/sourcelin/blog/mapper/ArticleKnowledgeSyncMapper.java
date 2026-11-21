package com.sourcelin.blog.mapper;

import com.sourcelin.blog.domain.ArticleKnowledgeSync;
import org.apache.ibatis.annotations.Param;

/**
 * 文章知识库同步记录 Mapper。
 */
public interface ArticleKnowledgeSyncMapper {

    ArticleKnowledgeSync selectByArticleId(@Param("articleId") Long articleId);

    int insert(ArticleKnowledgeSync sync);

    int updateByArticleId(ArticleKnowledgeSync sync);
}
