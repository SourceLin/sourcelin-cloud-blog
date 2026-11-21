package com.sourcelin.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * AI 配置属性
 * 所有 AI 能力配置已迁移至数据库 ai_ability_config 表，本类仅保留最小结构以备后续扩展
 */
@Configuration
@ConfigurationProperties(prefix = "ai")
@RefreshScope
public class AiProperties {

    /**
     * 能力配置映射（预留，当前不使用）
     */
    private Map<String, AbilityConfig> abilities = new HashMap<>();

    public Map<String, AbilityConfig> getAbilities() {
        return abilities;
    }

    public void setAbilities(Map<String, AbilityConfig> abilities) {
        this.abilities = abilities;
    }

    /**
     * 能力配置（预留）
     */
    public static class AbilityConfig {
        private String providerType;
        private String endpointUrl;
        private String apiKey;
        private String providerAppId;
        private boolean enabled = true;
        private Map<String, String> properties = new HashMap<>();

        public String getProviderType() {
            return providerType;
        }

        public void setProviderType(String providerType) {
            this.providerType = providerType;
        }

        public String getEndpointUrl() {
            return endpointUrl;
        }

        public void setEndpointUrl(String endpointUrl) {
            this.endpointUrl = endpointUrl;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getProviderAppId() {
            return providerAppId;
        }

        public void setProviderAppId(String providerAppId) {
            this.providerAppId = providerAppId;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Map<String, String> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, String> properties) {
            this.properties = properties;
        }
    }
}
