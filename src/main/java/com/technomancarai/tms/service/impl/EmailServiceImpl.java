package com.technomancarai.tms.service.impl;

import com.technomancarai.tms.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:your-email@gmail.com}")
    private String fromEmail;

    @Override
    public void sendOtpEmail(String toEmail, String otp, String purpose) {
        log.info("==================================================================");
        log.info("  [REAL-TIME OTP DISPATCH] Email: {} | Purpose: {} | OTP CODE: {}", toEmail, purpose, otp);
        log.info("==================================================================");

        String subject = "Flow Deck - Your OTP Verification Code";
        String htmlContent = buildHtmlContent(
                "OTP Verification",
                "Use the following One-Time Password (OTP) to complete your request for <strong>" + purpose + "</strong>:",
                "<div style='text-align: center; margin: 20px 0;'>" +
                        "<span style='font-size: 32px; font-weight: bold; letter-spacing: 6px; color: #4F46E5; background: #EEF2FF; padding: 12px 24px; border-radius: 8px; display: inline-block;'>" + otp + "</span>" +
                        "</div>" +
                        "<p style='color: #6B7280; font-size: 14px;'>This OTP is valid for 10 minutes. Please do not share this code with anyone.</p>"
        );

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String name) {
        log.info("Sending Welcome Email to: {}", toEmail);
        String subject = "Welcome to Flow Deck!";
        String htmlContent = buildHtmlContent(
                "Welcome to Flow Deck, " + name + "!",
                "We are excited to have you on board. Flow Deck helps you streamline task management, track progress, and collaborate seamlessly.",
                "<p style='font-size: 16px; color: #374151;'>Get started by exploring your dashboard and organizing your workspace projects!</p>"
        );

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String name) {
        log.info("Sending Password Reset Confirmation Email to: {}", toEmail);
        String subject = "Flow Deck - Password Reset Notification";
        String htmlContent = buildHtmlContent(
                "Password Reset Successful",
                "Hello " + name + ", your password for your Flow Deck account has been successfully reset.",
                "<p style='color: #EF4444; font-size: 14px;'>If you did not perform this action, please contact our support team immediately.</p>"
        );

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    @Override
    public void sendApprovalEmail(String toEmail, String name, String roleName) {
        log.info("Sending Account Approval Email to: {}", toEmail);
        String subject = "Flow Deck - Account Approved!";
        String htmlContent = buildHtmlContent(
                "Account Approved, " + name + "!",
                "Great news! Your registration request for Flow Deck has been approved by the Admin.",
                "<p style='font-size: 16px; color: #374151;'>Assigned Role: <strong>" + roleName + "</strong></p>" +
                "<p style='font-size: 14px; color: #4B5563;'>You can now log in to your account and start using Flow Deck.</p>"
        );

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    @Override
    public void sendRejectionEmail(String toEmail, String name, String reason) {
        log.info("Sending Account Rejection Email to: {}", toEmail);
        String subject = "Flow Deck - Registration Update";
        String htmlContent = buildHtmlContent(
                "Registration Status Update",
                "Hello " + name + ", your registration request for Flow Deck has been reviewed.",
                "<p style='font-size: 16px; color: #EF4444;'>Status: <strong>REJECTED</strong></p>" +
                "<p style='font-size: 14px; color: #374151;'>Reason: " + (reason != null ? reason : "No reason provided") + "</p>"
        );

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    private void sendHtmlEmail(String toEmail, String subject, String htmlBody) {
        log.info("Preparing to send email over SMTP. Sender: {} | Recipient: {} | Subject: {}", fromEmail, toEmail, subject);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            log.info("Dispatching MimeMessage via JavaMailSender.send()...");
            mailSender.send(message);
            log.info("SUCCESS: Email successfully delivered via SMTP to recipient: {}", toEmail);
        } catch (MessagingException e) {
            log.error("SMTP Messaging Error when sending email to '{}'. Root Cause: {}", toEmail, e.getMessage(), e);
            throw new IllegalStateException("SMTP MimeMessage construction failed for " + toEmail + ": " + e.getMessage(), e);
        } catch (MailException e) {
            log.error("SMTP Delivery Failed for recipient '{}'. Root Cause: {}", toEmail, e.getMessage(), e);
            throw new IllegalStateException("SMTP Email delivery failed for recipient '" + toEmail + "': " + e.getMessage() + ". Check spring.mail.username and spring.mail.password in application.properties.", e);
        }
    }

    private String buildHtmlContent(String headerTitle, String bodyMessage, String extraSection) {
        return "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #F3F4F6; margin: 0; padding: 20px;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background-color: #FFFFFF; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +
                "<div style='background-color: #4F46E5; color: #FFFFFF; padding: 20px; text-align: center;'>" +
                "<h2 style='margin: 0; font-size: 24px;'>Flow Deck</h2>" +
                "</div>" +
                "<div style='padding: 30px; color: #1F2937;'>" +
                "<h3 style='color: #111827; margin-top: 0;'>" + headerTitle + "</h3>" +
                "<p style='font-size: 16px; line-height: 1.5;'>" + bodyMessage + "</p>" +
                extraSection +
                "</div>" +
                "<div style='background-color: #F9FAFB; color: #9CA3AF; padding: 15px; text-align: center; font-size: 12px; border-top: 1px solid #E5E7EB;'>" +
                "<p style='margin: 0;'>&copy; 2026 Flow Deck Task Management System. All rights reserved.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
