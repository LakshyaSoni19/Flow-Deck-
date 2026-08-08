package com.technomancarai.tms.repository;

import com.technomancarai.tms.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findByEmailAndPurpose(String email, String purpose);

    Optional<OtpVerification> findFirstByEmailAndPurposeOrderByIdDesc(String email, String purpose);

    Optional<OtpVerification> findTopByEmailAndPurposeOrderByExpiresAtDesc(String email, String purpose);

    Optional<OtpVerification> findTopByEmailAndPurposeOrderByIdDesc(String email, String purpose);

    List<OtpVerification> findByEmailAndPurposeAndIsVerifiedFalse(String email, String purpose);

    boolean existsByEmailAndPurposeAndIsVerifiedTrue(String email, String purpose);
}
