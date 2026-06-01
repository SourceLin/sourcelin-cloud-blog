package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.domain.ContentReport;
import com.sourcelin.blog.mapper.ContentReportMapper;
import com.sourcelin.blog.service.IContentReportService;
import com.sourcelin.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentReportServiceImpl implements IContentReportService
{
    @Autowired
    private ContentReportMapper contentReportMapper;

    @Override
    public List<ContentReport> selectContentReportList(ContentReport contentReport)
    {
        return contentReportMapper.selectContentReportList(contentReport);
    }

    @Override
    public ContentReport selectContentReportById(Long id)
    {
        return contentReportMapper.selectContentReportById(id);
    }

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

    @Override
    public int handleContentReport(Long id, String status, String remark)
    {
        ContentReport report = new ContentReport();
        report.setId(id);
        report.setStatus(status);
        report.setRemark(remark);
        report.setUpdateTime(DateUtils.getNowDate());
        return contentReportMapper.updateContentReport(report);
    }

    @Override
    public int deleteContentReportByIds(Long[] ids)
    {
        return contentReportMapper.deleteContentReportByIds(ids);
    }
}
