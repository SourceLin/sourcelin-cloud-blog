package com.sourcelin.common.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class SaTokenConfig
{
    @Bean
    public cn.dev33.satoken.config.SaTokenConfig saTokenConfig()
    {
        cn.dev33.satoken.config.SaTokenConfig config = new cn.dev33.satoken.config.SaTokenConfig();
        config.setTokenName("Authorization");
        config.setTokenPrefix("Bearer");
        config.setIsReadHeader(true);
        config.setIsReadCookie(false);
        return config;
    }
}
