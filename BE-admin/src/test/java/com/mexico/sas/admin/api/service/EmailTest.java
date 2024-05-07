package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.util.EmailUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

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
        emailUtils.sendMessage(to, subject, "Este es un correo de prueba ejecutado con SpringBoot Mail");
    }

    @DisplayName("Send email html test")
    @Test
    public void sendEmailHtml() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("message", "Este es el mensaje de prueba creado desde sendEmailHtml");
        emailUtils.sendMessage(to, subject, "email-test", variables);
    }

}
