package com.sourcelin.system.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sourcelin.system.domain.vo.AdminUiConfigAggregateVo;
import com.sourcelin.system.service.IAdminUiConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理后台 UI 默认配置接口。
 */
@RestController
@RequestMapping("/admin-ui")
public class AdminUiController
{
    @Autowired
    private IAdminUiConfigService adminUiConfigService;

    @SaCheckLogin(type = "admin")
    @SaCheckPermission(type = "admin", value = "system:admin-ui:query")
    @GetMapping
    public AdminUiConfigAggregateVo getConfig()
    {
        return adminUiConfigService.getConfig();
    }

    @SaCheckPermission(type = "admin", value = "system:admin-ui:edit")
    @PutMapping
    public Boolean updateConfig(@Validated @RequestBody AdminUiConfigAggregateVo payload)
    {
        return adminUiConfigService.updateConfig(payload);
    }
}
