package com.mexico.sas.admin.api.i18n;

import com.mexico.sas.admin.api.security.AuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * @author Oziel Naranjo
 */
@Component
public class I18nResolver {

    public static MessageSource messageSource;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        I18nResolver.messageSource = messageSource;
    }

    public static String getMessage(String key) {
        return messageSource.getMessage(key, null, AuthorizationFilter.LOCALE);
    }

    public static String getMessage(String key, Object ... args) {
        return String.format(messageSource.getMessage(key, null, AuthorizationFilter.LOCALE), args);
    }
}
