package com.sourcelin.blog.service.interaction;

import com.sourcelin.blog.constant.ArticleConstants;
import com.sourcelin.blog.constant.InteractionTargetTypeEnum;
import com.sourcelin.blog.domain.Article;
import com.sourcelin.blog.domain.Say;
import com.sourcelin.blog.domain.Treehole;
import com.sourcelin.blog.service.IArticleService;
import com.sourcelin.blog.service.ISayService;
import com.sourcelin.blog.service.ITreeholeService;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 互动目标校验与计数更新支撑。
 */
@Component
public class InteractionTargetSupport
{
    @Autowired
    private IArticleService articleService;
    @Autowired
    private ISayService sayService;
    @Autowired
    private ITreeholeService treeholeService;

    public InteractionTargetTypeEnum validateTarget(String targetType, Long targetId)
    {
        InteractionTargetTypeEnum type = InteractionTargetTypeEnum.fromCode(targetType);
        if (type == null)
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "目标类型不合法");
        }
        if (targetId == null || targetId <= 0)
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "目标ID不合法");
        }
        switch (type)
        {
            case ARTICLE:
                Article article = articleService.selectArticleById(targetId);
                if (article == null || (article.getDeleted() != null && article.getDeleted() == ArticleConstants.DELETED_YES))
                {
                    throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
                }
                if (article.getStatus() == null || article.getStatus() != ArticleConstants.STATUS_PUBLISHED)
                {
                    throw new BusinessException(ResultCode.BUSINESS_ERROR, "文章当前不可互动");
                }
                return type;
            case SAY:
                Say say = sayService.selectSayById(targetId);
                if (say == null || (say.getDeleted() != null && say.getDeleted() != 0L))
                {
                    throw new BusinessException(ResultCode.NOT_FOUND, "说说不存在");
                }
                return type;
            case TREEHOLE:
                Treehole treehole = treeholeService.selectTreeholeById(targetId);
                if (treehole == null || (treehole.getDeleted() != null && treehole.getDeleted() != 0L))
                {
                    throw new BusinessException(ResultCode.NOT_FOUND, "树洞不存在");
                }
                return type;
            default:
                throw new BusinessException(ResultCode.VALIDATION_ERROR, "目标类型不合法");
        }
    }

    public void increaseLikeCount(InteractionTargetTypeEnum type, Long targetId)
    {
        int affected;
        switch (type)
        {
            case ARTICLE:
                affected = articleService.updateLikeCount(targetId);
                break;
            case SAY:
                affected = sayService.updateLikeCount(targetId);
                break;
            case TREEHOLE:
                affected = treeholeService.updateLikeCount(targetId);
                break;
            default:
                affected = 0;
        }
        if (affected <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "点赞失败");
        }
    }

    public void decreaseLikeCount(InteractionTargetTypeEnum type, Long targetId)
    {
        int affected;
        switch (type)
        {
            case ARTICLE:
                affected = articleService.updateLikeCountDecrease(targetId);
                break;
            case SAY:
                affected = sayService.updateLikeCountDecrease(targetId);
                break;
            case TREEHOLE:
                affected = treeholeService.updateLikeCountDecrease(targetId);
                break;
            default:
                affected = 0;
        }
        if (affected <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "取消点赞失败");
        }
    }

    public void increaseCollectCount(InteractionTargetTypeEnum type, Long targetId)
    {
        int affected;
        switch (type)
        {
            case ARTICLE:
                affected = articleService.updateCollectCountIncrease(targetId);
                break;
            case SAY:
                affected = sayService.updateCollectCountIncrease(targetId);
                break;
            case TREEHOLE:
                affected = treeholeService.updateCollectCountIncrease(targetId);
                break;
            default:
                affected = 0;
        }
        if (affected <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "收藏失败");
        }
    }

    public void decreaseCollectCount(InteractionTargetTypeEnum type, Long targetId)
    {
        int affected;
        switch (type)
        {
            case ARTICLE:
                affected = articleService.updateCollectCountDecrease(targetId);
                break;
            case SAY:
                affected = sayService.updateCollectCountDecrease(targetId);
                break;
            case TREEHOLE:
                affected = treeholeService.updateCollectCountDecrease(targetId);
                break;
            default:
                affected = 0;
        }
        if (affected <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "取消收藏失败");
        }
    }

    public Long readCollectCount(InteractionTargetTypeEnum type, Long targetId)
    {
        switch (type)
        {
            case ARTICLE:
                Article article = articleService.selectArticleById(targetId);
                return article == null || article.getCollectCount() == null ? 0L : article.getCollectCount();
            case SAY:
                Say say = sayService.selectSayById(targetId);
                return say == null || say.getCollectCount() == null ? 0L : say.getCollectCount();
            case TREEHOLE:
                Treehole treehole = treeholeService.selectTreeholeById(targetId);
                return treehole == null || treehole.getCollectCount() == null ? 0L : treehole.getCollectCount();
            default:
                return 0L;
        }
    }
}

