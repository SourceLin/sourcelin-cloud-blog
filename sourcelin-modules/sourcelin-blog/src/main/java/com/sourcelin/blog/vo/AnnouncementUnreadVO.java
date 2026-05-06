package com.sourcelin.blog.vo;

/**
 * 博客公告未读统计
 */
public class AnnouncementUnreadVO
{
    private Long total;

    public AnnouncementUnreadVO()
    {
    }

    public AnnouncementUnreadVO(Long total)
    {
        this.total = total;
    }

    public Long getTotal()
    {
        return total;
    }

    public void setTotal(Long total)
    {
        this.total = total;
    }
}
