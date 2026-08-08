package com.technomancarai.tms.repository;

import com.technomancarai.tms.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

    Optional<TaskStatus> findByName(String name);

    boolean existsByName(String name);
}
