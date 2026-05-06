package com.sourcelin.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.sourcelin.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.poi.ExcelUtil;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sourcelin.common.security.accessor.AdminLoginAccessor;
import com.sourcelin.system.domain.SysConfig;

/**
 * 参数配置 信息操作处理
 * 
 * @author sourcelin
 */
@RestController
@RequestMapping("/config")
public class SysConfigController extends BaseController
{
    @Autowired
    private AdminLoginAccessor adminLoginAccessor;

    @Autowired
    private ISysConfigService configService;

    /**
     * 获取参数配置列表
     */
    @SaCheckPermission(type = "admin", value = "system:config:list")
    @GetMapping("/list")
    public PageResult<SysConfig> list(SysConfig config)
    {
        startPage();
        List<SysConfig> list = configService.selectConfigList(config);
        return toPageResult(list);
    }

    @Log(title = "参数管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission(type = "admin", value = "system:config:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysConfig config)
    {
        List<SysConfig> list = configService.selectConfigList(config);
        ExcelUtil<SysConfig> util = new ExcelUtil<SysConfig>(SysConfig.class);
        util.exportExcel(response, list, "参数数据");
    }

    /**
     * 根据参数编号获取详细信息
     */
    @GetMapping(value = "/{configId}")
    public SysConfig getInfo(@PathVariable Long configId)
    {
        return configService.selectConfigById(configId);
    }

    /**
     * 根据参数键名查询参数值
     */
    @GetMapping(value = "/configKey/{configKey}")
    public String getConfigKey(@PathVariable String configKey)
    {
        return configService.selectConfigByKey(configKey);
    }

    /**
     * 新增参数配置
     */
    @SaCheckPermission(type = "admin", value = "system:config:add")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Boolean add(@Validated @RequestBody SysConfig config)
    {
        assertNotReservedConfigKey(config.getConfigKey(), "新增");
        if (!configService.checkConfigKeyUnique(config))
        {
            throw new BusinessException(ResultCode.CONFLICT, "新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setCreateBy(adminLoginAccessor.getUsername());
        if (configService.insertConfig(config) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增参数失败");
        }
        return true;
    }

    /**
     * 修改参数配置
     */
    @SaCheckPermission(type = "admin", value = "system:config:edit")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Boolean edit(@Validated @RequestBody SysConfig config)
    {
        assertNotReservedConfigKey(config.getConfigKey(), "修改");
        SysConfig currentConfig = configService.selectConfigById(config.getConfigId());
        assertNotReservedConfigKey(currentConfig == null ? null : currentConfig.getConfigKey(), "修改");
        if (!configService.checkConfigKeyUnique(config))
        {
            throw new BusinessException(ResultCode.CONFLICT, "修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setUpdateBy(adminLoginAccessor.getUsername());
        if (configService.updateConfig(config) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改参数失败");
        }
        return true;
    }

    /**
     * 删除参数配置
     */
    @SaCheckPermission(type = "admin", value = "system:config:remove")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public Boolean remove(@PathVariable Long[] configIds)
    {
        for (Long configId : configIds)
        {
            SysConfig config = configService.selectConfigById(configId);
            assertNotReservedConfigKey(config == null ? null : config.getConfigKey(), "删除");
        }
        configService.deleteConfigByIds(configIds);
        return true;
    }

    /**
     * 刷新参数缓存
     */
    @SaCheckPermission(type = "admin", value = "system:config:refresh")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public Boolean refreshCache()
    {
        configService.resetConfigCache();
        return true;
    }

    /**
     * 根据参数键名更新参数值
     */
    @SaCheckPermission(type = "admin", value = "system:config:edit")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping("/key/{configKey}")
    public Boolean updateByKey(@PathVariable String configKey, @RequestBody String configValue)
    {
        assertNotReservedConfigKey(configKey, "更新");
        if (configService.updateConfigByKey(configKey, configValue) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "更新参数失败");
        }
        return true;
    }

    private void assertNotReservedConfigKey(String configKey, String action)
    {
        if (configService.isReservedConfigKey(configKey))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "不允许通过参数中心" + action + "保留配置键: " + configKey);
        }
    }
}
