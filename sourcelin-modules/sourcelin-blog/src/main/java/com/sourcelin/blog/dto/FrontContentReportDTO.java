package com.sourcelin.blog.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class FrontContentReportDTO
{
    @NotBlank(message = "目标类型不能为空")
    @Size(max = 32, message = "目标类型过长")
    private String targetType;

    @NotNull(message = "目标ID不能为空")
    private Long targetId;

    @NotBlank(message = "举报原因不能为空")
    @Size(max = 64, message = "举报原因过长")
    private String reason;

    @Size(max = 500, message = "补充说明过长")
    private String detail;

    @Size(max = 128, message = "页面路径过长")
    private String pagePath;

    public String getTargetType()
    {
        return targetType;
    }

    public void setTargetType(String targetType)
    {
        this.targetType = targetType;
    }

    public Long getTargetId()
    {
        return targetId;
    }

    public void setTargetId(Long targetId)
    {
        this.targetId = targetId;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getDetail()
    {
        return detail;
    }

    public void setDetail(String detail)
    {
        this.detail = detail;
    }

    public String getPagePath()
    {
        return pagePath;
    }

    public void setPagePath(String pagePath)
    {
        this.pagePath = pagePath;
    }
}
