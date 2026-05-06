package com.sourcelin.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.sourcelin.common.security.annotation.EnableCustomConfig;
import com.sourcelin.common.security.annotation.EnableRyFeignClients;
/**
 * 定时任务
 * 
 * @author sourcelin
 */
@EnableCustomConfig
@EnableRyFeignClients   
@SpringBootApplication
public class JobApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(JobApplication.class, args);
        System.out.println("()  定时任务模块启动成功   (`) ");
    }
}
