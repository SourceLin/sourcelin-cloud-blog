package com.sourcelin.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.sourcelin.common.security.annotation.EnableCustomConfig;
import com.sourcelin.common.security.annotation.EnableRyFeignClients;
/**
 * 博客模块
 * 
 * @author sourcelin
 */
@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class BlogApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(BlogApplication.class, args);
        System.out.println("()  博客模块启动成功   (`) ");
    }
}
