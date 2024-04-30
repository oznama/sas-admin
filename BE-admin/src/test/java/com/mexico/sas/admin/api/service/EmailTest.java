package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.util.EmailUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "/env-test.properties")
public class EmailTest {

    final String to = "oznama27@gmail.com";
    final String subject = "email test";

    @Autowired
    EmailUtils emailUtils;

    @DisplayName("Send email test")
    @Test
    public void sendEmailSimple() {
        emailUtils.sendSimpleMessage(to, subject, "Este es un correo de prueba ejecutado con SpringBoot Mail");
    }

}
