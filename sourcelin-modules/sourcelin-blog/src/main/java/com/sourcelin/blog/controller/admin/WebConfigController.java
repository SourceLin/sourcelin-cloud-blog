package com.sourcelin.blog.controller.admin;

import com.sourcelin.blog.domain.WebConfig;
import com.sourcelin.blog.service.IWebConfigService;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 网站配置Controller
 * 
 * @author sourcelin
 * @date 2026-01-04
 */
@RestController
@RequestMapping("/admin/config")
public class WebConfigController
{
    @Autowired
    private IWebConfigService webConfigService;

    /**
     * 获取网站配置
     */
    @SaCheckPermission(type = "admin", value = "blog:config:list")
    @GetMapping("/getWebConfig")
    public WebConfig getWebConfig()
    {
        return webConfigService.getWebConfig();
    }

    /**
     * 新增网站配置
     */
    @SaCheckPermission(type = "admin", value = "blog:config:add")
    @Log(title = "新增网站配置", businessType = BusinessType.INSERT)
    @PostMapping
    public Void add(@RequestBody WebConfig webConfig)
    {
        if (webConfigService.insertWebConfig(webConfig) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "新增网站配置失败");
        }
        return null;
    }

    /**
     * 修改网站配置
     */
    @SaCheckPermission(type = "admin", value = "blog:config:edit")
    @Log(title = "修改网站配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public Void edit(@RequestBody WebConfig webConfig)
    {
        if (webConfigService.updateWebConfig(webConfig) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "修改网站配置失败");
        }
        return null;
    }
}
