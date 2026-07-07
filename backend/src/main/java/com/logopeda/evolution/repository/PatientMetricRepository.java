package com.logopeda.evolution.repository;

import com.logopeda.evolution.model.PatientMetric;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientMetricRepository extends JpaRepository<PatientMetric, String> {

    Optional<PatientMetric> findByIdAndClinicId(String id, String clinicId);

    List<PatientMetric> findByPatientIdAndClinicIdOrderByCreatedAtDesc(String patientId, String clinicId);

    List<PatientMetric> findByPatientIdAndClinicIdAndVisibleToFamilyTrueOrderByCreatedAtDesc(
            String patientId, String clinicId);
}
