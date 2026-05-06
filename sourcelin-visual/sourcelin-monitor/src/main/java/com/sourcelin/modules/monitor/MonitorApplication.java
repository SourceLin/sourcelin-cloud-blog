package com.sourcelin.modules.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import de.codecentric.boot.admin.server.config.EnableAdminServer;

/**
 * 监控中心
 * 
 * @author sourcelin
 */
@EnableAdminServer
@SpringBootApplication(exclude = {
        // 监控中心仅作为 Spring Boot Admin Server 使用，不应强制依赖数据源。
        // classpath 中若存在 JDBC/MyBatis 相关依赖，默认会触发 DataSource 自动装配，
        // 在未配置 url/driver 时导致启动失败。
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class MonitorApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(MonitorApplication.class, args);
        System.out.println("()  监控中心启动成功   (`) ");
    }
}
