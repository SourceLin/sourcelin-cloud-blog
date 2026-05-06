package com.sourcelin.blog.service;

import com.sourcelin.blog.domain.Announcement;

import java.util.List;

public interface IAnnouncementService
{
    List<Announcement> selectFrontAnnouncementList(Long userId);

    Announcement selectFrontAnnouncementById(Long id, Long userId);

    long countUnread(Long userId);

    boolean markRead(Long id, Long userId);

    boolean markAllRead(Long userId);

    List<Announcement> selectAnnouncementList(Announcement announcement);

    Announcement selectAnnouncementById(Long id);

    int insertAnnouncement(Announcement announcement);

    int updateAnnouncement(Announcement announcement);

    int publishAnnouncement(Long id, String updateBy);

    int offlineAnnouncement(Long id, String updateBy);

    int deleteAnnouncementByIds(Long[] ids, String updateBy);
}
