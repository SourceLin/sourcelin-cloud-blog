package com.sourcelin.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.WebApplicationType;

/**
 * 网关启动程序
 * 
 * @author sourcelin
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class GatewayApplication
{
    public static void main(String[] args)
    {
        // 依赖树中可能同时存在 spring-webmvc 与 webflux，显式强制网关以 Reactive 模式启动
        SpringApplication application = new SpringApplication(GatewayApplication.class);
        application.setWebApplicationType(WebApplicationType.REACTIVE);
        application.run(args);
        System.out.println("()  网关启动成功   (`) ");
    }
}
