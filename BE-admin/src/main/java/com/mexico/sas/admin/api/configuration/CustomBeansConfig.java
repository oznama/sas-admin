package com.mexico.sas.admin.api.configuration;

import com.mexico.sas.admin.api.i18n.I18nResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

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

    @Bean
    public LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(I18nResolver.messageSource);
        return bean;
    }
}
