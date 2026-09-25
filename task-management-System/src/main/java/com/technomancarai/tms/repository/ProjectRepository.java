package com.technomancarai.tms.repository;

import com.technomancarai.tms.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByProjectCode(String projectCode);

    boolean existsByProjectCode(String projectCode);

    List<Project> findByCreatedByUserId(Long userId);

    List<Project> findByStatus(String status);

    Page<Project> findByManagerId(Long managerId, Pageable pageable);

    Page<Project> findByManagerEmail(String email, Pageable pageable);
}
