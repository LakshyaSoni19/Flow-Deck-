package com.technomancarai.tms.repository;

import com.technomancarai.tms.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByDepartmentId(Long departmentId);

    boolean existsByDepartmentId(Long departmentId);

    boolean existsByDesignationId(Long designationId);

    List<User> findByIsActiveTrue();

    Page<User> findByApprovalStatus(String approvalStatus, Pageable pageable);

    Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email, Pageable pageable
    );
}
