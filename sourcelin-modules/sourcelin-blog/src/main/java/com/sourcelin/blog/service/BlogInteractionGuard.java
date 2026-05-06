package com.sourcelin.blog.service;

import com.sourcelin.blog.constant.ArticleConstants;
import com.sourcelin.blog.constant.InteractionActionTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 前台点赞/浏览/评论互动：Redis 去重与简单频控（配合网关全局限流）。
 */
@Service
public class BlogInteractionGuard
{
    private static final String RATE_LIKE_USER = "blog:rl:like:user:";
    private static final String RATE_VIEW_IP = "blog:rl:view:ip:";
    private static final String RATE_COMMENT_USER = "blog:rl:comment:user:";
    private static final String RATE_COMMENT_IP = "blog:rl:comment:ip:";
    private static final String RATE_CLIKE_USER = "blog:rl:clike:user:";
    private static final String RATE_CLIKE_IP = "blog:rl:clike:ip:";
    private static final String RATE_INTERACTION_USER = "blog:rl:interaction:user:";
    private static final String RATE_INTERACTION_ACTION_USER = "blog:rl:interaction:action:user:";
    private static final String DEDUP_CLIKE = "blog:dedup:clike:";
    private static final String DEDUP_CLIKE_IP = "blog:dedup:clike:ip:";

    /** 每分钟每用户最多点赞文章次数 */
    private static final int MAX_ARTICLE_LIKE_PER_MIN = 30;
    /** 每分钟每 IP 浏览上报次数 */
    private static final int MAX_VIEW_PER_MIN = 120;
    /** 每分钟每用户发评论次数 */
    private static final int MAX_COMMENT_PER_MIN = 20;
    /** 每分钟每用户评论点赞次数 */
    private static final int MAX_COMMENT_LIKE_PER_MIN = 60;
    /** 每分钟每用户点赞/收藏总互动次数 */
    private static final int MAX_INTERACTION_PER_MIN = 120;
    /** 每分钟每用户每种行为互动次数 */
    private static final int MAX_INTERACTION_ACTION_PER_MIN = 80;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public enum Decision
    {
        OK,
        DUPLICATE,
        RATE_LIMIT
    }

    /**
     * 文章点赞：需已登录；成功时写入与「点赞阅读」一致的 Redis 标记（30 天）。
     */
    public Decision tryArticleLike(Long userId, Long articleId)
    {
        if (userId == null || userId <= 0)
        {
            return Decision.RATE_LIMIT;
        }
        if (!incrementRate(RATE_LIKE_USER + userId, MAX_ARTICLE_LIKE_PER_MIN, 60))
        {
            return Decision.RATE_LIMIT;
        }
        String key = ArticleConstants.REDIS_ARTICLE_LIKED_PREFIX + userId + ":" + articleId;
        Boolean first = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofDays(30));
        if (Boolean.FALSE.equals(first))
        {
            return Decision.DUPLICATE;
        }
        return Decision.OK;
    }

    /** DB 更新失败时回滚点赞标记，允许用户重试 */
    public void rollbackArticleLike(Long userId, Long articleId)
    {
        if (userId == null || articleId == null)
        {
            return;
        }
        stringRedisTemplate.delete(ArticleConstants.REDIS_ARTICLE_LIKED_PREFIX + userId + ":" + articleId);
    }

    public Decision tryViewReport(String ip)
    {
        String safeIp = (ip == null || ip.isEmpty()) ? "unknown" : ip;
        if (!incrementRate(RATE_VIEW_IP + safeIp, MAX_VIEW_PER_MIN, 60))
        {
            return Decision.RATE_LIMIT;
        }
        return Decision.OK;
    }

    public Decision tryCommentAdd(Long userId)
    {
        if (userId == null || userId <= 0)
        {
            return Decision.RATE_LIMIT;
        }
        if (!incrementRate(RATE_COMMENT_USER + userId, MAX_COMMENT_PER_MIN, 60))
        {
            return Decision.RATE_LIMIT;
        }
        return Decision.OK;
    }

    /** 匿名树洞评论：按 IP 限频 */
    public Decision tryAnonymousCommentAdd(String ip)
    {
        if (!incrementRate(RATE_COMMENT_IP + normalizeIp(ip), MAX_COMMENT_PER_MIN, 60))
        {
            return Decision.RATE_LIMIT;
        }
        return Decision.OK;
    }

    public Decision tryCommentLike(Long userId, Long commentId)
    {
        if (userId == null || userId <= 0)
        {
            return Decision.RATE_LIMIT;
        }
        if (!incrementRate(RATE_CLIKE_USER + userId, MAX_COMMENT_LIKE_PER_MIN, 60))
        {
            return Decision.RATE_LIMIT;
        }
        String key = DEDUP_CLIKE + userId + ":" + commentId;
        Boolean first = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofDays(7));
        if (Boolean.FALSE.equals(first))
        {
            return Decision.DUPLICATE;
        }
        return Decision.OK;
    }

    public Decision tryInteractionAction(Long userId, String actionType)
    {
        if (userId == null || userId <= 0)
        {
            return Decision.RATE_LIMIT;
        }
        String normalizedAction = normalizeInteractionAction(actionType);
        if (!incrementRate(RATE_INTERACTION_USER + userId, MAX_INTERACTION_PER_MIN, 60))
        {
            return Decision.RATE_LIMIT;
        }
        if (!incrementRate(RATE_INTERACTION_ACTION_USER + normalizedAction + ":" + userId, MAX_INTERACTION_ACTION_PER_MIN, 60))
        {
            return Decision.RATE_LIMIT;
        }
        return Decision.OK;
    }

    /** 匿名树洞评论点赞：IP 限频 + IP+评论去重 */
    public Decision tryAnonymousCommentLike(String ip, Long commentId)
    {
        String normalizedIp = normalizeIp(ip);
        if (!incrementRate(RATE_CLIKE_IP + normalizedIp, MAX_COMMENT_LIKE_PER_MIN, 60))
        {
            return Decision.RATE_LIMIT;
        }
        String key = DEDUP_CLIKE_IP + normalizedIp + ":" + commentId;
        Boolean first = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofDays(7));
        if (Boolean.FALSE.equals(first))
        {
            return Decision.DUPLICATE;
        }
        return Decision.OK;
    }

    public void rollbackCommentLike(Long userId, Long commentId)
    {
        if (userId == null || commentId == null)
        {
            return;
        }
        stringRedisTemplate.delete(DEDUP_CLIKE + userId + ":" + commentId);
    }

    public void rollbackAnonymousCommentLike(String ip, Long commentId)
    {
        if (commentId == null)
        {
            return;
        }
        stringRedisTemplate.delete(DEDUP_CLIKE_IP + normalizeIp(ip) + ":" + commentId);
    }

    private boolean incrementRate(String key, int max, int windowSeconds)
    {
        Long n = stringRedisTemplate.opsForValue().increment(key);
        if (n != null && n == 1L)
        {
            stringRedisTemplate.expire(key, windowSeconds, java.util.concurrent.TimeUnit.SECONDS);
        }
        return n != null && n <= max;
    }

    private String normalizeIp(String ip)
    {
        return (ip == null || ip.trim().isEmpty()) ? "unknown" : ip.trim();
    }

    private String normalizeInteractionAction(String actionType)
    {
        if (InteractionActionTypeEnum.COLLECT.getCode().equals(actionType))
        {
            return InteractionActionTypeEnum.COLLECT.getCode();
        }
        return InteractionActionTypeEnum.LIKE.getCode();
    }
}
