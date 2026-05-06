package com.sourcelin.system.api.domain;

import com.sourcelin.common.core.web.domain.BaseEntity;

/**
 * 通知公告（Feign 传输）
 */
public class SysNotice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long noticeId;

    private String noticeTitle;

    /** 公告类型（1通知 2公告） */
    private String noticeType;

    private String noticeContent;

    /** 公告状态（0正常 1关闭） */
    private String status;

    public Long getNoticeId()
    {
        return noticeId;
    }

    public void setNoticeId(Long noticeId)
    {
        this.noticeId = noticeId;
    }

    public String getNoticeTitle()
    {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle)
    {
        this.noticeTitle = noticeTitle;
    }

    public String getNoticeType()
    {
        return noticeType;
    }

    public void setNoticeType(String noticeType)
    {
        this.noticeType = noticeType;
    }

    public String getNoticeContent()
    {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent)
    {
        this.noticeContent = noticeContent;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
