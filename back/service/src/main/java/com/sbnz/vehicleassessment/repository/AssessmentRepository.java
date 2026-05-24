package com.sbnz.vehicleassessment.repository;

import com.sbnz.vehicleassessment.model.enums.TipOdluke;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<AssessmentRecord, Long> {
    List<AssessmentRecord> findByOdluka(TipOdluke odluka);
    List<AssessmentRecord> findByMarka(String marka);
}
