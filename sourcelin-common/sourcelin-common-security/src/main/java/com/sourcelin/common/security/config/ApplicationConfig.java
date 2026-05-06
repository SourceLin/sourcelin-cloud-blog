package com.sourcelin.common.security.config;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.interceptor.SaInterceptor;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import com.sourcelin.common.security.stp.StpAdminUtil;
import com.sourcelin.common.security.stp.StpBlogUtil;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 系统配置
 *
 * @author sourcelin
 */
@Configuration(proxyBeanMethods = false)
public class ApplicationConfig implements WebMvcConfigurer
{
    @PostConstruct
    public void registerStpLogics()
    {
        SaManager.putStpLogic(StpAdminUtil.stpLogic);
        SaManager.putStpLogic(StpBlogUtil.stpLogic);
    }

    /**
     * 时区配置
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization()
    {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.timeZone(TimeZone.getDefault());
    }

    /**
     * 注册 Sa-Token MVC 拦截器，启用 @SaCheckPermission/@SaCheckRole 等注解鉴权。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
    }
}
