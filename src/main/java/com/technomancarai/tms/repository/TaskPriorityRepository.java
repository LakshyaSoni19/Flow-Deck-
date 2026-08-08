package com.technomancarai.tms.repository;

import com.technomancarai.tms.entity.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskPriorityRepository extends JpaRepository<TaskPriority, Long> {

    Optional<TaskPriority> findByName(String name);

    boolean existsByName(String name);
}
