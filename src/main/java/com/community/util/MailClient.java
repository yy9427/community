package com.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;
    //邮件发件人
    @Value("${spring.mail.username}")
    private String fromUser;

    public void sendMail(String toUser, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(fromUser);
            helper.setTo(toUser);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(helper.getMimeMessage());

        } catch (MessagingException e) {
            logger.error("邮件发送失败:" + e.getMessage());
        }


    }


}
