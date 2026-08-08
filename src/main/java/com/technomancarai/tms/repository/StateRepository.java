package com.technomancarai.tms.repository;

import com.technomancarai.tms.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {

    List<State> findByCountryId(Long countryId);

    Optional<State> findByNameAndCountryId(String name, Long countryId);

    boolean existsByNameAndCountryId(String name, Long countryId);
}
