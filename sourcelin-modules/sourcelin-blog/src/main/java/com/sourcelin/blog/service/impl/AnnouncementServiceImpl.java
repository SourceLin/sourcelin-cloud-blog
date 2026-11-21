package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.domain.Announcement;
import com.sourcelin.blog.mapper.AnnouncementMapper;
import com.sourcelin.blog.mapper.AnnouncementReadMapper;
import com.sourcelin.blog.service.IAnnouncementService;
import com.sourcelin.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Service
public class AnnouncementServiceImpl implements IAnnouncementService
{
    @Autowired
    private AnnouncementMapper announcementMapper;

    @Autowired
    private AnnouncementReadMapper announcementReadMapper;

    @Autowired
    private com.sourcelin.blog.shared.support.RealtimeMessageSupport realtimeMessageSupport;

    @Override
    public List<Announcement> selectFrontAnnouncementList(Long userId)
    {
        return announcementMapper.selectFrontAnnouncementList(userId);
    }

    @Override
    public Announcement selectFrontAnnouncementById(Long id, Long userId)
    {
        return announcementMapper.selectFrontAnnouncementById(id, userId);
    }

    @Override
    public long countUnread(Long userId)
    {
        if (userId == null)
        {
            return 0L;
        }
        Long count = announcementMapper.countUnread(userId);
        return count == null ? 0L : count;
    }

    @Override
    public boolean markRead(Long id, Long userId)
    {
        if (id == null || userId == null)
        {
            return false;
        }
        boolean result = announcementReadMapper.insertIgnoreRead(id, userId) >= 0;
        realtimeMessageSupport.invalidate(userId); return result;
    }

    @Override
    public boolean markAllRead(Long userId)
    {
        if (userId == null)
        {
            return false;
        }
        announcementReadMapper.insertReadAllVisible(userId);
        realtimeMessageSupport.invalidate(userId);
        return true;
    }

    @Override
    public List<Announcement> selectAnnouncementList(Announcement announcement)
    {
        return announcementMapper.selectAnnouncementList(announcement);
    }

    @Override
    public Announcement selectAnnouncementById(Long id)
    {
        return announcementMapper.selectAnnouncementById(id);
    }

    @Override
    public int insertAnnouncement(Announcement announcement)
    {
        announcement.setCreateTime(DateUtils.getNowDate());
        if ("1".equals(announcement.getPublishStatus()) && announcement.getPublishTime() == null)
        {
            // DATETIME 保存到秒，避免毫秒被进位到下一秒，导致 SSE 刷新时公告还不可见。
            announcement.setPublishTime(DateUtils.truncate(announcement.getCreateTime(), Calendar.SECOND));
        }
        int rows = announcementMapper.insertAnnouncement(announcement);
        if (rows > 0 && "1".equals(announcement.getPublishStatus()))
        {
            realtimeMessageSupport.invalidate(null);
        }
        return rows;
    }

    @Override
    public int updateAnnouncement(Announcement announcement)
    {
        announcement.setUpdateTime(DateUtils.getNowDate());
        int rows = announcementMapper.updateAnnouncement(announcement);
        if(rows > 0) realtimeMessageSupport.invalidate(null); return rows;
    }

    @Override
    public int publishAnnouncement(Long id, String updateBy)
    {
        Announcement update = new Announcement();
        update.setId(id);
        update.setPublishStatus("1");
        update.setUpdateBy(updateBy);
        update.setPublishTime(DateUtils.truncate(DateUtils.getNowDate(), Calendar.SECOND));
        update.setUpdateTime(DateUtils.getNowDate());
        int rows = announcementMapper.updateAnnouncement(update);
        if(rows > 0) realtimeMessageSupport.invalidate(null); return rows;
    }

    @Override
    public int offlineAnnouncement(Long id, String updateBy)
    {
        Announcement update = new Announcement();
        update.setId(id);
        update.setPublishStatus("2");
        update.setUpdateBy(updateBy);
        update.setUpdateTime(DateUtils.getNowDate());
        int rows = announcementMapper.updateAnnouncement(update);
        if(rows > 0) realtimeMessageSupport.invalidate(null); return rows;
    }

    @Override
    public int deleteAnnouncementByIds(Long[] ids, String updateBy)
    {
        int rows = announcementMapper.deleteAnnouncementByIds(ids, updateBy);
        if(rows > 0) realtimeMessageSupport.invalidate(null); return rows;
    }
}
