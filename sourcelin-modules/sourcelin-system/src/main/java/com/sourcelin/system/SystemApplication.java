package com.sourcelin.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.sourcelin.common.security.annotation.EnableCustomConfig;
import com.sourcelin.common.security.annotation.EnableRyFeignClients;
/**
 * 系统模块
 * 
 * @author sourcelin
 */
@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class SystemApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(SystemApplication.class, args);
        System.out.println("()  系统模块启动成功   (`) ");
    }
}
