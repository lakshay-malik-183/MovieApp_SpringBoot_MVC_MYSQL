package com.spring_boot_project.movieApp.services.impl;

import com.spring_boot_project.movieApp.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String toEmail, String subject, String body) {

        try {
            //create Syntax for Mail
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

            //whom to send email (Receiver Email)
            simpleMailMessage.setTo(toEmail);
            //subject for email
            simpleMailMessage.setSubject(subject);
            //body of email
            simpleMailMessage.setText(body);

            //use javaMailSender Dependency to send Email
            javaMailSender.send(simpleMailMessage);
        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

}
