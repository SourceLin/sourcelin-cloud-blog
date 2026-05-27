package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.domain.ContentReport;
import com.sourcelin.blog.mapper.ContentReportMapper;
import com.sourcelin.blog.service.IContentReportService;
import com.sourcelin.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentReportServiceImpl implements IContentReportService
{
    @Autowired
    private ContentReportMapper contentReportMapper;

    @Override
    public int insertContentReport(ContentReport contentReport)
    {
        contentReport.setCreateTime(DateUtils.getNowDate());
        contentReport.setUpdateTime(DateUtils.getNowDate());
        if (contentReport.getStatus() == null || contentReport.getStatus().isEmpty())
        {
            contentReport.setStatus("PENDING");
        }
        return contentReportMapper.insertContentReport(contentReport);
    }
}
