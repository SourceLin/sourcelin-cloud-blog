package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.domain.ContentReport;
import com.sourcelin.blog.dto.FrontContentReportDTO;
import com.sourcelin.blog.service.IContentReportService;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.ServletUtils;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.utils.ip.IpUtils;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/front/reports")
public class FrontReportController extends BaseController
{
    @Autowired
    private IContentReportService contentReportService;

    @Autowired
    private BlogLoginAccessor blogLoginAccessor;

    @PostMapping
    public Void create(@Valid @RequestBody FrontContentReportDTO body)
    {
        ContentReport report = new ContentReport();
        report.setUserId(blogLoginAccessor.getCurrentUserId());
        report.setTargetType(StringUtils.substring(StringUtils.trim(body.getTargetType()), 0, 32));
        report.setTargetId(body.getTargetId());
        report.setReason(StringUtils.substring(StringUtils.trim(body.getReason()), 0, 64));
        report.setDetail(StringUtils.substring(StringUtils.trim(body.getDetail()), 0, 500));
        report.setPagePath(StringUtils.substring(StringUtils.trim(body.getPagePath()), 0, 128));
        report.setClientIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        report.setUserAgent(StringUtils.substring(ServletUtils.getRequest().getHeader("User-Agent"), 0, 255));
        if (contentReportService.insertContentReport(report) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "举报提交失败");
        }
        return null;
    }
}
