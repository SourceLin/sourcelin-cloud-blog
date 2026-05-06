package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.constant.ArticleConstants;
import com.sourcelin.blog.domain.Article;
import com.sourcelin.blog.domain.Collect;
import com.sourcelin.blog.mapper.CommentMapper;
import com.sourcelin.blog.service.ICollectService;
import com.sourcelin.blog.service.IFollowService;
import com.sourcelin.blog.service.IArticleReadAuthService;
import com.sourcelin.blog.vo.ArticleVO;
import com.sourcelin.common.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * readAuth 与登录态决定正文是否返回；作者本人始终可读全文。
 */
@Service
public class ArticleReadAuthServiceImpl implements IArticleReadAuthService
{
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ICollectService collectService;
    @Autowired
    private IFollowService followService;
    @Autowired
    private RedisService redisService;

    private static boolean isOwner(Article article, Long viewerId)
    {
        return viewerId != null && viewerId > 0 && article.getUserId() != null
            && viewerId.equals(article.getUserId());
    }

    private static boolean isOwnerVo(ArticleVO vo, Long viewerId)
    {
        return viewerId != null && viewerId > 0 && vo.getUserId() != null
            && viewerId.equals(vo.getUserId());
    }

    private static String likedKey(Long userId, Long articleId)
    {
        return ArticleConstants.REDIS_ARTICLE_LIKED_PREFIX + userId + ":" + articleId;
    }

    @Override
    public boolean canReadFullContent(Article article, Long viewerId)
    {
        if (article == null)
        {
            return false;
        }
        if (isOwner(article, viewerId))
        {
            return true;
        }
        Integer ra = article.getReadAuth();
        if (ra == null || ra == ArticleConstants.READ_AUTH_PUBLIC)
        {
            return true;
        }
        if (viewerId == null || viewerId <= 0)
        {
            return false;
        }
        switch (ra)
        {
            case ArticleConstants.READ_AUTH_LOGIN:
                return true;
            case ArticleConstants.READ_AUTH_AFTER_COMMENT:
                return commentMapper.countByArticleIdAndUserId(article.getId(), viewerId) > 0;
            case ArticleConstants.READ_AUTH_VERIFY:
            case ArticleConstants.READ_AUTH_VIP:
            case ArticleConstants.READ_AUTH_PAID:
                // 验证/会员/付费：业务未接时与「登录可读」一致，避免误拦已登录用户
                return true;
            case ArticleConstants.READ_AUTH_AFTER_LIKE:
                return Boolean.TRUE.equals(redisService.hasKey(likedKey(viewerId, article.getId())));
            case ArticleConstants.READ_AUTH_AFTER_COLLECT:
                return collectService.selectCollectByUserAndTarget(
                    viewerId, Collect.TARGET_TYPE_ARTICLE, article.getId()) != null;
            case ArticleConstants.READ_AUTH_AFTER_FOLLOW:
                if (article.getUserId() == null)
                {
                    return false;
                }
                return followService.selectFollowByUserAndTarget(viewerId, article.getUserId()) != null;
            default:
                return true;
        }
    }

    @Override
    public boolean canReadFullContentVo(ArticleVO vo, Long viewerId)
    {
        if (vo == null)
        {
            return false;
        }
        if (isOwnerVo(vo, viewerId))
        {
            return true;
        }
        Article stub = new Article();
        stub.setId(vo.getId());
        stub.setUserId(vo.getUserId());
        stub.setReadAuth(vo.getReadAuth());
        return canReadFullContent(stub, viewerId);
    }

    @Override
    public void applyContentVisibility(Article article, Long viewerId)
    {
        if (article == null || canReadFullContent(article, viewerId))
        {
            return;
        }
        article.setContent(null);
    }

    @Override
    public void applyContentVisibilityVo(ArticleVO vo, Long viewerId)
    {
        if (vo == null || canReadFullContentVo(vo, viewerId))
        {
            return;
        }
        vo.setContent(null);
    }

    @Override
    public String buildUnlockHint(Integer readAuth)
    {
        if (readAuth == null)
        {
            return "请登录后查看全文";
        }
        switch (readAuth)
        {
            case ArticleConstants.READ_AUTH_LOGIN:
                return "登录后可阅读全文";
            case ArticleConstants.READ_AUTH_AFTER_COMMENT:
                return "在本文章下发表评论后可阅读全文";
            case ArticleConstants.READ_AUTH_VERIFY:
                return "完成验证后可阅读全文";
            case ArticleConstants.READ_AUTH_VIP:
                return "会员可见全文";
            case ArticleConstants.READ_AUTH_PAID:
                return "购买后可阅读全文";
            case ArticleConstants.READ_AUTH_AFTER_LIKE:
                return "点赞后可阅读全文";
            case ArticleConstants.READ_AUTH_AFTER_COLLECT:
                return "收藏后可阅读全文";
            case ArticleConstants.READ_AUTH_AFTER_FOLLOW:
                return "关注作者后可阅读全文";
            default:
                return "登录后可阅读全文";
        }
    }
}
