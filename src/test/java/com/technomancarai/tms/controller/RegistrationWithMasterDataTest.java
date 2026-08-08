package com.technomancarai.tms.controller;

import com.technomancarai.tms.TaskManagementSystemApplication;
import com.technomancarai.tms.dto.request.RegisterRequest;
import com.technomancarai.tms.dto.response.RegisterResponse;
import com.technomancarai.tms.service.AuthService;
import com.technomancarai.tms.dto.request.VerifyRegistrationOtpRequest;
import com.technomancarai.tms.dto.response.ApiResponse;
import com.technomancarai.tms.entity.OtpVerification;
import com.technomancarai.tms.repository.OtpVerificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Session;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

@SpringBootTest(classes = TaskManagementSystemApplication.class)
@Transactional
public class RegistrationWithMasterDataTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private OtpVerificationRepository otpVerificationRepository;

    @MockitoBean
    private JavaMailSender mailSender;

    @BeforeEach
    public void setUp() {
        Mockito.when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @Test
    public void testUserRegistrationWithMasterDataIds() {
        String testEmail = "testuser_" + System.currentTimeMillis() + "@example.com";

        RegisterRequest request = RegisterRequest.builder()
                .firstName("Lakshya")
                .lastName("Soni")
                .email(testEmail)
                .password("password123")
                .mobile("9876543210")
                .gender("Male")
                .cityId(1L)        // Jaipur (from master-data.sql)
                .departmentId(1L)  // Engineering (from master-data.sql)
                .designationId(1L) // Software Engineer (from master-data.sql)
                .build();

        // Step 1: Send registration OTP
        ApiResponse<Void> sendOtpResponse = authService.sendRegistrationOtp(request);
        assertThat(sendOtpResponse).isNotNull();
        assertThat(sendOtpResponse.isSuccess()).isTrue();

        // Retrieve generated OTP from repository
        OtpVerification otpVerification = otpVerificationRepository
                .findFirstByEmailAndPurposeOrderByIdDesc(testEmail, "REGISTRATION")
        .orElseThrow(() -> new AssertionError("OTP not generated"));
        assertThat(otpVerification.getOtp()).isNotNull();

        // Step 2: Verify registration OTP
        VerifyRegistrationOtpRequest verifyRequest = VerifyRegistrationOtpRequest.builder()
                .email(testEmail)
                .otp(otpVerification.getOtp())
                .build();

        RegisterResponse response = authService.verifyRegistrationOtp(verifyRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getEmail()).isEqualTo(testEmail);
        assertThat(response.getMessage()).startsWith("Registration successful.");
    }
}
