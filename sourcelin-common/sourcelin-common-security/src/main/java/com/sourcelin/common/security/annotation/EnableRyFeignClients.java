package com.sourcelin.common.security.annotation;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.annotation.AliasFor;
import java.lang.annotation.*;

/**
 * 自定义feign注解
 * 添加basePackages路径
 * 
 * @author sourcelin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableFeignClients
public @interface EnableRyFeignClients
{
    @AliasFor(annotation = EnableFeignClients.class, attribute = "value")
    String[] value() default { "com.sourcelin" };

    @AliasFor(annotation = EnableFeignClients.class, attribute = "basePackages")
    String[] basePackages() default { "com.sourcelin" };

    @AliasFor(annotation = EnableFeignClients.class, attribute = "basePackageClasses")
    Class<?>[] basePackageClasses() default {};

    @AliasFor(annotation = EnableFeignClients.class, attribute = "defaultConfiguration")
    Class<?>[] defaultConfiguration() default {};

    @AliasFor(annotation = EnableFeignClients.class, attribute = "clients")
    Class<?>[] clients() default {};
}
