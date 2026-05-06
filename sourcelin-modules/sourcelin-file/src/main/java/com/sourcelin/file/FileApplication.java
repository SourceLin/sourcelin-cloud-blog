package com.sourcelin.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import com.sourcelin.common.security.annotation.EnableCustomConfig;
import com.sourcelin.common.security.annotation.EnableRyFeignClients;
/**
 * 文件服务
 * 
 * @author sourcelin
 */
@EnableCustomConfig
@EnableRyFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class FileApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(FileApplication.class, args);
        System.out.println("()  文件服务模块启动成功   (`) ");
    }
}
