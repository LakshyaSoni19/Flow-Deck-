package com.technomancarai.tms.repository;

import com.technomancarai.tms.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    Optional<Role> findFirstByName(String name);

    boolean existsByName(String name);
}
