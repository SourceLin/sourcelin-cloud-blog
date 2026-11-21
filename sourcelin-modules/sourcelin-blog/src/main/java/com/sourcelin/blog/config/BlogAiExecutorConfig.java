package com.sourcelin.blog.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;
@Configuration
public class BlogAiExecutorConfig {
 @Bean("blogAiStreamExecutor")
 public Executor blogAiStreamExecutor() {
  ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
  executor.setCorePoolSize(2); executor.setMaxPoolSize(8); executor.setQueueCapacity(16);
  executor.setThreadNamePrefix("blog-ai-stream-"); executor.setWaitForTasksToCompleteOnShutdown(false);
  executor.initialize(); return executor;
 }
}
