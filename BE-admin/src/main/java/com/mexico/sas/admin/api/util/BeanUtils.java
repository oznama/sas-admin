package com.mexico.sas.admin.api.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author Oziel Naranjo
 */
@Service
public class BeanUtils implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static  Object getBean(String beanId) {
        return applicationContext.getBean(beanId);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanUtils.applicationContext = applicationContext;
    }
}
