package com.sourcelin.common.core.config;

import com.github.pagehelper.PageInterceptor;
import java.util.Properties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/** 使用支持 JDK 17 的 PageHelper 核心，由 MyBatis 自动注册分页插件。 */
@AutoConfiguration
public class PageHelperConfiguration {
    @Bean
    @ConditionalOnMissingBean(PageInterceptor.class)
    public PageInterceptor pageInterceptor(Environment environment) {
        PageInterceptor interceptor = new PageInterceptor();
        interceptor.setProperties(Binder.get(environment).bind("pagehelper", Properties.class)
                .orElseGet(Properties::new));
        return interceptor;
    }
}
