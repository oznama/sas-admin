package com.mexico.sas.nativequeries.api.mail;

import com.mexico.sas.nativequeries.api.repository.CatalogRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
public class EmailUtils {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private String port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String smtpAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String starttlsEnabled;

    @Value("${spring.mail.debug}")
    private String mailDebug;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private CatalogRepository catalogRepository;

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
                            String pmBossEmal) {
        log.debug("Sending HTML message {} with template {}, to {}", subject, templateName, to);
        sendMessage(username, emailSender, to, subject, templateName, variables, null, null, listToArray(pmBossEmal));
    }

    public void sendMessage(String from, String password, String to, String subject, String templateName,
                            Map<String, Object> variables, String fileName, InputStream inputStream, String... cc) {
        log.debug("Sending HTML message {} with template {}, from {}, to {}", subject, templateName, from, to);
        JavaMailSenderImpl emailSender = new JavaMailSenderImpl();
        emailSender.setHost(host);
        emailSender.setPort(Integer.parseInt(port));
        emailSender.setUsername(from);
        emailSender.setPassword(password);

        Properties props = emailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.starttls.enable", starttlsEnabled);
        props.put("mail.debug", mailDebug);

        sendMessage(from, emailSender, to, subject, templateName, variables, fileName, inputStream, cc);
    }

    private void sendMessage(String username, JavaMailSender emailSender, String to, String subject, String templateName,
                             Map<String, Object> variables, String fileName, InputStream inputStream, String... cc) {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        if (!StringUtils.isEmpty(fileName) && inputStream != null) {
            try {
                helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            } catch (MessagingException e) {
                log.debug("Error to create mime message with multipart mode", e);
                return;
            }
        } else {
            helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
        }

        Context context = new Context();
        context.setVariables(variables);
        try {
            helper.setFrom(username);
            helper.setTo(to);
            helper.setCc(cc);
            helper.setSubject(subject);
            if (!StringUtils.isEmpty(fileName) && inputStream != null) {
                log.debug("Attaching file {} ...", fileName);
                try {
                    helper.addAttachment(fileName, new ByteArrayResource(IOUtils.toByteArray(inputStream)));
                } catch (IOException e) {
                    log.error("Error to add attachment to email", e);
                }
            }
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);
            emailSender.send(mimeMessage);
            log.debug("Email sent successfully");
        } catch (MessagingException e) {
            log.error("Error sending htlm message email", e);
        }
    }

    private String[] listToArray(String pmBossEmal) {
        List<String> cc = catalogRepository.getEmails();
        if( cc != null && pmBossEmal != null ) {
            cc.add(pmBossEmal);
        } else {
            cc = new ArrayList<>();
        }
        log.debug("CC to mail: {}", cc);
        return cc.toArray(new String[cc.size()]);
    }
}
