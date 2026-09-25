package com.technomancarai.tms.controller;

import com.technomancarai.tms.dto.request.LoginRequest;
import com.technomancarai.tms.dto.request.RegisterRequest;
import com.technomancarai.tms.dto.request.ResetPasswordRequest;
import com.technomancarai.tms.dto.request.SendOtpRequest;
import com.technomancarai.tms.dto.request.VerifyOtpRequest;
import com.technomancarai.tms.dto.request.VerifyRegistrationOtpRequest;
import com.technomancarai.tms.dto.response.LoginResponse;
import com.technomancarai.tms.dto.response.OtpResponse;
import com.technomancarai.tms.dto.response.RegisterResponse;
import com.technomancarai.tms.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "01. Authentication", description = "User registration, login, OTP verification, and password management endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
        summary = "01. Register User",
        description = "Creates a new user account in the system without requiring OTP verification."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = com.technomancarai.tms.dto.response.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload or duplicate user data")
    })
    public ResponseEntity<com.technomancarai.tms.dto.response.ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.technomancarai.tms.dto.response.ApiResponse.success(response, "User registered successfully"));
    }

    @PostMapping("/send-registration-otp")
    @Operation(
        summary = "02. Send Registration OTP",
        description = "Initiates user registration by validating request details, checking duplicate email, generating a secure 6-digit OTP, temporarily storing registration payload, and sending OTP to user email."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Registration details required to generate OTP",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                name = "Registration OTP Request Example",
                value = "{\n" +
                        "  \"firstName\": \"Lakshya\",\n" +
                        "  \"lastName\": \"Soni\",\n" +
                        "  \"email\": \"lakshya.soni@example.com\",\n" +
                        "  \"password\": \"Password@123\",\n" +
                        "  \"mobile\": \"9876543210\",\n" +
                        "  \"gender\": \"Male\",\n" +
                        "  \"dob\": \"1998-05-15\",\n" +
                        "  \"address\": \"123 Tech Park, MG Road\",\n" +
                        "  \"cityId\": 1,\n" +
                        "  \"departmentId\": 1,\n" +
                        "  \"designationId\": 1\n" +
                        "}"
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Registration OTP sent successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input or email already exists")
    })
    public ResponseEntity<com.technomancarai.tms.dto.response.ApiResponse<Void>> sendRegistrationOtp(@Valid @RequestBody RegisterRequest request) {
        com.technomancarai.tms.dto.response.ApiResponse<Void> response = authService.sendRegistrationOtp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-registration-otp")
    @Operation(
        summary = "03. Verify Registration OTP",
        description = "Validates the OTP sent to user email. Upon successful verification, creates the user account in system, encodes password using BCrypt, links master data entities, sends welcome email, and invalidates temporary registration data."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Email and OTP code for registration completion",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                name = "Verify Registration OTP Request Example",
                value = "{\n" +
                        "  \"email\": \"lakshya.soni@example.com\",\n" +
                        "  \"otp\": \"123456\"\n" +
                        "}"
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Registration OTP verified and user account created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired OTP")
    })
    public ResponseEntity<com.technomancarai.tms.dto.response.ApiResponse<RegisterResponse>> verifyRegistrationOtp(@Valid @RequestBody VerifyRegistrationOtpRequest request) {
        RegisterResponse response = authService.verifyRegistrationOtp(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.technomancarai.tms.dto.response.ApiResponse.success(response, "User registered successfully"));
    }

    @PostMapping("/login")
    @Operation(summary = "04. Login", description = "Authenticates user credentials and returns authentication token.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials or user unapproved/inactive")
    })
    public ResponseEntity<com.technomancarai.tms.dto.response.ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(com.technomancarai.tms.dto.response.ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "05. Forgot Password", description = "Initiates password reset process by issuing an OTP to registered email.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password reset OTP sent successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User email not found")
    })
    public ResponseEntity<com.technomancarai.tms.dto.response.ApiResponse<OtpResponse>> forgotPassword(@Valid @RequestBody SendOtpRequest request) {
        OtpResponse response = authService.forgotPassword(request);
        return ResponseEntity.ok(com.technomancarai.tms.dto.response.ApiResponse.success(response, "Password reset OTP sent successfully"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "06. Reset Password", description = "Resets user password using verified OTP.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password reset successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid OTP or request data")
    })
    public ResponseEntity<com.technomancarai.tms.dto.response.ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        com.technomancarai.tms.dto.response.ApiResponse<String> response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-otp")
    @Operation(summary = "07. Send OTP", description = "Generates and sends an OTP for the specified email and purpose.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP sent successfully")
    })
    public ResponseEntity<com.technomancarai.tms.dto.response.ApiResponse<OtpResponse>> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        OtpResponse response = authService.sendOtp(request);
        return ResponseEntity.ok(com.technomancarai.tms.dto.response.ApiResponse.success(response, "OTP sent successfully"));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "08. Verify OTP", description = "Validates the OTP code provided for an email and purpose.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP verified successfully")
    })
    public ResponseEntity<com.technomancarai.tms.dto.response.ApiResponse<OtpResponse>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        OtpResponse response = authService.verifyOtp(request);
        return ResponseEntity.ok(com.technomancarai.tms.dto.response.ApiResponse.success(response, "OTP verified successfully"));
    }

    @PostMapping("/resend-otp")
    @Operation(summary = "09. Resend OTP", description = "Resends a fresh OTP for the specified email and purpose.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP resent successfully")
    })
    public ResponseEntity<com.technomancarai.tms.dto.response.ApiResponse<OtpResponse>> resendOtp(@Valid @RequestBody SendOtpRequest request) {
        OtpResponse response = authService.resendOtp(request);
        return ResponseEntity.ok(com.technomancarai.tms.dto.response.ApiResponse.success(response, "OTP resent successfully"));
    }
}
