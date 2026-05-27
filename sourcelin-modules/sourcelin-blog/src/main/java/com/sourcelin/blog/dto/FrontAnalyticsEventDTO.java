package com.sourcelin.blog.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class FrontAnalyticsEventDTO
{
    @NotBlank(message = "事件类型不能为空")
    @Size(max = 64, message = "事件类型过长")
    private String eventType;

    @Size(max = 128, message = "页面路径过长")
    private String pagePath;

    @Size(max = 32, message = "目标类型过长")
    private String targetType;

    private Long targetId;

    @Size(max = 4000, message = "附加数据过长")
    private String metadataJson;

    @Size(max = 32, message = "平台标识过长")
    private String platform;

    @Size(max = 64, message = "应用版本过长")
    private String appVersion;

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public String getPagePath()
    {
        return pagePath;
    }

    public void setPagePath(String pagePath)
    {
        this.pagePath = pagePath;
    }

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

    public String getMetadataJson()
    {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson)
    {
        this.metadataJson = metadataJson;
    }

    public String getPlatform()
    {
        return platform;
    }

    public void setPlatform(String platform)
    {
        this.platform = platform;
    }

    public String getAppVersion()
    {
        return appVersion;
    }

    public void setAppVersion(String appVersion)
    {
        this.appVersion = appVersion;
    }
}
