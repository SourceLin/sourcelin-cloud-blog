package com.sourcelin.blog.vo.interaction;

/**
 * 互动目标统计视图。
 */
public class InteractionTargetStatVO
{
    private String targetType;

    private Long targetId;

    private Long likeCount;

    private Long collectCount;

    private Boolean likedByMe;

    private Boolean collectedByMe;

    public String getTargetType()
    {
        return targetType;
    }

    public void setTargetType(String targetType)
    {
        this.targetType = targetType;
    }

    public Long getTargetId()
    {
        return targetId;
    }

    public void setTargetId(Long targetId)
    {
        this.targetId = targetId;
    }

    public Long getLikeCount()
    {
        return likeCount;
    }

    public void setLikeCount(Long likeCount)
    {
        this.likeCount = likeCount;
    }

    public Long getCollectCount()
    {
        return collectCount;
    }

    public void setCollectCount(Long collectCount)
    {
        this.collectCount = collectCount;
    }

    public Boolean getLikedByMe()
    {
        return likedByMe;
    }

    public void setLikedByMe(Boolean likedByMe)
    {
        this.likedByMe = likedByMe;
    }

    public Boolean getCollectedByMe()
    {
        return collectedByMe;
    }

    public void setCollectedByMe(Boolean collectedByMe)
    {
        this.collectedByMe = collectedByMe;
    }
}

