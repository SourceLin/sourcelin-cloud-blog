package com.sourcelin.ai.provider;

/** 供应商请求发送前发现的配置错误，修正配置后可重试。 */
public class ProviderConfigurationException extends IllegalArgumentException {
    public ProviderConfigurationException(String message) {
        super(message);
    }
}
