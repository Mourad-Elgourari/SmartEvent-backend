package com.congress.event.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService  {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String token) {
        String link = "http://localhost:9090/api/auth/verify?token=" + token;
        String html = "<h3>Activation de votre compte</h3>" +
                "<p>Cliquez sur le lien ci-dessous pour activer votre compte :</p>" +
                "<a href=\"" + link + "\">Activer mon compte</a>";

        sendEmail(to, "Activation de compte", html);
    }

    public void sendPasswordResetEmail(String to, String token) {
        String link = "http://localhost:9090/api/auth/reset-password?token=" + token;
        String html = "<h3>Réinitialisation de mot de passe</h3>" +
                "<p>Cliquez sur le lien ci-dessous pour réinitialiser votre mot de passe :</p>" +
                "<a href=\"" + link + "\">Réinitialiser</a>";

        sendEmail(to, "Réinitialisation de mot de passe", html);
    }

    private void sendEmail(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

