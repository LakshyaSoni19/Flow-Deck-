package com.technomancarai.tms.controller;

import com.technomancarai.tms.TaskManagementSystemApplication;
import com.technomancarai.tms.dto.request.SendOtpRequest;
import com.technomancarai.tms.dto.request.VerifyOtpRequest;
import com.technomancarai.tms.dto.response.OtpResponse;
import com.technomancarai.tms.entity.OtpVerification;
import com.technomancarai.tms.exception.BadRequestException;
import com.technomancarai.tms.repository.OtpVerificationRepository;
import com.technomancarai.tms.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = TaskManagementSystemApplication.class)
public class OtpFlowIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private OtpVerificationRepository otpVerificationRepository;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private org.springframework.mail.javamail.JavaMailSender mailSender;

    private final String testEmail = "test.otp.flow@example.com";
    private final String testPurpose = "LOGIN";

    @BeforeEach
    void setUp() {
        org.mockito.Mockito.when(mailSender.createMimeMessage()).thenReturn(new jakarta.mail.internet.MimeMessage((jakarta.mail.Session) null));
        otpVerificationRepository.deleteAll();
    }

    @Test
    @DisplayName("Scenario 1: Correct OTP -> Verification Success & isVerified=true")
    void testScenario1_CorrectOtp_Success() {
        authService.sendOtp(SendOtpRequest.builder().email(testEmail).purpose(testPurpose).build());

        OtpVerification savedOtp = otpVerificationRepository
                .findTopByEmailAndPurposeOrderByIdDesc(testEmail, testPurpose)
                .orElseThrow();

        OtpResponse response = authService.verifyOtp(VerifyOtpRequest.builder()
                .email(testEmail)
                .purpose(testPurpose)
                .otp(savedOtp.getOtp())
                .build());

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("OTP verified successfully");

        OtpVerification updatedOtp = otpVerificationRepository.findById(savedOtp.getId()).orElseThrow();
        assertThat(updatedOtp.getIsVerified()).isTrue();
    }

    @Test
    @DisplayName("Scenario 2: Wrong OTP -> Proper Error")
    void testScenario2_WrongOtp_ThrowsException() {
        authService.sendOtp(SendOtpRequest.builder().email(testEmail).purpose(testPurpose).build());

        assertThatThrownBy(() -> authService.verifyOtp(VerifyOtpRequest.builder()
                .email(testEmail)
                .purpose(testPurpose)
                .otp("000000")
                .build()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Invalid OTP code");
    }

    @Test
    @DisplayName("Scenario 3: Expired OTP -> Proper Error")
    void testScenario3_ExpiredOtp_ThrowsException() {
        authService.sendOtp(SendOtpRequest.builder().email(testEmail).purpose(testPurpose).build());

        OtpVerification savedOtp = otpVerificationRepository
                .findTopByEmailAndPurposeOrderByIdDesc(testEmail, testPurpose)
                .orElseThrow();
        savedOtp.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        otpVerificationRepository.save(savedOtp);

        assertThatThrownBy(() -> authService.verifyOtp(VerifyOtpRequest.builder()
                .email(testEmail)
                .purpose(testPurpose)
                .otp(savedOtp.getOtp())
                .build()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("OTP has expired");
    }

    @Test
    @DisplayName("Scenario 4: Already Verified OTP -> Proper Error")
    void testScenario4_AlreadyVerifiedOtp_ThrowsException() {
        authService.sendOtp(SendOtpRequest.builder().email(testEmail).purpose(testPurpose).build());

        OtpVerification savedOtp = otpVerificationRepository
                .findTopByEmailAndPurposeOrderByIdDesc(testEmail, testPurpose)
                .orElseThrow();

        authService.verifyOtp(VerifyOtpRequest.builder()
                .email(testEmail)
                .purpose(testPurpose)
                .otp(savedOtp.getOtp())
                .build());

        assertThatThrownBy(() -> authService.verifyOtp(VerifyOtpRequest.builder()
                .email(testEmail)
                .purpose(testPurpose)
                .otp(savedOtp.getOtp())
                .build()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("OTP has already been verified");
    }

    @Test
    @DisplayName("Scenario 5: Multiple OTPs -> Only Latest OTP Works")
    void testScenario5_MultipleOtps_OnlyLatestWorks() {
        authService.sendOtp(SendOtpRequest.builder().email(testEmail).purpose(testPurpose).build());
        OtpVerification firstOtp = otpVerificationRepository
                .findTopByEmailAndPurposeOrderByIdDesc(testEmail, testPurpose)
                .orElseThrow();

        authService.sendOtp(SendOtpRequest.builder().email(testEmail).purpose(testPurpose).build());
        OtpVerification secondOtp = otpVerificationRepository
                .findTopByEmailAndPurposeOrderByIdDesc(testEmail, testPurpose)
                .orElseThrow();

        assertThat(secondOtp.getId()).isGreaterThan(firstOtp.getId());

        // First OTP should fail (invalidated/expired)
        assertThatThrownBy(() -> authService.verifyOtp(VerifyOtpRequest.builder()
                .email(testEmail)
                .purpose(testPurpose)
                .otp(firstOtp.getOtp())
                .build()))
                .isInstanceOf(BadRequestException.class);

        // Second (latest) OTP should succeed
        OtpResponse response = authService.verifyOtp(VerifyOtpRequest.builder()
                .email(testEmail)
                .purpose(testPurpose)
                .otp(secondOtp.getOtp())
                .build());
        assertThat(response.getMessage()).isEqualTo("OTP verified successfully");
    }

    @Test
    @DisplayName("Scenario 6: Resend OTP -> Old OTP Becomes Invalid")
    void testScenario6_ResendOtp_OldOtpInvalidated() {
        authService.sendOtp(SendOtpRequest.builder().email(testEmail).purpose(testPurpose).build());
        OtpVerification originalOtp = otpVerificationRepository
                .findTopByEmailAndPurposeOrderByIdDesc(testEmail, testPurpose)
                .orElseThrow();

        authService.resendOtp(SendOtpRequest.builder().email(testEmail).purpose(testPurpose).build());

        // Attempting to verify original OTP should fail
        assertThatThrownBy(() -> authService.verifyOtp(VerifyOtpRequest.builder()
                .email(testEmail)
                .purpose(testPurpose)
                .otp(originalOtp.getOtp())
                .build()))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("Scenario 7: Different Purpose OTP Cannot Verify Another Purpose")
    void testScenario7_PurposeMismatch_CannotVerify() {
        authService.sendOtp(SendOtpRequest.builder().email(testEmail).purpose("REGISTRATION").build());
        OtpVerification regOtp = otpVerificationRepository
                .findTopByEmailAndPurposeOrderByIdDesc(testEmail, "REGISTRATION")
                .orElseThrow();

        assertThatThrownBy(() -> authService.verifyOtp(VerifyOtpRequest.builder()
                .email(testEmail)
                .purpose("FORGOT_PASSWORD")
                .otp(regOtp.getOtp())
                .build()))
                .isInstanceOf(Exception.class);
    }
}
