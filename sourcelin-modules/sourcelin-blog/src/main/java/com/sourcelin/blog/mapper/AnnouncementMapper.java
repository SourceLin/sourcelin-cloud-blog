package com.sourcelin.blog.mapper;

import com.sourcelin.blog.domain.Announcement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AnnouncementMapper
{
    List<Announcement> selectFrontAnnouncementList(@Param("userId") Long userId);

    Announcement selectFrontAnnouncementById(@Param("id") Long id, @Param("userId") Long userId);

    Long countUnread(@Param("userId") Long userId);

    List<Announcement> selectAnnouncementList(Announcement announcement);

    Announcement selectAnnouncementById(@Param("id") Long id);

    int insertAnnouncement(Announcement announcement);

    int updateAnnouncement(Announcement announcement);

    int deleteAnnouncementByIds(@Param("ids") Long[] ids, @Param("updateBy") String updateBy);
}
