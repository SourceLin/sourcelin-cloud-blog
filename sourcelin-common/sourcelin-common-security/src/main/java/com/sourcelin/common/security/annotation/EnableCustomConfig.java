package com.sourcelin.common.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sourcelin.common.security.accessor.AdminLoginAccessor;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import com.sourcelin.common.security.feign.FeignAutoConfiguration;
import com.sourcelin.common.security.stp.AdminStpInterface;
import com.sourcelin.common.security.stp.BlogStpInterface;
import com.sourcelin.common.security.stp.CompositeStpInterface;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import com.sourcelin.common.security.config.ApplicationConfig;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.sourcelin.**.mapper")
@EnableAsync
@Import({ 
    ApplicationConfig.class, 
    FeignAutoConfiguration.class,
    CompositeStpInterface.class,
    AdminStpInterface.class,
    BlogStpInterface.class,
    AdminLoginAccessor.class,
    BlogLoginAccessor.class
})
public @interface EnableCustomConfig
{
}
