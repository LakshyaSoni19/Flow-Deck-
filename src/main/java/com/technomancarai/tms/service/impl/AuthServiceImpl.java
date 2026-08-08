package com.technomancarai.tms.service.impl;

import com.technomancarai.tms.dto.request.LoginRequest;
import com.technomancarai.tms.dto.request.RegisterRequest;
import com.technomancarai.tms.dto.request.ResetPasswordRequest;
import com.technomancarai.tms.dto.request.SendOtpRequest;
import com.technomancarai.tms.dto.request.VerifyOtpRequest;
import com.technomancarai.tms.dto.response.ApiResponse;
import com.technomancarai.tms.dto.response.LoginResponse;
import com.technomancarai.tms.dto.response.OtpResponse;
import com.technomancarai.tms.dto.response.RegisterResponse;
import com.technomancarai.tms.dto.response.UserResponse;
import com.technomancarai.tms.entity.City;
import com.technomancarai.tms.entity.Department;
import com.technomancarai.tms.entity.Designation;
import com.technomancarai.tms.entity.OtpVerification;
import com.technomancarai.tms.entity.User;
import com.technomancarai.tms.exception.BadRequestException;
import com.technomancarai.tms.exception.DuplicateResourceException;
import com.technomancarai.tms.exception.ResourceNotFoundException;
import com.technomancarai.tms.exception.UnauthorizedException;
import com.technomancarai.tms.mapper.OtpVerificationMapper;
import com.technomancarai.tms.mapper.UserMapper;
import com.technomancarai.tms.repository.CityRepository;
import com.technomancarai.tms.repository.DepartmentRepository;
import com.technomancarai.tms.repository.DesignationRepository;
import com.technomancarai.tms.repository.OtpVerificationRepository;
import com.technomancarai.tms.repository.UserRepository;
import com.technomancarai.tms.service.AuthService;
import com.technomancarai.tms.service.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technomancarai.tms.dto.request.VerifyRegistrationOtpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import com.technomancarai.tms.entity.Role;
import com.technomancarai.tms.entity.UserRole;
import com.technomancarai.tms.repository.RoleRepository;
import com.technomancarai.tms.repository.UserRoleRepository;
import com.technomancarai.tms.security.JwtTokenProvider;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final CityRepository cityRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final UserMapper userMapper;
    private final OtpVerificationMapper otpVerificationMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final SecureRandom secureRandom = new SecureRandom();

    private String normalizeEmail(String email) {
        return email != null ? email.trim().toLowerCase() : null;
    }

    private String normalizePurpose(String purpose) {
        return purpose != null ? purpose.trim().toUpperCase() : null;
    }

    private void invalidatePreviousOtps(String email, String purpose) {
        List<OtpVerification> oldOtps = otpVerificationRepository.findByEmailAndPurposeAndIsVerifiedFalse(email, purpose);
        if (!oldOtps.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (OtpVerification oldOtp : oldOtps) {
                oldOtp.setExpiresAt(now);
            }
            otpVerificationRepository.saveAll(oldOtps);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> sendRegistrationOtp(RegisterRequest request) {
        String email = normalizeEmail(request.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("User already exists with email: " + email);
        }

        invalidatePreviousOtps(email, "REGISTRATION");

        String generatedOtp = String.format("%06d", secureRandom.nextInt(1000000));
        request.setEmail(email);

        String registrationDataJson;
        try {
            registrationDataJson = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Failed to process registration request data");
        }

        OtpVerification otpVerification = OtpVerification.builder()
                .email(email)
                .otp(generatedOtp)
                .purpose("REGISTRATION")
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .isVerified(false)
                .registrationData(registrationDataJson)
                .build();

        otpVerificationRepository.save(otpVerification);

        emailService.sendOtpEmail(email, generatedOtp, "REGISTRATION");

        return ApiResponse.success("OTP sent successfully.");
    }

    @Override
    @Transactional
    public RegisterResponse verifyRegistrationOtp(VerifyRegistrationOtpRequest request) {
        String email = normalizeEmail(request.getEmail());
        String inputOtp = request.getOtp() != null ? request.getOtp().trim() : "";

        OtpVerification otpVerification = otpVerificationRepository
                .findFirstByEmailAndPurposeOrderByIdDesc(email, "REGISTRATION")
                .orElseThrow(() -> new BadRequestException("No registration OTP request found for email: " + email));

        if (Boolean.TRUE.equals(otpVerification.getIsVerified())) {
            throw new BadRequestException("OTP has already been verified");
        }

        if (otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired");
        }

        if (!otpVerification.getOtp().equals(inputOtp)) {
            throw new BadRequestException("Invalid OTP code");
        }

        if (otpVerification.getRegistrationData() == null || otpVerification.getRegistrationData().isBlank()) {
            throw new BadRequestException("Registration request data not found or already processed");
        }

        RegisterRequest regRequest;
        try {
            regRequest = objectMapper.readValue(otpVerification.getRegistrationData(), RegisterRequest.class);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Failed to parse registration request data");
        }

        if (userRepository.existsByEmail(regRequest.getEmail())) {
            throw new DuplicateResourceException("User already exists with email: " + regRequest.getEmail());
        }

        otpVerification.setIsVerified(true);

        // DEVELOPMENT MODE
        // Enable Admin Approval before production deployment.
        User user = userMapper.toUser(regRequest);
        user.setPassword(passwordEncoder.encode(regRequest.getPassword()));
        user.setIsActive(true);
        user.setApprovalStatus("APPROVED");

        if (regRequest.getCityId() != null) {
            City city = cityRepository.findById(regRequest.getCityId())
                    .orElseThrow(() -> new ResourceNotFoundException("City not found with ID: " + regRequest.getCityId()));
            user.setCity(city);
        }

        if (regRequest.getDepartmentId() != null) {
            Department department = departmentRepository.findById(regRequest.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + regRequest.getDepartmentId()));
            user.setDepartment(department);
        }

        if (regRequest.getDesignationId() != null) {
            Designation designation = designationRepository.findById(regRequest.getDesignationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Designation not found with ID: " + regRequest.getDesignationId()));
            user.setDesignation(designation);
        }

        User savedUser = userRepository.save(user);

        // DEVELOPMENT MODE
        // Enable Admin Approval before production deployment.
        // Automatically assign ROLE_EMPLOYEE to every newly registered user.
        Role employeeRole = roleRepository.findFirstByName("ROLE_EMPLOYEE")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_EMPLOYEE").build()));

        if (!userRoleRepository.existsByUserIdAndRoleId(savedUser.getId(), employeeRole.getId())) {
            UserRole userRole = UserRole.builder()
                    .user(savedUser)
                    .role(employeeRole)
                    .build();
            userRole.setIsActive(true);
            userRoleRepository.save(userRole);
        }

        otpVerification.setRegistrationData(null);
        otpVerificationRepository.save(otpVerification);

        return RegisterResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .message("Registration successful.")
                .build();
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User already exists with email: " + request.getEmail());
        }

        // DEVELOPMENT MODE
        // Enable Admin Approval before production deployment.
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);
        user.setApprovalStatus("APPROVED");

        if (request.getCityId() != null) {
            City city = cityRepository.findById(request.getCityId())
                    .orElseThrow(() -> new ResourceNotFoundException("City not found with ID: " + request.getCityId()));
            user.setCity(city);
        }

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + request.getDepartmentId()));
            user.setDepartment(department);
        }

        if (request.getDesignationId() != null) {
            Designation designation = designationRepository.findById(request.getDesignationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Designation not found with ID: " + request.getDesignationId()));
            user.setDesignation(designation);
        }

        User savedUser = userRepository.save(user);

        // DEVELOPMENT MODE
        // Enable Admin Approval before production deployment.
        // Automatically assign ROLE_EMPLOYEE to every newly registered user.
        Role employeeRole = roleRepository.findFirstByName("ROLE_EMPLOYEE")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_EMPLOYEE").build()));

        if (!userRoleRepository.existsByUserIdAndRoleId(savedUser.getId(), employeeRole.getId())) {
            UserRole userRole = UserRole.builder()
                    .user(savedUser)
                    .role(employeeRole)
                    .build();
            userRole.setIsActive(true);
            userRoleRepository.save(userRole);
        }

        return RegisterResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .message("Registration successful.")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        // DEVELOPMENT MODE
        // Enable Admin Approval before production deployment.
        // Approval status validation (PENDING / REJECTED) skipped for dev mode.

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new UnauthorizedException("User account is inactive");
        }

        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        List<String> roleNames = userRoles.stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toList());

        if (roleNames.isEmpty()) {
            roleNames.add("ROLE_USER");
        }

        String token = jwtTokenProvider.generateTokenForUser(user.getEmail(), roleNames);
        UserResponse userResponse = userMapper.toUserResponseWithRoles(user, roleNames);

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userResponse)
                .build();
    }

    @Override
    @Transactional
    public OtpResponse sendOtp(SendOtpRequest request) {
        String email = normalizeEmail(request.getEmail());
        String purpose = normalizePurpose(request.getPurpose());

        invalidatePreviousOtps(email, purpose);

        String generatedOtp = String.format("%06d", secureRandom.nextInt(1000000));

        OtpVerification otpVerification = OtpVerification.builder()
                .email(email)
                .otp(generatedOtp)
                .purpose(purpose)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .isVerified(false)
                .build();

        OtpVerification savedOtp = otpVerificationRepository.save(otpVerification);

        emailService.sendOtpEmail(email, generatedOtp, purpose);

        return otpVerificationMapper.toOtpResponse(savedOtp, "OTP sent successfully to " + email);
    }

    @Override
    @Transactional
    public OtpResponse verifyOtp(VerifyOtpRequest request) {
        String email = normalizeEmail(request.getEmail());
        String purpose = normalizePurpose(request.getPurpose());
        String inputOtp = request.getOtp() != null ? request.getOtp().trim() : "";

        OtpVerification otpVerification = otpVerificationRepository
                .findTopByEmailAndPurposeOrderByIdDesc(email, purpose)
                .orElseThrow(() -> new ResourceNotFoundException("No OTP request found for email: " + email));

        if (Boolean.TRUE.equals(otpVerification.getIsVerified())) {
            throw new BadRequestException("OTP has already been verified");
        }

        if (otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired");
        }

        if (!otpVerification.getOtp().equals(inputOtp)) {
            throw new BadRequestException("Invalid OTP code");
        }

        otpVerification.setIsVerified(true);
        OtpVerification verifiedOtp = otpVerificationRepository.save(otpVerification);

        return otpVerificationMapper.toOtpResponse(verifiedOtp, "OTP verified successfully");
    }

    @Override
    @Transactional
    public OtpResponse resendOtp(SendOtpRequest request) {
        return sendOtp(request);
    }

    @Override
    @Transactional
    public OtpResponse forgotPassword(SendOtpRequest request) {
        String email = normalizeEmail(request.getEmail());
        if (!userRepository.existsByEmail(email)) {
            throw new ResourceNotFoundException("No user registered with email: " + email);
        }

        SendOtpRequest forgotPasswordOtpRequest = SendOtpRequest.builder()
                .email(email)
                .purpose("FORGOT_PASSWORD")
                .build();

        return sendOtp(forgotPasswordOtpRequest);
    }

    @Override
    @Transactional
    public ApiResponse<String> resetPassword(ResetPasswordRequest request) {
        String email = normalizeEmail(request.getEmail());
        String inputOtp = request.getOtp() != null ? request.getOtp().trim() : "";

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        OtpVerification otpVerification = otpVerificationRepository
                .findTopByEmailAndPurposeOrderByIdDesc(email, "FORGOT_PASSWORD")
                .orElseThrow(() -> new BadRequestException("OTP verification required before resetting password"));

        if (!Boolean.TRUE.equals(otpVerification.getIsVerified())) {
            throw new BadRequestException("OTP must be verified before password reset");
        }

        if (!otpVerification.getOtp().equals(inputOtp)) {
            throw new BadRequestException("Invalid OTP code provided for password reset");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        emailService.sendPasswordResetEmail(email, user.getFirstName());

        return ApiResponse.success("Password reset successfully", "Password updated for user: " + email);
    }
}
