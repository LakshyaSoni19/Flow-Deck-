package com.technomancarai.tms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "otp_verification")
public class OtpVerification extends BaseEntity {

    @Column(name = "email", nullable = false, length = 45)
    private String email;

    @Column(name = "otp", length = 10)
    private String otp;

    @Column(name = "purpose", length = 45)
    private String purpose;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    @Column(name = "registration_data", columnDefinition = "TEXT")
    private String registrationData;
}
