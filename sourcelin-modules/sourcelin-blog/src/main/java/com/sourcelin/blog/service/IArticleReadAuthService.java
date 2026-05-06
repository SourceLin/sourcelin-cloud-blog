package com.sourcelin.blog.service;

import com.sourcelin.blog.domain.Article;
import com.sourcelin.blog.vo.ArticleVO;

/**
 * 文章阅读权限（readAuth）与正文可见性。
 */
public interface IArticleReadAuthService
{
    /**
     * 当前访问者是否可读全文（含正文）。
     *
     * @param article  文章（需含 id、userId、readAuth）
     * @param viewerId 当前用户 id，未登录为 null 或 &lt;=0
     */
    boolean canReadFullContent(Article article, Long viewerId);

    /**
     * 与 {@link #canReadFullContent(Article, Long)} 相同逻辑，用于列表 VO。
     */
    boolean canReadFullContentVo(ArticleVO vo, Long viewerId);

    /**
     * 无权限时清空正文等大字段，不修改入参中的其它业务字段。
     */
    void applyContentVisibility(Article article, Long viewerId);

    void applyContentVisibilityVo(ArticleVO vo, Long viewerId);

    /**
     * 前台提示：为何未解锁全文（简短中文）。
     */
    String buildUnlockHint(Integer readAuth);
}
