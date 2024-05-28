package com.mexico.sas.nativequeries.api.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class EmailUtils {

    @Value("${spring.mail.username}")
    private String username;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendMessage(String to, String subject, String text) {
        log.debug("Sending simple message {} to {}", subject, to);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(username);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
            log.debug("Email sent successfully");
        } catch (Exception e) {
            log.error("Error sending simple message email", e);
        }
    }

    public void sendMessage(String to, String subject, String templateName, Map<String, Object> variables,
                            String currentUserEmail, String bossEmail, String pmBossEmal) {
        log.debug("Sending HTML message {} with template {}, to {}", subject, templateName, to);
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(variables);
        try {
            helper.setFrom(username);
            helper.setTo(to);
            helper.setCc(listToArray(currentUserEmail, bossEmail, pmBossEmal));
            helper.setSubject(subject);
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);
            emailSender.send(mimeMessage);
            log.debug("Email sent successfully");
        } catch (MessagingException e) {
            log.error("Error sending htlm message email", e);
        }
    }

    private String[] listToArray(String currentUserEmail, String bossEmail, String pmBossEmal) {
        List<String> cc = new ArrayList<>();
        cc.add(currentUserEmail);
        cc.add(bossEmail);
        if(pmBossEmal != null ) {
            cc.add(pmBossEmal);
        }
        log.debug("CC to mail: {}", cc);
        return cc.toArray(new String[cc.size()]);
    }
}
