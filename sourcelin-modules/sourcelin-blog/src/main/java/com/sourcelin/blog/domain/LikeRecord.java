package com.sourcelin.blog.domain;

import com.sourcelin.common.core.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 点赞记录对象 b_like_record
 */
public class LikeRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private String targetType;

    private Long targetId;

    /**
     * 软删除标记，0=有效 1=已取消。
     */
    private Integer deleted;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

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

    public Integer getDeleted()
    {
        return deleted;
    }

    public void setDeleted(Integer deleted)
    {
        this.deleted = deleted;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("targetType", getTargetType())
            .append("targetId", getTargetId())
            .append("deleted", getDeleted())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

