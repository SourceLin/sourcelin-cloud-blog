package com.sourcelin.blog.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.domain.ContentReport;
import com.sourcelin.blog.service.IContentReportService;
import com.sourcelin.blog.shared.support.BlogPageResults;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.core.web.page.PageDomain;
import com.sourcelin.common.core.web.page.TableSupport;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/report")
public class ContentReportAdminController
{
    @Autowired
    private IContentReportService contentReportService;

    @SaCheckPermission(type = "admin", value = "blog:report:list")
    @GetMapping("/list")
    public PageResult<ContentReport> list(ContentReport query)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        PageHelper.startPage(pageDomain.getPage(), pageDomain.getPageSize());
        List<ContentReport> list = contentReportService.selectContentReportList(query);
        PageInfo<ContentReport> pageInfo = new PageInfo<ContentReport>(list);
        return BlogPageResults.of(list, pageInfo);
    }

    @SaCheckPermission(type = "admin", value = "blog:report:query")
    @GetMapping("/{id}")
    public ContentReport getInfo(@PathVariable("id") Long id)
    {
        ContentReport report = contentReportService.selectContentReportById(id);
        if (report == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "举报记录不存在");
        }
        return report;
    }

    @SaCheckPermission(type = "admin", value = "blog:report:edit")
    @Log(title = "内容举报", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/status")
    public Void handle(@PathVariable("id") Long id, @RequestBody ReportHandleBody body)
    {
        if (body == null || StringUtils.isBlank(body.getStatus()))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "处理状态不能为空");
        }
        if (contentReportService.handleContentReport(id, body.getStatus(), body.getRemark()) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "处理举报失败");
        }
        return null;
    }

    @SaCheckPermission(type = "admin", value = "blog:report:remove")
    @Log(title = "内容举报", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Void remove(@PathVariable Long[] ids)
    {
        if (contentReportService.deleteContentReportByIds(ids) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除举报失败");
        }
        return null;
    }

    public static class ReportHandleBody
    {
        private String status;
        private String remark;

        public String getStatus()
        {
            return status;
        }

        public void setStatus(String status)
        {
            this.status = status;
        }

        public String getRemark()
        {
            return remark;
        }

        public void setRemark(String remark)
        {
            this.remark = remark;
        }
    }
}
