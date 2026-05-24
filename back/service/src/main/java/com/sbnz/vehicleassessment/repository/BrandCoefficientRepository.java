package com.sbnz.vehicleassessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandCoefficientRepository extends JpaRepository<BrandCoefficientEntity, Long> {
    Optional<BrandCoefficientEntity> findByMarka(String marka);
}
