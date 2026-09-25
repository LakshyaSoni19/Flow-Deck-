package com.technomancarai.tms.repository;

import com.technomancarai.tms.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findByStateId(Long stateId);

    Optional<City> findByNameAndStateId(String name, Long stateId);

    boolean existsByNameAndStateId(String name, Long stateId);
}
