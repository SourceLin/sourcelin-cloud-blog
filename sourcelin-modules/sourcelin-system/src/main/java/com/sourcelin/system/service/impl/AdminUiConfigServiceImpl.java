package com.sourcelin.system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sourcelin.common.core.constant.CacheConstants;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.redis.service.RedisService;
import com.sourcelin.system.domain.SysConfig;
import com.sourcelin.system.domain.vo.AdminUiConfigAggregateVo;
import com.sourcelin.system.domain.vo.AdminUiDefaultsVo;
import com.sourcelin.system.domain.vo.AdminUiPolicyVo;
import com.sourcelin.system.mapper.SysConfigMapper;
import com.sourcelin.system.service.IAdminUiConfigService;
import com.sourcelin.system.service.ISysConfigService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 管理后台 UI 配置服务实现。
 */
@Service
public class AdminUiConfigServiceImpl implements IAdminUiConfigService
{
    static final String DEFAULTS_KEY = "sys.admin.ui.defaults";
    static final String POLICY_KEY = "sys.admin.ui.policy";
    static final String PRESETS_KEY = "sys.admin.ui.presets";

    private static final List<String> DEFAULT_PRESETS = Arrays.asList(
        "#4B55FA",
        "#0F766E",
        "#C2410C",
        "#1D4ED8",
        "#BE185D"
    );

    private final ISysConfigService sysConfigService;
    private final SysConfigMapper sysConfigMapper;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    public AdminUiConfigServiceImpl(
        ISysConfigService sysConfigService,
        SysConfigMapper sysConfigMapper,
        RedisService redisService,
        ObjectMapper objectMapper
    )
    {
        this.sysConfigService = sysConfigService;
        this.sysConfigMapper = sysConfigMapper;
        this.redisService = redisService;
        this.objectMapper = objectMapper;
    }

    @Override
    public AdminUiConfigAggregateVo getConfig()
    {
        AdminUiConfigAggregateVo config = new AdminUiConfigAggregateVo();
        config.setDefaults(readDefaults());
        config.setPolicy(readPolicy());
        config.setPresets(readPresets());
        return config;
    }

    @Override
    public boolean updateConfig(AdminUiConfigAggregateVo payload)
    {
        validate(payload);
        writeJson(DEFAULTS_KEY, payload.getDefaults());
        writeJson(POLICY_KEY, payload.getPolicy());
        writeJson(PRESETS_KEY, payload.getPresets());
        return true;
    }

    private AdminUiDefaultsVo readDefaults()
    {
        String rawValue = sysConfigService.selectConfigByKey(DEFAULTS_KEY);
        if (StringUtils.isBlank(rawValue))
        {
            return buildDefaultDefaults();
        }
        AdminUiDefaultsVo defaults = readJson(rawValue, AdminUiDefaultsVo.class, DEFAULTS_KEY);
        return normalizeDefaults(defaults);
    }

    private AdminUiPolicyVo readPolicy()
    {
        String rawValue = sysConfigService.selectConfigByKey(POLICY_KEY);
        if (StringUtils.isBlank(rawValue))
        {
            return buildDefaultPolicy();
        }
        AdminUiPolicyVo policy = readJson(rawValue, AdminUiPolicyVo.class, POLICY_KEY);
        return normalizePolicy(policy);
    }

    private List<String> readPresets()
    {
        String rawValue = sysConfigService.selectConfigByKey(PRESETS_KEY);
        if (StringUtils.isBlank(rawValue))
        {
            return DEFAULT_PRESETS;
        }
        List<String> presets = readJson(rawValue, new TypeReference<List<String>>() { }, PRESETS_KEY);
        return StringUtils.isEmpty(presets) ? DEFAULT_PRESETS : presets;
    }

    private AdminUiDefaultsVo buildDefaultDefaults()
    {
        AdminUiDefaultsVo defaults = new AdminUiDefaultsVo();
        defaults.setThemePreference("system");
        defaults.setLayout("left");
        defaults.setSidebarAppearance("classic");
        defaults.setThemeColor("#4B55FA");
        defaults.setShowTagsView(Boolean.TRUE);
        defaults.setShowAppLogo(Boolean.TRUE);
        defaults.setPageSwitchingAnimation("fade-slide");
        defaults.setComponentSize("default");
        return defaults;
    }

    private AdminUiDefaultsVo normalizeDefaults(AdminUiDefaultsVo defaults)
    {
        AdminUiDefaultsVo normalized = defaults == null ? new AdminUiDefaultsVo() : defaults;
        if (StringUtils.isBlank(normalized.getThemePreference()))
        {
            normalized.setThemePreference("system");
        }
        if (StringUtils.isBlank(normalized.getLayout()))
        {
            normalized.setLayout("left");
        }
        if (StringUtils.isBlank(normalized.getSidebarAppearance()))
        {
            normalized.setSidebarAppearance("classic");
        }
        if (StringUtils.isBlank(normalized.getThemeColor()))
        {
            normalized.setThemeColor("#4B55FA");
        }
        if (normalized.getShowTagsView() == null)
        {
            normalized.setShowTagsView(Boolean.TRUE);
        }
        if (normalized.getShowAppLogo() == null)
        {
            normalized.setShowAppLogo(Boolean.TRUE);
        }
        if (StringUtils.isBlank(normalized.getPageSwitchingAnimation()))
        {
            normalized.setPageSwitchingAnimation("fade-slide");
        }
        if (StringUtils.isBlank(normalized.getComponentSize()))
        {
            normalized.setComponentSize("default");
        }
        return normalized;
    }

    private AdminUiPolicyVo buildDefaultPolicy()
    {
        AdminUiPolicyVo policy = new AdminUiPolicyVo();
        policy.setAllowCustomThemeColor(Boolean.TRUE);
        policy.setAllowUserLayoutSwitch(Boolean.TRUE);
        policy.setAllowUserSidebarAppearanceSwitch(Boolean.TRUE);
        return policy;
    }

    private AdminUiPolicyVo normalizePolicy(AdminUiPolicyVo policy)
    {
        AdminUiPolicyVo normalized = policy == null ? new AdminUiPolicyVo() : policy;
        if (normalized.getAllowCustomThemeColor() == null)
        {
            normalized.setAllowCustomThemeColor(Boolean.TRUE);
        }
        if (normalized.getAllowUserLayoutSwitch() == null)
        {
            normalized.setAllowUserLayoutSwitch(Boolean.TRUE);
        }
        if (normalized.getAllowUserSidebarAppearanceSwitch() == null)
        {
            normalized.setAllowUserSidebarAppearanceSwitch(Boolean.TRUE);
        }
        return normalized;
    }

    private void validate(AdminUiConfigAggregateVo payload)
    {
        if (payload == null || payload.getDefaults() == null || payload.getPolicy() == null || StringUtils.isEmpty(payload.getPresets()))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "管理后台 UI 配置不能为空");
        }
    }

    private void writeJson(String key, Object value)
    {
        try
        {
            String configValue = objectMapper.writeValueAsString(value);
            int updated = sysConfigMapper.updateConfigByKey(key, configValue);
            if (updated <= 0)
            {
                insertMissingConfig(key, configValue);
                return;
            }
            redisService.setCacheObject(CacheConstants.SYS_CONFIG_KEY + key, configValue);
        }
        catch (JsonProcessingException e)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "序列化管理后台 UI 配置失败: " + key, e.getMessage());
        }
    }

    private void insertMissingConfig(String key, String configValue)
    {
        if (sysConfigMapper.insertConfig(buildMissingConfig(key, configValue)) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "更新管理后台 UI 配置失败: " + key);
        }
        redisService.setCacheObject(CacheConstants.SYS_CONFIG_KEY + key, configValue);
    }

    private SysConfig buildMissingConfig(String key, String configValue)
    {
        SysConfig config = new SysConfig();
        config.setConfigKey(key);
        config.setConfigValue(configValue);
        config.setConfigType("Y");
        config.setCreateBy("system");
        if (DEFAULTS_KEY.equals(key))
        {
            config.setConfigName("管理后台UI默认配置");
            config.setRemark("管理后台默认 UI 配置");
        }
        else if (POLICY_KEY.equals(key))
        {
            config.setConfigName("管理后台UI策略");
            config.setRemark("管理后台 UI 策略");
        }
        else if (PRESETS_KEY.equals(key))
        {
            config.setConfigName("管理后台主题色预设");
            config.setRemark("管理后台主题色预设");
        }
        else
        {
            config.setConfigName(key);
            config.setRemark("管理后台 UI 配置");
        }
        return config;
    }

    private <T> T readJson(String rawValue, Class<T> clazz, String key)
    {
        try
        {
            return objectMapper.readValue(rawValue, clazz);
        }
        catch (IOException e)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "解析管理后台 UI 配置失败: " + key, e.getMessage());
        }
    }

    private <T> T readJson(String rawValue, TypeReference<T> typeReference, String key)
    {
        try
        {
            return objectMapper.readValue(rawValue, typeReference);
        }
        catch (IOException e)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "解析管理后台 UI 配置失败: " + key, e.getMessage());
        }
    }
}
