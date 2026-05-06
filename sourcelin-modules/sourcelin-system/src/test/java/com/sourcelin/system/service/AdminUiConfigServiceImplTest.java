package com.sourcelin.system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sourcelin.common.core.constant.CacheConstants;
import com.sourcelin.common.redis.service.RedisService;
import com.sourcelin.system.domain.SysConfig;
import com.sourcelin.system.domain.vo.AdminUiConfigAggregateVo;
import com.sourcelin.system.domain.vo.AdminUiDefaultsVo;
import com.sourcelin.system.domain.vo.AdminUiPolicyVo;
import com.sourcelin.system.mapper.SysConfigMapper;
import com.sourcelin.system.service.impl.AdminUiConfigServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminUiConfigServiceImplTest {

    private final ISysConfigService sysConfigService = mock(ISysConfigService.class);
    private final SysConfigMapper sysConfigMapper = mock(SysConfigMapper.class);
    private final RedisService redisService = mock(RedisService.class);
    private final AdminUiConfigServiceImpl service =
        new AdminUiConfigServiceImpl(sysConfigService, sysConfigMapper, redisService, new ObjectMapper());

    @Test
    void shouldReturnFallbackWhenConfigIsBlank() {
        when(sysConfigService.selectConfigByKey("sys.admin.ui.defaults")).thenReturn("");
        when(sysConfigService.selectConfigByKey("sys.admin.ui.policy")).thenReturn("");
        when(sysConfigService.selectConfigByKey("sys.admin.ui.presets")).thenReturn("");

        AdminUiConfigAggregateVo result = service.getConfig();

        assertEquals("system", result.getDefaults().getThemePreference());
        assertEquals("left", result.getDefaults().getLayout());
        assertTrue(result.getPolicy().getAllowCustomThemeColor());
        assertFalse(result.getPresets().isEmpty());
    }

    @Test
    void shouldWriteThreeConfigKeysWhenUpdateConfig() {
        AdminUiConfigAggregateVo payload = new AdminUiConfigAggregateVo();
        payload.setDefaults(new AdminUiDefaultsVo());
        payload.setPolicy(new AdminUiPolicyVo());
        payload.setPresets(Arrays.asList("#4B55FA", "#0F766E"));

        when(sysConfigMapper.updateConfigByKey(anyString(), anyString())).thenReturn(1);

        service.updateConfig(payload);

        verify(sysConfigMapper).updateConfigByKey(eq("sys.admin.ui.defaults"), anyString());
        verify(sysConfigMapper).updateConfigByKey(eq("sys.admin.ui.policy"), anyString());
        verify(sysConfigMapper).updateConfigByKey(eq("sys.admin.ui.presets"), anyString());
        verify(redisService).setCacheObject(eq(CacheConstants.SYS_CONFIG_KEY + "sys.admin.ui.defaults"), anyString());
        verify(redisService).setCacheObject(eq(CacheConstants.SYS_CONFIG_KEY + "sys.admin.ui.policy"), anyString());
        verify(redisService).setCacheObject(eq(CacheConstants.SYS_CONFIG_KEY + "sys.admin.ui.presets"), anyString());
    }

    @Test
    void shouldInsertMissingConfigWhenUpdateKeyDoesNotExist() {
        AdminUiConfigAggregateVo payload = new AdminUiConfigAggregateVo();
        payload.setDefaults(new AdminUiDefaultsVo());
        payload.setPolicy(new AdminUiPolicyVo());
        payload.setPresets(Arrays.asList("#4B55FA", "#0F766E"));

        when(sysConfigMapper.updateConfigByKey(anyString(), anyString())).thenReturn(0);
        when(sysConfigMapper.insertConfig(any(SysConfig.class))).thenReturn(1);

        service.updateConfig(payload);

        verify(sysConfigMapper, times(3)).insertConfig(any(SysConfig.class));
        verify(redisService, times(3)).setCacheObject(anyString(), anyString());
    }

    @Test
    void shouldKeepWriteReadConsistentForAdminUiConfig() {
        Map<String, String> memoryStore = new HashMap<>();
        when(sysConfigMapper.updateConfigByKey(anyString(), anyString())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            String value = invocation.getArgument(1);
            memoryStore.put(key, value);
            return 1;
        });
        when(sysConfigService.selectConfigByKey(anyString())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            return memoryStore.getOrDefault(key, "");
        });

        AdminUiDefaultsVo defaults = new AdminUiDefaultsVo();
        defaults.setThemePreference("dark");
        defaults.setLayout("mix");
        defaults.setSidebarAppearance("minimal");
        defaults.setThemeColor("#123456");
        defaults.setShowTagsView(Boolean.FALSE);
        defaults.setShowAppLogo(Boolean.FALSE);
        defaults.setPageSwitchingAnimation("fade");
        defaults.setComponentSize("small");

        AdminUiPolicyVo policy = new AdminUiPolicyVo();
        policy.setAllowCustomThemeColor(Boolean.FALSE);
        policy.setAllowUserLayoutSwitch(Boolean.TRUE);
        policy.setAllowUserSidebarAppearanceSwitch(Boolean.FALSE);

        AdminUiConfigAggregateVo payload = new AdminUiConfigAggregateVo();
        payload.setDefaults(defaults);
        payload.setPolicy(policy);
        payload.setPresets(Arrays.asList("#123456", "#654321"));

        service.updateConfig(payload);
        AdminUiConfigAggregateVo result = service.getConfig();

        assertEquals("dark", result.getDefaults().getThemePreference());
        assertEquals("mix", result.getDefaults().getLayout());
        assertEquals("minimal", result.getDefaults().getSidebarAppearance());
        assertEquals("#123456", result.getDefaults().getThemeColor());
        assertEquals(Boolean.FALSE, result.getDefaults().getShowTagsView());
        assertEquals(Boolean.FALSE, result.getDefaults().getShowAppLogo());
        assertEquals("fade", result.getDefaults().getPageSwitchingAnimation());
        assertEquals("small", result.getDefaults().getComponentSize());
        assertEquals(Boolean.FALSE, result.getPolicy().getAllowCustomThemeColor());
        assertEquals(Boolean.TRUE, result.getPolicy().getAllowUserLayoutSwitch());
        assertEquals(Boolean.FALSE, result.getPolicy().getAllowUserSidebarAppearanceSwitch());
        assertEquals(Arrays.asList("#123456", "#654321"), result.getPresets());
    }
}
