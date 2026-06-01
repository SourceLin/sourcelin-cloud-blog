package com.sourcelin.blog.mapper;

import com.sourcelin.blog.domain.ContentReport;

import java.util.List;

public interface ContentReportMapper
{
    List<ContentReport> selectContentReportList(ContentReport contentReport);

    ContentReport selectContentReportById(Long id);

    int insertContentReport(ContentReport contentReport);

    int updateContentReport(ContentReport contentReport);

    int deleteContentReportByIds(Long[] ids);
}
