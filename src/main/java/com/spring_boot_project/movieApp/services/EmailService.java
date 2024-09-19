package com.spring_boot_project.movieApp.services;

public interface EmailService {

    void sendEmail(String toEmail, String subject, String body);

}
