package com.mexico.sas.admin.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

/**
 * @author Oziel Naranjo
 */
@SpringBootTest
@TestPropertySource(locations = "/env-test.properties")
public class I18nTest {

    private final String CODE_HELLO = "hello";

    @Autowired
    private ResourceBundleMessageSource messageSource;

    @BeforeEach
    public void init() {
        messageSource.setBasenames("lang/messages");
    }

    @DisplayName("Test Spring Message Internalization ES")
    @Test
    public void esp() {
        String hello = messageSource.getMessage(CODE_HELLO, null, new Locale("es", "MX"));
        System.out.printf("Hello in es: %s\n", hello);
        assertEquals(hello, "Hola!");
    }

    @DisplayName("Test Spring Message Internalization IT")
    @Test
    public void it() {
        String hello = messageSource.getMessage(CODE_HELLO, null, Locale.ITALIAN);
        System.out.printf("Hello in it: %s\n", hello);
        assertEquals(hello, "Ciao!");
    }

    @DisplayName("Test Spring loop locales")
    @Test
    public void loop() {
        Collection<Locale> localesList = new ArrayList<>();
        localesList.add(Locale.forLanguageTag("en-GB"));
        localesList.add(Locale.forLanguageTag("en-US"));
        localesList.add(Locale.forLanguageTag("ja-*"));
        localesList.add(Locale.forLanguageTag("fe-FE"));
        localesList.add(Locale.forLanguageTag("es-MX"));

        for(Locale locale : localesList) {
            System.out.printf("%s %s %s %s\n", locale.getLanguage(), locale.getCountry(), locale.getDisplayName(), locale.getDisplayLanguage());
        }
    }

}
