package com.sourcelin.blog.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class FrontSubscribeAuthorizationDTO
{
    @NotBlank(message = "订阅模板不能为空")
    @Size(max = 128, message = "订阅模板过长")
    private String templateId;

    @NotBlank(message = "授权状态不能为空")
    @Size(max = 32, message = "授权状态过长")
    private String authorizationStatus;

    @Size(max = 64, message = "订阅场景过长")
    private String scene;

    @Size(max = 32, message = "平台标识过长")
    private String platform;

    public String getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(String templateId)
    {
        this.templateId = templateId;
    }

    public String getAuthorizationStatus()
    {
        return authorizationStatus;
    }

    public void setAuthorizationStatus(String authorizationStatus)
    {
        this.authorizationStatus = authorizationStatus;
    }

    public String getScene()
    {
        return scene;
    }

    public void setScene(String scene)
    {
        this.scene = scene;
    }

    public String getPlatform()
    {
        return platform;
    }

    public void setPlatform(String platform)
    {
        this.platform = platform;
    }
}
