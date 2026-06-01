package com.sourcelin.blog.service;

import com.sourcelin.blog.domain.ContentReport;

import java.util.List;

public interface IContentReportService
{
    List<ContentReport> selectContentReportList(ContentReport contentReport);

    ContentReport selectContentReportById(Long id);

    int insertContentReport(ContentReport contentReport);

    int handleContentReport(Long id, String status, String remark);

    int deleteContentReportByIds(Long[] ids);
}
