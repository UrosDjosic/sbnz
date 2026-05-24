package com.sbnz.vehicleassessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepairPolicyRepository extends JpaRepository<RepairPolicyEntity, Long> {
    Optional<RepairPolicyEntity> findByDeo(String deo);
}
