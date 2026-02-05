package com.phonenexus.identities.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(String toEmail, String token) {
        String subject = "Verify your PhoneNexus account";
        String verificationUrl = "http://localhost:8081/api/auth/verify-email?token=" + token;
        String message = "Please click the link below to verify your email address:\n" + verificationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(fromEmail);
        email.setTo(toEmail);
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }
}
