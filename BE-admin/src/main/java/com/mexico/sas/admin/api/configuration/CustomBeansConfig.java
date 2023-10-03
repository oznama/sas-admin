package com.mexico.sas.admin.api.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author Oziel Naranjo
 */
@Configuration
@EnableAsync
@EnableScheduling
@Slf4j
public class CustomBeansConfig {

    @Bean(name = "ExecutorAsync")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(32);
        executor.setQueueCapacity(Integer.MAX_VALUE);
        executor.setThreadNamePrefix("ExecutorAsync-");
        executor.initialize();
        return executor;
    }
}
