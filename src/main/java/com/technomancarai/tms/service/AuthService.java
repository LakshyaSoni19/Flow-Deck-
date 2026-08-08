package com.technomancarai.tms.service;

import com.technomancarai.tms.dto.request.LoginRequest;
import com.technomancarai.tms.dto.request.RegisterRequest;
import com.technomancarai.tms.dto.request.ResetPasswordRequest;
import com.technomancarai.tms.dto.request.SendOtpRequest;
import com.technomancarai.tms.dto.request.VerifyOtpRequest;
import com.technomancarai.tms.dto.response.ApiResponse;
import com.technomancarai.tms.dto.response.LoginResponse;
import com.technomancarai.tms.dto.response.OtpResponse;
import com.technomancarai.tms.dto.response.RegisterResponse;

import com.technomancarai.tms.dto.request.VerifyRegistrationOtpRequest;

public interface AuthService {

    ApiResponse<Void> sendRegistrationOtp(RegisterRequest request);

    RegisterResponse verifyRegistrationOtp(VerifyRegistrationOtpRequest request);

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    OtpResponse sendOtp(SendOtpRequest request);

    OtpResponse verifyOtp(VerifyOtpRequest request);

    OtpResponse resendOtp(SendOtpRequest request);

    OtpResponse forgotPassword(SendOtpRequest request);

    ApiResponse<String> resetPassword(ResetPasswordRequest request);
}
