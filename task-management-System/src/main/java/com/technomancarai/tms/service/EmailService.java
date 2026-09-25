package com.technomancarai.tms.service;

public interface EmailService {

    void sendOtpEmail(String toEmail, String otp, String purpose);

    void sendWelcomeEmail(String toEmail, String name);

    void sendPasswordResetEmail(String toEmail, String name);

    void sendApprovalEmail(String toEmail, String name, String roleName);

    void sendRejectionEmail(String toEmail, String name, String reason);
}
