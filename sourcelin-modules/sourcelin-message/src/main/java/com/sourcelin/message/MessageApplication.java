package com.sourcelin.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import com.sourcelin.common.security.annotation.EnableCustomConfig;
import com.sourcelin.common.security.annotation.EnableRyFeignClients;

/**
 * 消息推送模块启动类
 * <p>
 * 本模块仅做 SSE 长连接推送 + Redis Pub/Sub，不操作数据库，
 * 但间接依赖了 pagehelper（通过 common-core），其传递的 mybatis-spring-boot-starter
 * 会触发 DataSourceAutoConfiguration，故需排除。
 */
@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class MessageApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessageApplication.class, args);
        System.out.println("(♥)  消息推送模块启动成功   (♥)");
    }
}
