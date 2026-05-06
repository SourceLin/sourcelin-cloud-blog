package com.sourcelin.blog.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sourcelin.common.core.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 博客公告对象 announcement
 */
public class Announcement extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private String title;

    private String content;

    /**
     * 作用域类型：all/role/segment/assign
     */
    private String scopeType;

    /**
     * 作用域载荷（json字符串）
     */
    private String scopePayload;

    /**
     * 发布状态：0草稿 1已发布 2已下线
     */
    private String publishStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    private Integer deleted;

    /**
     * 查询附加字段：当前用户是否已读（0/1）
     */
    private Integer isRead;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getScopeType()
    {
        return scopeType;
    }

    public void setScopeType(String scopeType)
    {
        this.scopeType = scopeType;
    }

    public String getScopePayload()
    {
        return scopePayload;
    }

    public void setScopePayload(String scopePayload)
    {
        this.scopePayload = scopePayload;
    }

    public String getPublishStatus()
    {
        return publishStatus;
    }

    public void setPublishStatus(String publishStatus)
    {
        this.publishStatus = publishStatus;
    }

    public Date getPublishTime()
    {
        return publishTime;
    }

    public void setPublishTime(Date publishTime)
    {
        this.publishTime = publishTime;
    }

    public Date getExpireTime()
    {
        return expireTime;
    }

    public void setExpireTime(Date expireTime)
    {
        this.expireTime = expireTime;
    }

    public Integer getDeleted()
    {
        return deleted;
    }

    public void setDeleted(Integer deleted)
    {
        this.deleted = deleted;
    }

    public Integer getIsRead()
    {
        return isRead;
    }

    public void setIsRead(Integer isRead)
    {
        this.isRead = isRead;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("title", getTitle())
            .append("scopeType", getScopeType())
            .append("scopePayload", getScopePayload())
            .append("publishStatus", getPublishStatus())
            .append("publishTime", getPublishTime())
            .append("expireTime", getExpireTime())
            .append("deleted", getDeleted())
            .append("isRead", getIsRead())
            .toString();
    }
}
