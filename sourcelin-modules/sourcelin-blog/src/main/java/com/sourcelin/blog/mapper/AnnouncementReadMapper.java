package com.sourcelin.blog.mapper;

import org.apache.ibatis.annotations.Param;

public interface AnnouncementReadMapper
{
    int insertIgnoreRead(@Param("announcementId") Long announcementId, @Param("userId") Long userId);

    int insertReadAllVisible(@Param("userId") Long userId);
}
