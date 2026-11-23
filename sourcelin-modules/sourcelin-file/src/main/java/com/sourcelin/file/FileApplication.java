package com.sourcelin.file;

import com.github.tobato.fastdfs.FdfsClientConfig;
import org.springframework.context.annotation.Import;
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
@Import(FdfsClientConfig.class)
@SpringBootApplication
public class FileApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(FileApplication.class, args);
        System.out.println("()  文件服务模块启动成功   (`) ");
    }
}
