package com.sourcelin.blog.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 博客公告已读对象 announcement_read
 */
public class AnnouncementRead implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long announcementId;

    private Long userId;

    private Date readTime;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getAnnouncementId()
    {
        return announcementId;
    }

    public void setAnnouncementId(Long announcementId)
    {
        this.announcementId = announcementId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Date getReadTime()
    {
        return readTime;
    }

    public void setReadTime(Date readTime)
    {
        this.readTime = readTime;
    }
}
