package com.sourcelin.ai.service;

import com.sourcelin.ai.domain.AiAbilityConfig;
import com.sourcelin.ai.domain.ProviderTypeEnum;
import com.sourcelin.ai.mapper.AiAbilityConfigMapper;
import com.sourcelin.ai.provider.AiProviderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RefreshScope
public class AiAbilityRouter {

    @Autowired
    private AiAbilityConfigMapper aiAbilityConfigMapper;

    @Autowired
    private List<AiProviderClient> providerClients;

    /**
     * 根据能力编码获取其对应的配置信息（带缓存）
     * 配置全部来自数据库 ai_ability_config 表，不再依赖 Nacos
     */
    @Cacheable(value = "aiAbilityConfig", key = "#abilityCode", unless = "#result == null")
    public AiAbilityConfig getAbilityConfig(String abilityCode) {
        AiAbilityConfig config = aiAbilityConfigMapper.selectByAbilityCode(abilityCode);
        if (config == null) {
            throw new IllegalArgumentException(
                "AI 能力未配置: " + abilityCode + "，请在管理后台「AI 能力配置」页面添加");
        }
        return config;
    }

    /**
     * 清除指定能力的配置缓存
     */
    @CacheEvict(value = "aiAbilityConfig", key = "#abilityCode")
    public void evictAbilityConfigCache(String abilityCode) {
        // 仅清除缓存，无需额外逻辑
    }

    /**
     * 清除所有 AI 能力配置缓存
     */
    @CacheEvict(value = "aiAbilityConfig", allEntries = true)
    public void evictAllAbilityConfigCache() {
        // 仅清除缓存，无需额外逻辑
    }

    /**
     * 根据提供商类型获取对应的 Provider Client
     */
    public AiProviderClient route(ProviderTypeEnum providerType) {
        for (AiProviderClient client : providerClients) {
            if (client.supports(providerType)) {
                return client;
            }
        }
        throw new UnsupportedOperationException("No AiProviderClient matched for providerType: " + providerType.getCode());
    }

    /**
     * 根据提供商类型字符串获取对应的 Provider Client（兼容旧调用方）
     */
    public AiProviderClient route(String providerTypeCode) {
        return route(ProviderTypeEnum.fromCode(providerTypeCode));
    }
}
