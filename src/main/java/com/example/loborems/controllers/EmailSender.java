package com.example.loborems.controllers;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

public class EmailSender {

    private static final String EMAIL = "lyl67629@gmail.com"; // Your email address
    private static final String PASSWORD = "lpas kkob iaaa rxcf"; // Your email password
    private static final String SMTP_HOST = "smtp.gmail.com"; // Gmail SMTP server
    private static final int SMTP_PORT = 587; // SMTP port for TLS

    public static void sendResetEmail(String recipientEmail, String resetLink) {
        try {
            // Build the email message
             Email email = EmailBuilder.startingBlank()
                    .from("Your App", EMAIL)
                    .to(recipientEmail)
                    .withSubject("Reset Your Password")
                    .withPlainText("Click the link below to reset your password:\n" + resetLink)
                    .buildEmail();

            // Build the mailer
            Mailer mailer = MailerBuilder
                    .withSMTPServer(SMTP_HOST, SMTP_PORT, EMAIL, PASSWORD)
                    .withTransportStrategy(org.simplejavamail.api.mailer.config.TransportStrategy.SMTP_TLS)
                    .buildMailer();

            // Send the email
            mailer.sendMail(email);
            System.out.println("Reset email sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to send the email: " + e.getMessage());
        }
    }
}
