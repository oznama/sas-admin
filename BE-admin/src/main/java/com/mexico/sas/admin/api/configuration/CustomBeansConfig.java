package com.mexico.sas.admin.api.configuration;

import com.mexico.sas.admin.api.i18n.I18nResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.concurrent.Executor;

/**
 * @author Oziel Naranjo
 */
@Configuration
@Slf4j
public class CustomBeansConfig {

    @Bean
    public LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(I18nResolver.messageSource);
        return bean;
    }
}
