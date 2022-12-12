package com.practice.productservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendEmail(String emailReceiver, String subject, String text, String contactorEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(emailReceiver);
        message.setSubject(subject);
        message.setText(text + contactorEmail);
        try {
            javaMailSender.send(message);
        } catch (MailSendException e) {
            throw new MailSendException("Fail to send email.");
        }
    }
}
